package com.chengshang.ad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chengshang.ad.Model.NoticeModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/6/6
 * 描述：
 * 修订历史：
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private List<NoticeModel.ListBean> list;
    private Context mContext;

    public NoticeAdapter(List<NoticeModel.ListBean> data, Context context) {
        super();
        list = data;
        mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTvTitle.setText(list.get(position).getTitle());
        holder.mRemark.setText(list.get(position).getRemark());
        holder.mTvDate.setText(getDateToString(Long.valueOf(list.get(position).getDateline())*1000,"yyyy-MM-dd"));
        holder.mCarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, WebViewActivity.class);
                i.putExtra("url",list.get(position).getHref());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTitle;
        TextView mTvDate;
        TextView mRemark;
        CardView mCarView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            this.mTvDate = (TextView) itemView.findViewById(R.id.tv_date);
            this.mRemark = (TextView) itemView.findViewById(R.id.remark);
            this.mCarView = (CardView) itemView.findViewById(R.id.carView);
        }
    }

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     */
    public String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

}
