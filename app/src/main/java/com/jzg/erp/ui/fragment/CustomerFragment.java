package com.jzg.erp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jzg.erp.R;
import com.jzg.erp.adapter.CustomerAdapter;
import com.jzg.erp.app.JzgApp;
import com.jzg.erp.base.BaseActivity;
import com.jzg.erp.base.NewBaseFragment;
import com.jzg.erp.global.Constant;
import com.jzg.erp.model.CustomerDetail;
import com.jzg.erp.model.CustomerInfo;
import com.jzg.erp.presenter.CustomerPresenter;
import com.jzg.erp.ui.activity.CustomerDetailActivity;
import com.jzg.erp.view.ICustomer;
import com.jzg.erp.widget.EmptyView;
import com.jzg.erp.widget.ErrorView;
import com.jzg.erp.widget.RecycleViewDivider;
import com.jzg.erp.widget.XRecyclerView;
import com.zhy.base.adapter.recyclerview.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 客户界面
 */
public class CustomerFragment extends NewBaseFragment implements OnItemClickListener, ICustomer, XRecyclerView.LoadingListener {
    @Bind(R.id.customer_xrc)
    XRecyclerView xrv;
    @Bind(R.id.err_layout)
    ErrorView errLayout;
    private CustomerAdapter adapter;
    private String status;
    private CustomerPresenter presenter;
    private List<CustomerInfo.CustomerListEntity> customerInfos;
    private int pageIndex = 1;
    private View rootView;

    public CustomerFragment() {
    }

    @SuppressLint("ValidFragment")
    public CustomerFragment(String status) {
        this.status = status;
    }


    @Override
    protected void initData() {
        customerInfos = new ArrayList<>();
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_customer1, container, false);
            ButterKnife.bind(this, rootView);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            xrv.setLayoutManager(linearLayoutManager);
            xrv.setRefreshProgressStyle(ProgressStyle.SysProgress);
            xrv.setLoadingMoreProgressStyle(ProgressStyle.BallScaleMultiple);
            //添加空界面
            EmptyView emptyView = new EmptyView(context, "暂无" + status + "客户");
            ((ViewGroup) xrv.getParent()).addView(emptyView);
            xrv.setEmptyView(emptyView);
            //添加错误界面监听
            errLayout.setOnErrorListener(new ErrorView.OnErrorListener() {
                @Override
                public void onError() {
                    pageIndex = 1;
                    presenter.getCustomerInfo(status, pageIndex);
                }
            });
            xrv.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.VERTICAL, 6, R.color.common_gray_bg));
            xrv.setPullRefreshEnabled(true);
            xrv.setLoadingMoreEnabled(true);
            xrv.setLoadingListener(this);
            adapter = new CustomerAdapter(context, R.layout.item_customer, customerInfos, xrv);
            adapter.setOnItemClickListener(this);
            xrv.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    protected void setView() {

    }


    @Override
    protected void setTextLeft(int res) {
        super.setTextLeft(res);
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter = new CustomerPresenter(this);
        ((BaseActivity)getActivity()).showDialog();
        presenter.getCustomerInfo(status, pageIndex);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onItemClick(ViewGroup parent, View view, Object o, int position) {
        presenter.getCustomerDetail(getCustomerParams(customerInfos.get(position).getCustomerID() + ""));
    }

    @Override
    public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
        return false;
    }

    @Override
    public void succeedResult(CustomerInfo customerInfo, int pageIdex) {
        if (xrv == null) {
            xrv = (XRecyclerView) rootView.findViewById(R.id.customer_xrc);
            errLayout = (ErrorView) rootView.findViewById(R.id.err_layout);
        }
        if (pageIdex == 1) {
            EventBus.getDefault().post(customerInfo);
            xrv.refreshComplete();
            customerInfos.clear();
        } else {
            xrv.loadMoreComplete();
        }
        if (null != customerInfo) {
            customerInfos.addAll(customerInfo.getCustomerList());
            if (errLayout.getVisibility() == View.VISIBLE) {
                errLayout.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void showCustomerDetail(CustomerDetail customer) {
        Intent intent = new Intent(context, CustomerDetailActivity.class);
        intent.putExtra("detail", (Parcelable) customer);
        startActivity(intent);
    }

    private void stopLoading(){
        if(xrv==null)
            return;
        if(pageIndex==1)
            xrv.refreshComplete();
        else
            xrv.loadMoreComplete();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLoading();
    }

    @Override
    public void showError(String error) {
        super.showError(error);
        stopLoading();
        if (xrv == null) {
            xrv = (XRecyclerView) rootView.findViewById(R.id.customer_xrc);
            errLayout = (ErrorView) rootView.findViewById(R.id.err_layout);
        }
        xrv.setVisibility(View.GONE);
        errLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取查询客户信息详情需要的参数集合
     *
     * @return 参数集合
     */
    private Map<String, String> getCustomerParams(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("Op", "getCustomerInfo");
        params.put("customerId", id);
        params.put("userID", String.valueOf(JzgApp.getUser().getUserID()));
        return params;
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        presenter.getCustomerInfo(status, pageIndex);
    }

    @Override
    public void onLoadMore() {
        if(customerInfos.size()==0){
            xrv.loadMoreComplete();
            return;
        }
        if(customerInfos.size()>0 && customerInfos.size()% Constant.PAGECOUNT!=0){
            xrv.loadMoreComplete();
            return;
        }
        pageIndex++;
        presenter.getCustomerInfo(status, pageIndex);
    }
}
