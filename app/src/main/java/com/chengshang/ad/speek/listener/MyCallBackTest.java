package com.chengshang.ad.speek.listener;

/**
 * APP名： advertisingProject
 * 包名：com.chengshang.advertisingproject.asrwakeup.listener
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019-09-28
 * 描述：
 * 修订历史：
 */
public class MyCallBackTest {
    private MyCallBack mBack;

    private void setMyCallBack(MyCallBack mBack) {
        this.mBack = mBack;
    }

    public interface MyCallBack {
        public void myBack();
    }
}
