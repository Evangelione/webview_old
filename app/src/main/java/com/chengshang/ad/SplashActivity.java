package com.chengshang.ad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.chengshang.ad.Model.HomeModel;
import com.chengshang.ad.Model.HomeModelResponse;
import com.chengshang.ad.Util.OkHttpUtils;
import com.chengshang.ad.constants.ApiUrl;
import com.chengshang.ad.constants.Constants;
import com.chengshang.ad.event.MessageEvent;
import com.king.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * APP名： InitialProject
 * 包名：com.ad.chengshang
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/28
 * 描述：
 * 修订历史：
 */
public class SplashActivity extends BaseActivity {
  private RelativeLayout rlSplash;

  @Override
  public void initUI() {
    setContentView(R.layout.activity_splash);
    if (this.getPackageName().equals("com.chengshang.fwc")) {
      EventBus.getDefault().postSticky(new MessageEvent(Constants.MAINURL, "1000"));

    } else {
      //请求tab数据
      OkHttpUtils.post(ApiUrl.TABBAR, new OkHttpUtils.ResultCallback<HomeModelResponse>() {
        @Override
        public void onSuccess(HomeModelResponse response) {
          if (response.getCode() == 200) {
            Log.i("==2==", "");
            if (response.getDatas().getFooter_menu() != null) {
              for (HomeModel.FooterMenuBean bean : response.getDatas().getFooter_menu()) {
                if (bean.getName().equals("首页")) {
                  EventBus.getDefault().postSticky(new MessageEvent(bean.getUrl(), "1000"));
                }
              }
            }
          } else {
            Log.i("==3==", "");
          }
        }

        @Override
        public void onFailure(Exception e) {
          Log.i("==4==", "");
        }
      }, new ArrayList<>());
    }
    rlSplash = (RelativeLayout) findViewById(R.id.rl_splash);
    startAnim();
  }

  @Override
  public void initData() {

  }

  @Override
  public void addListeners() {

  }

  /**
   * 启动动画
   */
  private void startAnim() {
    // 渐变动画,从完全透明到完全不透明
    AlphaAnimation alpha = new AlphaAnimation(0, 1);
    // 持续时间 2 秒
    alpha.setDuration(1500);
    // 动画结束后，保持动画状态
    alpha.setFillAfter(true);

    // 设置动画监听器
    alpha.setAnimationListener(new Animation.AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }

      // 动画结束时回调此方法
      @Override
      public void onAnimationEnd(Animation animation) {
        // 跳转到下一个页面
        jumpNextPage();
      }
    });

    // 启动动画
    rlSplash.startAnimation(alpha);
  }

  /**
   * 跳转到下一个页面
   */
  private void jumpNextPage() {
    //获得SharedPreferences的实例
    SharedPreferences sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
    //通过key值获取到相应的data，如果没取到，则返回后面的默认值
    Boolean data = sp.getBoolean("open_first", false);
    if (!data) { //第一次打开跳转引导页
//            startActivity(new Intent(SplashActivity.this, GuideCustomActivity.class));
      startActivity(new Intent(SplashActivity.this, MainActivity.class));
      finish();
    } else {
      startActivity(new Intent(SplashActivity.this, MainActivity.class));
      finish();
    }
  }
}
