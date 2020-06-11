/*
 Copyright © 2015, 2016 Jenly Yu <a href="mailto:jenly1314@gmail.com">Jenly</a>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */
package com.king.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 *
 * @param <T>
 *           实体对象
 * @param <H>
 *     		ViewHolder
 */
public abstract class HolderRecyclerAdapter<T,H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H>{

    private Context context;

    private List<T> listData;

    private LayoutInflater layoutInflater;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.mOnItemClickListener = clickListener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return mOnItemClickListener;
    }

    protected void onItemClick(View v,int position){
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(v,position);
        }
    }

    public HolderRecyclerAdapter(Context context, List<T> listData){
        super();
        this.context = context;
        this.listData = listData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public LayoutInflater getLayoutInflater(){
        return layoutInflater;
    }

    public Context getContext(){
        return context;
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = buildConvertView(layoutInflater,parent,viewType);
        return buildHolder(itemView,viewType);
    }

    @Override
    public void onBindViewHolder(H holder, final int position) {
        T t = position<listData.size() ? listData.get(position) : null;
        bindViewDatas(holder,t,position);
        if(this.mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listData==null ? 0:listData.size();
    }

    public View inflate(@LayoutRes int layoutId,ViewGroup parent,boolean attachToRoot){
        return layoutInflater.inflate(layoutId,parent,attachToRoot);
    }

    public List<T> getListData() {
        return listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    /**
     * 建立convertView
     * @param layoutInflater
     * @param parent
     * @param viewType
     * @return
     */
    public abstract View buildConvertView(LayoutInflater layoutInflater,ViewGroup parent,int viewType);

    /**
     * 建立视图Holder
     * @param convertView
     * @param viewType
     * @return
     */
    public abstract H buildHolder(View convertView,int viewType);

    /**
     * 绑定数据
     * @param holder
     * @param t
     * @param position
     */
    public abstract void bindViewDatas(H holder,T t,int position);
}
