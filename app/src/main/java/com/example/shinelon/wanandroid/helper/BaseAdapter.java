package com.example.shinelon.wanandroid.helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用适配器
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    /**
     * Object是因为Adapter通用的可以用T，但是即便相同的Adapter也可能有不同数据源
     */
    private List<Object> list;

    public BaseAdapter(List<Object> list){
        this.list = list;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        holder.getItemView().setOnClickListener((v)-> onItemClick(position));
        bindData(holder,position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        return new BaseViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemLayoutId(position);
    }

    /**
     * 抛给用户绑定具体数据
     * @param holder 万能的holder
     * @param position 真实位置
     */
    public abstract void bindData(BaseViewHolder holder,int position);

    /**
     * 同理暴露给用户实现
     * @param position adapter中数据的真实位置
     * @return 布局id
     */
    public abstract int getItemLayoutId(int position);

    /**
     * 可以重写此方法定义点击事件
     * @param position 数据真实位置
     */
    public void onItemClick(int position){ }
}
