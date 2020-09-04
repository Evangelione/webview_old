package com.chengshang.ad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.chengshang.ad.constants.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import net.grandcentrix.tray.AppPreferences;

import java.util.ArrayList;
import java.util.List;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import cn.jpush.android.api.JPushInterface;

/**
 * APP名： InitialProject
 * 包名：com.ad.chengshang
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/24
 * 描述：
 * 修订历史：
 */
public class APP extends MultiDexApplication {
    // IWXAPI 是第三方app和微信通信的openApi接口
    public static IWXAPI api;
    public static AppPreferences appPreferences;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:【友盟+】 AppKey
         * 参数3:【友盟+】 Channel
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
//        UMConfigure.init(this, "5cc037ca0cafb2d078000541", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        /*
         * 二维码扫描
         * */
        ZXingLibrary.initDisplayOpinion(this);

        /*
         * 百度地图
         * */
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
//                SDKInitializer.setCoordType(CoordType.BD09LL);

        /*
         * 极光推送
         * */
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //TODO:到时跟h5通信得到Alias
        JPushInterface.setAlias(this,1,"fwc_110");

        //得到RegistrationID
        JPushInterface.getRegistrationID(this);
        Log.e("RegistrationID",JPushInterface.getRegistrationID(this));
        /*
         * 环信聊天 //TODO：此次用H5的我这边不需要配置
         * **/
       // EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
      //  options.setAcceptInvitationAlways(false);
       // EaseUI.getInstance().init(this, null);

        /*
         * 微信SDK注册
         * **/
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);
        MultiDex.install(this);

        appPreferences = new AppPreferences(getApplicationContext());
        JPushInterface.setChannel(getApplicationContext(),"店员");
        createNotificationChannel();

    }

    private void createNotificationChannel(){
                                                    // >= 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("店员", "店员通知", importance);
            // 配置通知渠道的属性
            mChannel.setDescription("这是专门给店员提供通知的渠道");
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 自定义声音
            mChannel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.new_order),null);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationChannel mChannel1 = new NotificationChannel("服务者和配送员", "服务者和配送员通知", importance);
            // 配置通知渠道的属性
            mChannel1.setDescription("这是专门给服务者和配送员提供通知的渠道");
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel1.enableLights(true);
            mChannel1.setLightColor(Color.RED);
            // 自定义声音
            mChannel1.setSound(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.fuwutishi),null);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel1.enableVibration(true);
            mChannel1.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(mChannel);
            channels.add(mChannel1);
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannels(channels);
        }
    }

}
