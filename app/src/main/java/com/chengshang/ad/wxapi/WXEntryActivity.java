package com.chengshang.ad.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.chengshang.ad.APP;
import com.chengshang.ad.Util.OkHttpUtils;
import com.chengshang.ad.constants.Constants;
import com.chengshang.ad.event.MessageEvent;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import cn.jpush.android.api.JPushInterface;

/**
 * APP名： InitialProject
 * 包名：com.ad.chengshang.wxapi
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/26
 * 描述：微信
 * 修订历史：
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public  int WX_LOGIN = 1;

//    private IWXAPI iwxapi;

    private SendAuth.Resp resp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       APP.api.handleIntent(this.getIntent(), this);

    }

    @Override
    public void onReq(BaseReq baseReq) {
    }



    //请求回调结果处理
    @Override
    public void onResp(BaseResp baseResp) {
        System.out.println("--------------------" + baseResp.getType());
        //微信登录为getType为1，分享为0
        if (baseResp.getType() == WX_LOGIN){
            //登录回调
            //            System.out.println("------------登陆回调------------");
            resp = (SendAuth.Resp) baseResp;
            //            System.out.println("------------登陆回调的结果------------：" +  new Gson().toJson(resp));
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = String.valueOf(resp.code);
                    //获取用户信息
                    getAccessToken(code);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                    Toast.makeText(WXEntryActivity.this, "用户拒绝授权", Toast.LENGTH_LONG).show();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                    Toast.makeText(WXEntryActivity.this, "用户取消登录", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }else{
            //分享成功回调
            System.out.println("------------分享回调------------");
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    //分享成功
                    Toast.makeText(WXEntryActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                    EventBus.getDefault().post(new MessageEvent("分享成功","100"));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //分享取消
                    Toast.makeText(WXEntryActivity.this, "分享取消", Toast.LENGTH_LONG).show();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    //分享拒绝
                    Toast.makeText(WXEntryActivity.this, "分享拒绝", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        finish();
    }

    private void getAccessToken(String code) {
        //获取授权
        String http = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + Constants.APP_ID + "&secret=" + Constants.APP_SERECET + "&code=" + code + "&grant_type=authorization_code";
        OkHttpUtils.ResultCallback<String> resultCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                String access = null;
                String openId = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("openid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //获取个人信息
                String getUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openId + "";
                String finalAccess = access;
                OkHttpUtils.ResultCallback<WeChatInfo> resultCallback = new OkHttpUtils.ResultCallback<WeChatInfo>() {
                    @Override
                    public void onSuccess(WeChatInfo response) {
                        response.setErrCode(resp.errCode);
                        Log.i("TAG获取个人信息",new Gson().toJson(response));
                        response.setAccess(finalAccess);
                        response.setJpush_id(JPushInterface.getRegistrationID(WXEntryActivity.this));
                        //                        Toast.makeText(WXEntryActivity.this, new Gson().toJson(response), Toast.LENGTH_LONG).show();
                        EventBus.getDefault().post(new MessageEvent(new Gson().toJson(response),"10"));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                };
                OkHttpUtils.get(getUserInfo, resultCallback);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        };
        OkHttpUtils.get(http, resultCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
