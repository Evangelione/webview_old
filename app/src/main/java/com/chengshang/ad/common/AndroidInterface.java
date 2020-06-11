package com.chengshang.ad.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chengshang.ad.APP;
import com.chengshang.ad.MainActivity;
import com.chengshang.ad.Model.BaseModel;
import com.chengshang.ad.Model.model.WXPayModel;
import com.chengshang.ad.Util.OkHttpUtils;
import com.chengshang.ad.constants.ApiUrl;
import com.chengshang.ad.constants.Constants;
import com.chengshang.ad.wxapi.utils.WxPayUtils;
import com.chengshang.ad.wxapi.utils.WxShareAndLoginUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by cenxiaozhong on 2017/5/14.
 * source code  https://github.com/Justson/AgentWeb
 */

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private WebView agent;
    private Context context;

    public String callback;//回调函数名称
    public String nextUrl;//重新跳转的页面

    public AndroidInterface(WebView agent, Context context) {
        this.agent = agent;
        this.context = context;
    }

    @JavascriptInterface
    public void invokeMethods(String json) {
        try {
            JSONObject jsJSON = new JSONObject(json);
            String action = jsJSON.optString("action");
            switch (action) {
                case "ScanQRCode":
                    ScanQRCode(json);
                    break;
                case "ScanPhotoCode":
                    ScanPhotoCode(json);
                case "WxLogin":
                    WxLogin(json);
                    break;
                case "WxShare":
                    WxShare(json);
                    break;
                case "WxShareImage":
                    WxShareImage(json);
                    break;
                case "SendJPushIdToServer":
                    SendJPushIdToServer(json);
                    break;
                case "SendLocationToServer":
                    SendLocationToServer(json);
                    break;
                case "WxPay":
                    WxPay(json);
                    break;
                case "SaveImage":
                    SaveImage(json);
                    break;
            }
            //            if ("ScanQRCode".equals(action)) {
            //                ScanQRCode(json);
            //            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 扫描二维码
     * **/
    private void ScanQRCode(String json) {
        try {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.cameraTask();
            //解析获取js callback方法名
            JSONObject jsJson = new JSONObject(json);
            callback = jsJson.optString("callback");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 相册选择二维码
     * **/
    private void ScanPhotoCode(String json) {
        //解析获取js callback方法名
        try {
            //解析获取js callback方法名
            JSONObject jsJson = new JSONObject(json);
            callback = jsJson.optString("callback");
            //相册选择二维码
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            ((MainActivity) context).startActivityForResult(intent, MainActivity.REQUEST_IMAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * android给H5的返回值
     * **/
    public void callbackJs(String value) {
        deliver.post(() -> {
            //回传数据给H5页面:调用js方法,必须在主线程
            //使用callback机制，解耦
            agent.loadUrl("javascript:" + callback + "(" + value + ")");
        });
    }

    /*
     * 吊起微信登录
     * **/
    private void WxLogin(String json) {
        try {
            //解析获取js callback方法名
            MainActivity mainActivity = (MainActivity) context;
            JSONObject jsJson = new JSONObject(json);
            callback = jsJson.optString("callback");
            //            mainActivity.jump_url = jsJson.optString("jump_url");
            WxShareAndLoginUtils.WxLogin(mainActivity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 微信分享
     * json 字段 shareUrl、shareTitle、shareDescription、shareImageUrl、shareJudge（类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT）
     * **/
    private void WxShare(String json) {
        try {
            //解析获取js callback方法名
            JSONObject jsJson = new JSONObject(json);
            //            callback = jsJson.optString("callback");
            WxShareAndLoginUtils.WxUrlShare(context, jsJson.optString("shareUrl"),
                    jsJson.optString("shareTitle"), jsJson.optString("shareDescription"),
                    jsJson.optString("shareImageUrl"), jsJson.optInt("shareJudge"));
            nextUrl = jsJson.optString("nextUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void WxShareImage(String json) {
        try {
            //解析获取js callback方法名
            JSONObject jsJson = new JSONObject(json);
            //            callback = jsJson.optString("callback");
            WxShareAndLoginUtils.WxBitmapShare(context, WxShareAndLoginUtils.getBitMBitmap(jsJson.
                    optString("shareImageUrl")), jsJson.optInt("shareJudge"));
            nextUrl = jsJson.optString("nextUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
     * 登录后向服务端发送极光RegistrationID和用户uid
     * **/
    private void SendJPushIdToServer(String json) {
        try {
            //解析获取js callback方法名
            JSONObject jsJson = new JSONObject(json);
            List<OkHttpUtils.Param> params = new ArrayList();
            params.add(new OkHttpUtils.Param("uid", jsJson.optString("uid")));
            params.add(new OkHttpUtils.Param("jpush_id", JPushInterface.getRegistrationID(context)));
            params.add(new OkHttpUtils.Param("app_name", context.getPackageName()));
            params.add(new OkHttpUtils.Param("type", jsJson.optString("type")));
            Log.i("====params", json);
            //将uid储存到本地
            // this Preference comes for free from the library
            APP.appPreferences.put("uid", jsJson.optString("uid"));
            APP.appPreferences.put("type", jsJson.optString("type"));
            OkHttpUtils.post(ApiUrl.SEND_JPUSHID, new OkHttpUtils.ResultCallback<BaseModel>() {
                @Override
                public void onSuccess(BaseModel response) {
                    if (response.getErrorCode() == 0) {
                        Log.i("==极光ID注册成功==", "");
                        //                        Toast.makeText(context,"==极光ID注册成功==",Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("==极光ID注册失败==", response.getErrorMsg());
                        //                        Toast.makeText(context,"==极光ID注册失败==",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.i("==极光ID注册失败==", "");
                    //                    Toast.makeText(context,"========",Toast.LENGTH_LONG).show();
                }
            }, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     *开启地图持续定位
     * */
    private void SendLocationToServer(String json) {
        try {
            JSONObject jsJson = new JSONObject(json);
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.locationTask(jsJson.optString("offer_id"), jsJson.optString("uid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 微信支付
     * **/
    /*
     * 微信分享
     * json 字段 shareUrl、shareTitle、shareDescription、shareImageUrl、shareJudge（类型选择 好友-WECHAT_FRIEND 朋友圈-WECHAT_MOMENT）
     * **/
    private void WxPay(String json) {
        try {

            //解析获取js callback方法名
            JSONObject jsJson = new JSONObject(json);
            //            callback = jsJson.optString("callback");
            List<OkHttpUtils.Param> params = new LinkedList<>();
            params.add(new OkHttpUtils.Param("mer_id", jsJson.optString("mer_id")));
            params.add(new OkHttpUtils.Param("uid", jsJson.optString("uid")));
            params.add(new OkHttpUtils.Param("order_id", jsJson.optString("order_id")));
            params.add(new OkHttpUtils.Param("order_type", jsJson.optString("order_type")));
            params.add(new OkHttpUtils.Param("paymoney", jsJson.optString("paymoney")));
            params.add(new OkHttpUtils.Param("app_id", Constants.APP_ID));
            params.add(new OkHttpUtils.Param("app_serecet", Constants.APP_SERECET));
            params.add(new OkHttpUtils.Param("partnerid", Constants.partnerid));
            params.add(new OkHttpUtils.Param("package", "com.chengshang.fwc"));
            params.add(new OkHttpUtils.Param("pay_type", "wxPay"));
            params.add(new OkHttpUtils.Param("api_key", Constants.api_key));
            OkHttpUtils.post(ApiUrl.WX_PAY, new OkHttpUtils.ResultCallback<WXPayModel>() {
                @Override
                public void onSuccess(WXPayModel response) {
                    if (response.getReturn_code().equals("SUCCESS")) {
                        WxPayUtils.WXPayBuilder builder = new WxPayUtils.WXPayBuilder();
                        builder.setAppId(Constants.APP_ID)
                                .setPartnerId(Constants.partnerid)
                                .setPrepayId(response.getPrepay_id())
                                .setPackageValue("Sign=WXPay")
                                .setNonceStr(response.getNonce_str())
                                .setTimeStamp(response.getTimestamp())
                                .setSign(response.getSign())
                                .build()
                                .toWXPayAndSign(context, Constants.APP_ID, Constants.api_key);
                    } else {
                        Toast.makeText(context, "预支付失败请联系管理员", Toast.LENGTH_LONG).show();
                        Log.i("========", response.getReturn_msg());
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "预支付失败请联系管理员", Toast.LENGTH_LONG).show();
                }
            }, params);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 保存图片
     * **/
    private void SaveImage(String json) {
        try {
            JSONObject jsJson = new JSONObject(json);
            String url = URLDecoder.decode(jsJson.optString("imgUrl"), "UTF-8");


            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)   //需要下载的图片的地址
                    .submit()
                    .get();

            //把bitmap转化为base64格式的字符串
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 30, out);
            String result = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
            byte[] bytes = Base64.decode(result, Base64.DEFAULT);
            savaBitmap(bytes);
//            Glide.with(context)
//                    .load(url)
//                    .asBitmap()
//                    .toBytes()
//                    .into(new SimpleTarget<byte[]>() {
//                        @Override
//                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
//                            try {
//                                savaBitmap(resource);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    // 保存图片到手机
    public void savaBitmap(byte[] bytes) {
        String saveImagePath = null;
        Random random = new Random();
        String imageFileName = "JPEG_" + "down" + random.nextInt(10) + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + "test");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            saveImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fout = new FileOutputStream(imageFile);
                fout.write(bytes);
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(saveImagePath);
            Toast.makeText(context, "图片保存成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "图片保存失败", Toast.LENGTH_LONG).show();
        }
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

}
