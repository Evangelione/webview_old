package com.chengshang.ad;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.chengshang.ad.Util.PhotoUtils;
import com.chengshang.ad.common.AndroidInterface;
import com.king.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.chengshang.ad.constants.Constants.STAFF_MSG_DETAIL;
import static com.chengshang.ad.constants.Constants.STAFF_PENDING_ORDER;
import static com.chengshang.ad.constants.Constants.STAFF_SERVICE;

/**
 * APP名： InitialProject
 * 包名：com.ad.chengshang
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/26
 * 描述：
 * 修订历史：
 */
public class WebViewActivity extends BaseActivity {

  //    @BindView(R.id.toolbar_title)
//    TextView mTitleTextView;
//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;
  @BindView(R.id.container)
  LinearLayout mLinearLayout;
  private AlertDialog mAlertDialog;
  String jsonStr;
  protected WebView mWebView;
  public AndroidInterface mAndroidInterface;
  private ValueCallback<Uri> mUploadMessage;
  private ValueCallback<Uri[]> mUploadCallbackAboveL;
  private final static int PHOTO_REQUEST = 100;
  private Uri imageUri;

  @Override
  public void initUI() {
    setContentView(R.layout.activity_webview);
    ButterKnife.bind(this);
    if (getSupportActionBar() != null) {
      // Enable the Up button
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
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
          view.loadUrl(request.getUrl().toString());
        } else {
          view.loadUrl(request.toString());
        }
        return true;
      }

      //            @Override
      //            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
      //                super.onReceivedError(view, request, error);
      //            }
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
    String stt = getIntent().getStringExtra("url");
    if (stt != null) {
      mWebView.loadUrl(getIntent().getStringExtra("url"));
    } else if (getIntent().getStringExtra("open_type") != null) {
        switch (getIntent().getStringExtra("open_type")) {
        case "1":
          mWebView.loadUrl(STAFF_MSG_DETAIL);
          break;
        case "2":
          mWebView.loadUrl(STAFF_SERVICE);
          break;
        case "3":
          mWebView.loadUrl(STAFF_PENDING_ORDER);
          break;
      }
    }
  }

  @Override
  public void initData() {

  }

  @Override
  public void addListeners() {

  }

  /**
   * 拍照
   */
  private void takePhoto() {
    // 指定拍照存储位置的方式调起相机
    if (Build.VERSION.SDK_INT >= 24) {
      File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
      imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
    } else {
      String filePath = Environment.getExternalStorageDirectory() + File.separator
          + Environment.DIRECTORY_PICTURES + File.separator;
      String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
      imageUri = Uri.fromFile(new File(filePath + fileName));
    }

    //        File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
    //        imageUri = Uri.fromFile(fileUri);
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    //            imageUri = FileProvider.getUriForFile(this,
    //                    getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
    //        }
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
    mWebView.onResume();
    mWebView.resumeTimers();
    super.onResume();
  }


  @Override
  protected void onPause() {
    mWebView.onPause();
    mWebView.pauseTimers();
    super.onPause();
  }


  @Override
  protected void onDestroy() {
    //        mAgentWeb.getWebLifeCycle().onDestroy();
    mLinearLayout.removeView(mWebView);
    mWebView.destroy();
    super.onDestroy();
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }
}
