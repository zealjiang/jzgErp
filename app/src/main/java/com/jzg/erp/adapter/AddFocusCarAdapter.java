package com.jzg.erp.adapter;/**
 * author: gcc
 * date: 2016/7/4 10:42
 * email:
 */

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jzg.erp.R;
import com.jzg.erp.global.Constant;
import com.jzg.erp.model.CarSourceData;
import com.jzg.erp.utils.ScreenUtils;
import com.jzg.erp.widget.CustomTextView;
import com.jzg.erp.widget.XRecyclerView;
import com.zhy.base.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: guochen
 * date: 2016/7/4 10:42
 * email: 
 */
public class AddFocusCarAdapter extends CommonAdapter<CarSourceData.DataBean>{
    public Map<Integer, Boolean> maps;
    private Context context;
    private OnCheckedCountChangedListener listener;

    public AddFocusCarAdapter(Context context, int layoutId, List<CarSourceData.DataBean> datas, XRecyclerView xrv,OnCheckedCountChangedListener listener) {
        super(context, layoutId, datas, xrv);
        this.mDatas = datas;
        maps = new HashMap<>();
        this.context = context;
        this.listener = listener;

    }

    public Map<Integer, Boolean> getMap(){
        return maps;
    }
    public ArrayList<Integer> getDataMap() {
        ArrayList<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : maps.entrySet()) {
            if (entry.getValue()) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public void updateView(List<CarSourceData.DataBean> datas) {
        for (int i = 0; i < datas.size(); i++) {
            if (i >= maps.size()) {
                maps.put(i, false);
            }
        }
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public static interface OnCheckedCountChangedListener{
        void onSelectedCountChanged();
    }

    @Override
    public void convert(final ViewHolder holder, CarSourceData.DataBean dataBean) {

        CheckBox checkBox = holder.getView(R.id.radio);
        checkBox.setChecked(maps.get(holder.getAdapterPosition()-1));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maps.put(holder.getAdapterPosition() - 1, isChecked);
               listener.onSelectedCountChanged();
            }
        });
        holder.setVisible(R.id.sv_img,false);
        CustomTextView customTextView1 = holder.getView(R.id.sv_status);
        CustomTextView customTextView2 = holder.getView(R.id.sv_status1);
        CustomTextView customTextView3 = holder.getView(R.id.sv_status2);
        TextView tv_money = holder.getView(R.id.tv_money);
        holder.setText(R.id.tv_brand, dataBean.getFullName());
        String regdateStr = dataBean.getRegdateStr();
        if(!regdateStr.startsWith("2"))
            regdateStr = "";
        holder.setText(R.id.tv_time, regdateStr);
        if(TextUtils.isEmpty(regdateStr)){
            holder.setVisible(R.id.tv_time,false);
        }else{
            holder.setVisible(R.id.tv_time,true);
        }
        holder.setText(R.id.tv_mileage,dataBean.getMileageMKM()+"万公里");
        try {
            Float buyPrice = Float.parseFloat(dataBean.getBuyPriceFormat());
            if (buyPrice <= 0) {
                tv_money.setTextColor(Color.parseColor(Constant.NOMONEYTEXTCOLOR));
                holder.setText(R.id.tv_money, "价格待定");
            } else {
                tv_money.setTextColor(Color.parseColor(Constant.MONEYTEXTCOLOR));
                holder.setText(R.id.tv_money, dataBean.getBuyPriceFormat() + "万");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //第一个标签

        int carStatus2 = dataBean.getCarStatus2();
        if (carStatus2 == 1) {  //在售
            customTextView1.setText("在售");
            customTextView1.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLOR1));
            customTextView1.setStrokeColorAndWidth(ScreenUtils.dip2px(context,0),R.color.yishouchubg);
            customTextView1.setSolidColor(Color.parseColor(Constant.STATUSZAISHOUCOLOR));
            customTextView1.setVisibility(View.VISIBLE);
        }else if(carStatus2 == 2){//已预订
            customTextView1.setText("已预订");
            customTextView1.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLOR1));
            customTextView1.setStrokeColorAndWidth(ScreenUtils.dip2px(context,0),R.color.yishouchubg);
            customTextView1.setSolidColor(Color.parseColor(Constant.STATUSYIYUDINGCOLOR));
            customTextView1.setVisibility(View.VISIBLE);
        }else if(carStatus2 == 3){//已售出
            customTextView1.setText("已售出");
            customTextView1.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLOR1));
            customTextView1.setStrokeColorAndWidth(ScreenUtils.dip2px(context,0),R.color.yishouchubg);
            customTextView1.setSolidColor(Color.parseColor(Constant.STATUSYISHOUCHCOLOR));
            customTextView1.setVisibility(View.VISIBLE);
        }else {
            customTextView1.setVisibility(View.GONE);
            customTextView2.setVisibility(View.GONE);
            customTextView3.setVisibility(View.GONE);
        }

        //第二个标签

        if(carStatus2 == 3){
            customTextView2.setVisibility(View.GONE);
            customTextView3.setVisibility(View.GONE);
        }else{
            customTextView2.setVisibility(View.VISIBLE);
            customTextView3.setVisibility(View.VISIBLE);
            if (dataBean.getIsPreparation() == 1) {  //已整备
                customTextView2.setText("已整备");
                customTextView2.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLOR2));
                customTextView2.setStrokeColorAndWidth(ScreenUtils.dip2px(context,1),R.color.yishangjiastroke);
            }else if(dataBean.getIsPreparation() == 2){//未整备
                customTextView2.setText("未整备");
                customTextView2.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLORNO2));
                customTextView2.setStrokeColorAndWidth(ScreenUtils.dip2px(context,1),R.color.yixiajiastroke);
            }else{
                customTextView2.setVisibility(View.GONE);
            }

            if (dataBean.getIsUpshelf() == 1) {  //已上架
                customTextView3.setText("已上架");
                customTextView3.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLOR2));
                customTextView3.setStrokeColorAndWidth(ScreenUtils.dip2px(context,1),R.color.yishangjiastroke);
            }else if(dataBean.getIsUpshelf() == 2){//未上架
                customTextView3.setText("未上架");
                customTextView3.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLORNO2));
                customTextView3.setStrokeColorAndWidth(ScreenUtils.dip2px(context,1),R.color.yixiajiastroke);
            }else if(dataBean.getIsUpshelf() == 3){//已下架
                customTextView3.setText("已下架");
                customTextView3.setTextColor(Color.parseColor(Constant.STATUSTEXTCOLORNO2));
                customTextView3.setStrokeColorAndWidth(ScreenUtils.dip2px(context,1),R.color.yixiajiastroke);
            }else{
                customTextView3.setVisibility(View.GONE);
            }
        }


    }


}
