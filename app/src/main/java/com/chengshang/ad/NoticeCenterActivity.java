package com.chengshang.ad;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.chengshang.ad.Model.NoticeModel;
import com.chengshang.ad.Model.NoticeModelResponse;
import com.chengshang.ad.Util.OkHttpUtils;
import com.chengshang.ad.constants.ApiUrl;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.king.base.BaseActivity;
import com.king.base.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * APP名： InitialProject
 * 包名：com.chengshang
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/6/6
 * 描述：通知中心
 * 修订历史：
 */
public class NoticeCenterActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private List<NoticeModel.ListBean> mNoticeModelList;
    private int page = 1;//设置当前页面
    private int count = 0;//设置总页数
    private NoticeAdapter mAdapter;

    @SuppressLint("WrongConstant")
    @Override
    public void initUI() {
        setContentView(R.layout.activity_notice_center);
        mNoticeModelList = new ArrayList<>();
        mRecyclerView = findView(R.id.recyclerView);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                page = 1;
                mNoticeModelList.clear();
                initData();
            }

            @Override
            public void onLoadMore() {
                // load more data here
                page++;
                if (page <= count) {
                    initData();
                } else {
                    //底线
                    mRecyclerView.setNoMore(true);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new NoticeAdapter(mNoticeModelList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        //网络请求
        List<OkHttpUtils.Param> params = new ArrayList();
        params.add(new OkHttpUtils.Param("uid", APP.appPreferences.getString("uid", "default")));
//        params.add(new OkHttpUtils.Param("uid", "721"));
        params.add(new OkHttpUtils.Param("page", String.valueOf(page)));
        params.add(new OkHttpUtils.Param("type",APP.appPreferences.getString("type","0")));
//        params.add(new OkHttpUtils.Param("type","user"));
        OkHttpUtils.post(ApiUrl.NOTICE_LIST, new OkHttpUtils.ResultCallback<NoticeModelResponse>() {
            @Override
            public void onSuccess(NoticeModelResponse response) {
                Log.e("======response===","===");
                mRecyclerView.refreshComplete();
                if (response.getErrorCode() == 0) {
                    if (response.getResult().getList() != null) {
                        mNoticeModelList.addAll(response.getResult().getList());
                        count = response.getResult().getCount();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                mRecyclerView.refreshComplete();
                ToastUtils.showToast(NoticeCenterActivity.this, "网络异常", Toast.LENGTH_LONG);
            }
        }, params);

    }

    @Override
    public void addListeners() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerView != null) {
            mRecyclerView.destroy(); // this will totally release XR's memory
            mRecyclerView = null;
        }
    }
}
