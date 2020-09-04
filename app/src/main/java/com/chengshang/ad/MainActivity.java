package com.chengshang.ad;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.chengshang.ad.Model.BaseModel;
import com.chengshang.ad.Util.CheckPermissionUtils;
import com.chengshang.ad.Util.ImageUtil;
import com.chengshang.ad.Util.OkHttpUtils;
import com.chengshang.ad.Util.PhotoUtils;
import com.chengshang.ad.common.AndroidInterface;
import com.chengshang.ad.constants.ApiUrl;
import com.chengshang.ad.event.MessageEvent;
import com.chengshang.ad.jpush.ExampleUtil;
import com.chengshang.ad.sonic.DefaultSonicRuntimeImpl;
import com.chengshang.ad.sonic.SonicJavaScriptInterface;
import com.chengshang.ad.sonic.SonicSessionClientImpl;
import com.chengshang.ad.speek.OfflineResource;
import com.google.gson.Gson;
import com.king.base.BaseActivity;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Notification.EXTRA_CHANNEL_ID;
import static android.provider.Settings.EXTRA_APP_PACKAGE;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    //    protected AgentWeb mAgentWeb;
    protected WebView mWebView;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int PHOTO_REQUEST = 100;
    private Uri imageUri;

    //    public String jump_url = "";//微信登录成功以后调起页面

    @BindView(R.id.container)
    LinearLayout mLinearLayout;

    //    private AlertDialog mAlertDialog;
    private SonicSession sonicSession;
    //首屏秒开
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_IMAGE = 112;

    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;

    /*
     * 请求百度地图定位权限码
     * */
    //    public static final int READ_PHONE_STATE = 201 ;
    public static final int ACCESS_COARSE_LOCATION = 301;
    //    public static final int ACCESS_FINE_LOCATION = 401;

    public static boolean isForeground = false;

    public String offerId;
    public String uId;

    public AndroidInterface mAndroidInterface;


    //    public static IWXAPI api;
    @Override
    public void initUI() {
        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new DefaultSonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mWebView = findView(R.id.webView);
        mWebView.getSettings().setUserAgentString(WebSettings.getDefaultUserAgent(this) + "android_chengshang_app");
        if (mWebView != null) {
            mAndroidInterface = new AndroidInterface(mWebView, this);
            mWebView.addJavascriptInterface(mAndroidInterface, "android");
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //想在页面开始加载时有操作，在这添加
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //想在页面加载结束时有操作，在这添加
                super.onPageFinished(view, url);
            }

            //是否在webview内加载页面
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().startsWith("tel:")) {
                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(new Intent(Intent.ACTION_CALL, request.getUrl()));
                        } else {
                            initPermission();
                        }
                    } else {
                        view.loadUrl(request.getUrl().toString());
                    }

                } else {
                    if (request.toString().startsWith("tel:")) {
                        startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(request.toString())));
                    } else {
                        view.loadUrl(request.toString());
                    }
                }
                return true;
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                //                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                mUploadCallbackAboveL = filePathCallback;
                takePhoto();
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, true);
            }

        });

        WebSettings webSettings = mWebView.getSettings();
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

        // init webview settings
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        webSettings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        webSettings.setJavaScriptEnabled(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setGeolocationEnabled(true);//允许地理位置可用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//允许在HTTS下访问http的内容
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        //加载离线语音合成文件
        new Handler().postDelayed(this::createOfflineResource, 15000);
    }

    @Override
    public void initData() {
        initPermission();
        if (isNotificationEnable(this)) {
            Log.d("==========", "已经有推送权限");
        } else {
            //实例化建造者
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //设置警告对话框的标题
            builder.setTitle("提示");
            //设置警告显示的图片
            //    builder.setIcon(android.R.drawable.ic_dialog_alert);
            //设置警告对话框的提示信息
            builder.setMessage("应用通知还未打开，这样您将接收不到最新的服务消息，是否去打开？");
            //设置”正面”按钮，及点击事件
            builder.setPositiveButton("去打开", (dialog, which) -> {
                try {
                    // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                    intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
                    intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);

                    //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);

                    // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
                    //  if ("MI 6".equals(Build.MODEL)) {
                    //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    //      Uri uri = Uri.fromParts("package", getPackageName(), null);
                    //      intent.setData(uri);
                    //      // intent.setAction("com.android.settings/.SubSettings");
                    //  }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
                    Intent intent = new Intent();

                    //下面这种方案是直接跳转到当前应用的设置界面。
                    //https://blog.csdn.net/ysy950803/article/details/71910806
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            //设置“反面”按钮，及点击事件
            builder.setNegativeButton("取消", (dialog, which) -> {

            });
            //显示对话框
            builder.show();
        }
    }

    @Override
    public void addListeners() {
        registerMessageReceiver();
        EventBus.getDefault().register(this);
        //百度地图定位
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setCoorType("GCJ02");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setScanSpan(10000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效
        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false
        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        // 指定拍照存储位置的方式调起相机
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
        } else {
            String filePath = Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_PICTURES + File.separator;
            String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            imageUri = Uri.fromFile(new File(filePath + fileName));
        }
        PhotoUtils.takePicture(this, imageUri, PHOTO_REQUEST);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {  // goBack()表示返回WebView的上一页面
            mWebView.goBack();
            return true;
        } else {
            //结束当前页
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onResume() {
        isForeground = true;
        //        mAgentWeb.getWebLifeCycle().onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        //        mAgentWeb.getWebLifeCycle().onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //        mAgentWeb.getWebLifeCycle().onDestroy();
        mLinearLayout.removeView(mWebView);
        mWebView.destroy();
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /*
     * 接收微信登录成功 用户信息并传给H5
     * **/
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void Event(MessageEvent messageEvent) {
        if (messageEvent.getCode().equals("10")) {
            mAndroidInterface.callbackJs(messageEvent.getMessage());
        } else if (messageEvent.getCode().equals("100")) {
                    mWebView.clearView();
                    mWebView.reload();
                    mWebView.loadUrl(mAndroidInterface.nextUrl);
        } else if (messageEvent.getCode().equals("1000")) {
            mWebView.clearView();
            mWebView.reload();
            SonicSessionClientImpl sonicSessionClient = null;

            // step 2: Create SonicSession
            sonicSession = SonicEngine.getInstance().createSession(messageEvent.getMessage(), new SonicSessionConfig.Builder().build());
            if (null != sonicSession) {
                sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            }
            mWebView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient,
                    new Intent().putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis())), "sonic");
            if (sonicSessionClient != null) {
                sonicSessionClient.bindWebView(mWebView);
                sonicSessionClient.clientReady();
            } else { // default mode
                mWebView.loadUrl(messageEvent.getMessage());
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    mAndroidInterface.callbackJs(new Gson().toJson(result));
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        /*
         * 选择系统图片并解析
         */
        else if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            //                            Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                            mAndroidInterface.callbackJs(result);
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CAMERA_PERM || requestCode == ACCESS_COARSE_LOCATION) {
            Toast.makeText(this, "从设置页面返回...", Toast.LENGTH_SHORT)
                    .show();
        }
        /*
         * 拍照和相册
         * **/
        else if (requestCode == PHOTO_REQUEST) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != PHOTO_REQUEST || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }

    /**
     * EsayPermissions接管权限处理逻辑
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 把执行结果的操作给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*
     * 添加@AfterPermissionGranted()注解
     *要传入的参数是int类型的requestCode被这个注解标注的方法，
     *当这个requestCode的请求成功的时候，会执行这个方法。其实就相当于在onPermissionsGranted()调用这个方法而已:
     * **/
    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(ACCESS_COARSE_LOCATION)
    public void locationTask(String offer_id, String uid) {
        offerId = offer_id;
        uId = uid;
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Have permission, do the thing!
            //            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            //            startActivity(intent);
            //            开启持续定位
            mLocationClient.start();

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求获取地理位置权限",
                    ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override //申请成功时调用
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //        Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
    }

    @Override //申请失败时调用
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERM: {
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                            .setTitle("权限申请")
                            .setPositiveButton("确认")
                            .setNegativeButton("取消", null /* click listener */)
                            .setRequestCode(REQUEST_CAMERA_PERM)
                            .build()
                            .show();
                }
                break;
            }
            case ACCESS_COARSE_LOCATION: {
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                    new AppSettingsDialog.Builder(this, "当前App需要申请获取地理位置权限,需要打开设置页面么?")
                            .setTitle("权限申请")
                            .setPositiveButton("确认")
                            .setNegativeButton("取消", null /* click listener */)
                            .setRequestCode(ACCESS_COARSE_LOCATION)
                            .build()
                            .show();
                }
                break;
            }
        }
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    /*
     * 注册环信消息广播
     * **/
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    /*
     * 接收环信推送消息
     * **/
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    private void setCostomMsg(String msg) {
        showLongToast(msg);
    }

    /**
     * 初始化权限事件
     */
    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            //是否登录
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    /*
     * 判断通知权限是否打开
     */
    private boolean isNotificationEnable(Context mContext) {
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= 24) {
            return mNotificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps =
                    (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            String pkg = mContext.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 百度地图定位回调
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();    //获取纬度信息
            double longitude = bdLocation.getLongitude();    //获取经度信息
            Log.i("=====", bdLocation.getLocType() + "");
            Log.i("=====", latitude + "");
            Log.i("=====", longitude + "");
            List<OkHttpUtils.Param> params = new ArrayList<>();
            params.add(new OkHttpUtils.Param("offer_id", offerId));
            params.add(new OkHttpUtils.Param("address_lng", Double.toString(longitude)));
            params.add(new OkHttpUtils.Param("address_lat", Double.toString(latitude)));
            //            params.add(new OkHttpUtils.Param("address_lng","120.181721"));
            //            params.add(new OkHttpUtils.Param("address_lat","30.327046"));
            params.add(new OkHttpUtils.Param("uid", uId));
            OkHttpUtils.post(ApiUrl.LOCATION_CODE, new OkHttpUtils.ResultCallback<BaseModel>() {
                @Override
                public void onSuccess(BaseModel response) {
                    if (response.getError() == 1) {
                        Log.i("======locationS", "=====");

                    } else {
                        Log.i("======locationE", "=====");

                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.i("======locationF", "=====");
                }
            }, params);
        }

    }

    protected void createOfflineResource() {
        try {
            new OfflineResource(this, OfflineResource.VOICE_FEMALE);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
        }
    }

}
