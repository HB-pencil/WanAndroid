package com.example.shinelon.wanandroid.helper;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View itemView;
    BaseViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.mViews = new SparseArray<>();
    }

    /**
     * 抛给用户确定具体的View
     * @param <T>
     * @return
     */
    public <T extends View> T getChildView(int id){
        if (mViews.get(id) == null) {
            T child = itemView.findViewById(id);
            mViews.put(id,child);
        }
        return (T) mViews.get(id);
    }

    public View getItemView(){
        return itemView;
    }

}
