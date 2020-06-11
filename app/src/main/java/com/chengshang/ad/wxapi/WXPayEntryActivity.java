package com.chengshang.ad.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.chengshang.ad.APP;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.wxapi
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/5/28
 * 描述：
 * 修订历史：
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private final String TAG = "WXPayEntryActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        APP.api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        APP.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        APP.api.handleIntent(intent, this);
    }
    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG,"errCode = " + resp.errCode);
        //最好依赖于商户后台的查询结果
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            //如果返回-1，很大可能是因为应用签名的问题。用官方的工具生成
//            //签名工具下载：https://open.weixin.qq.com/zh_CN/htmledition/res/dev/download/sdk/Gen_Signature_Android.apk
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("提示");
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();

            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle("提示");
                    builder1.setMessage("付款成功！");
                    builder1.setNeutralButton("确定", (dialog, which) -> {
                             dialog.dismiss();
                             finish();
                    });
                    builder1.show();
                    //                    Toast.makeText(this, "付款成功！", Toast.LENGTH_SHORT).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setTitle("提示");
                    builder2.setMessage("付款取消！");
                    builder2.setNeutralButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
                    builder2.show();
                    //Toast.makeText(this, "付款取消！", Toast.LENGTH_SHORT).show();
                    //Constant.WEIXIN_PAY_STATUS = "PAY_CANCEL";
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    //分享拒绝
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                    builder3.setTitle("提示");
                    builder3.setMessage("付款拒绝！");
                    builder3.setNeutralButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
                    builder3.show();
                    //Toast.makeText(this, "付款拒绝！", Toast.LENGTH_SHORT).show();
                    //Constant.WEIXIN_PAY_STATUS = "PAY_DENY";
                    break;
            }
        }
    }
}
