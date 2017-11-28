package com.jzg.erp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jzg.erp.R;
import com.jzg.erp.adapter.TaskAdapter;
import com.jzg.erp.app.JzgApp;
import com.jzg.erp.base.NewBaseFragment;
import com.jzg.erp.event.TaskCountEvent;
import com.jzg.erp.global.Constant;
import com.jzg.erp.model.WaitingItemWrapper;
import com.jzg.erp.presenter.WaitItemPresenter;
import com.jzg.erp.ui.activity.TaskDetailActivity;
import com.jzg.erp.utils.LogUtil;
import com.jzg.erp.utils.MD5Utils;
import com.jzg.erp.utils.UIUtils;
import com.jzg.erp.view.IWaitingItems;
import com.jzg.erp.widget.EmptyView;
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

@SuppressLint("ValidFragment")
public class TaskFragment extends NewBaseFragment implements OnItemClickListener, XRecyclerView.LoadingListener ,IWaitingItems{

    @Bind(R.id.xrv)
    XRecyclerView xrv;
    private TaskAdapter adapter;
    private WaitItemPresenter presenter;
    private List<WaitingItemWrapper.WaitingItem> totalList;
    private int type;
    private int pageIndex = 1;
    public TaskFragment(int type) {
        this.type = type;
    }


    @Override
    protected void setView() {
        presenter = new WaitItemPresenter(this);
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, rootView);
        xrv.setLayoutManager(new LinearLayoutManager(context));
        xrv.setRefreshProgressStyle(ProgressStyle.SysProgress);
        xrv.setLoadingMoreProgressStyle(ProgressStyle.SysProgress);
        xrv.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.VERTICAL, 1,R.color.common_gray_line));
        xrv.setLoadingListener(this);
        EmptyView emptyView = null;
        if(type==0)
            emptyView = new EmptyView(context, "暂无今日待办事项");
        else
            emptyView = new EmptyView(context, "暂无待办事项");
        ((ViewGroup) xrv.getParent()).addView(emptyView);
        xrv.setEmptyView(emptyView);
        adapter = new TaskAdapter(context, R.layout.item_task, totalList,xrv);
        xrv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    protected void initData() {
        totalList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
}

    @Override
    public void showError(String error) {
        super.showError(error);
       stopLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(ViewGroup parent, View view, Object o, int position) {
        WaitingItemWrapper.WaitingItem item = totalList.get(position);
        Intent intent  = new Intent(getActivity(),TaskDetailActivity.class);
        intent.putExtra("item",item);
        jump(intent);
    }

    @Override
    public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
        return false;
    }

    @Override
    public void onRefresh() {
        pageIndex=1;
        getData();
    }

    private void getData(){
        Map<String,String> params = new HashMap<>();
        if(type==0){
            params.put("op","today");
        }else{
            params.put("op","all");
        }
        params.put("pageIndex",String.valueOf(pageIndex));
        params.put("pageSize",String.valueOf(Constant.PAGECOUNT));
        params.put("SalesId", String.valueOf(JzgApp.getUser().getUserID()));
        Map<String, Object> map = new HashMap<>();
        map.putAll(params);
        String sign = MD5Utils.getMD5Sign(map);
        params.put("sign", sign);
        LogUtil.e(TAG, UIUtils.getUrl(params));
        presenter.loadData(params);
    }

    @Override
    public void onLoadMore() {
        if(totalList.size()==0){
            xrv.loadMoreComplete();
            return;
        }
        if(totalList.size()>0 && totalList.size()% Constant.PAGECOUNT!=0){
            xrv.loadMoreComplete();
            return;
        }
        pageIndex++;
        getData();
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
    public void onSucceed(WaitingItemWrapper wrapper) {
        stopLoading();
        List<WaitingItemWrapper.WaitingItem> resultList = wrapper.getData();
        int allSum = wrapper.getAllSum();
        int todaySum = wrapper.getTodaySum();
        TaskCountEvent event = new TaskCountEvent(0,allSum,todaySum);
        EventBus.getDefault().post(event);
        if(resultList!=null && resultList.size()>0){
           if(pageIndex==1){
               totalList.clear();
           }
            totalList.addAll(resultList);
            adapter.notifyDataSetChanged();
        }
    }
}
