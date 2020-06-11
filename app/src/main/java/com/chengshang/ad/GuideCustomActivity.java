package com.chengshang.ad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.click.guide.guide_lib.GuideCustomViews;
import com.click.guide.guide_lib.interfaces.CallBack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019-11-28
 * 描述：
 * 修订历史：
 */
public class GuideCustomActivity extends AppCompatActivity implements CallBack {
    private GuideCustomViews GuideCustomViews;
    private final int[] mPageImages = {
            R.mipmap.guide_1,
            R.mipmap.guide_2,
            R.mipmap.guide_3,
            R.mipmap.guide_4
    };

    private final int[] mGuidePoint = {
            R.mipmap.icon_guide_point_select,
            R.mipmap.icon_guide_point_unselect
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        //获得SharedPreferences的实例 sp_name是文件名
        SharedPreferences sp = getSharedPreferences("sp_name",Context.MODE_PRIVATE);
        //获得Editor 实例
        SharedPreferences.Editor editor = sp.edit();
        //以key-value形式保存数据
        editor.putBoolean("open_first", true);
        //apply()是异步写入数据
        editor.apply();
        //commit()是同步写入数据
        //editor.commit();
        initView();
    }


    private void initView() {
        GuideCustomViews = findViewById(R.id.guide_CustomView);
        GuideCustomViews.setData(mPageImages, mGuidePoint, this);
    }

    @Override
    public void callSlidingPosition(int i) {
        Log.e("callSlidingPosition", "滑动位置 callSlidingPosition " + i);
    }

    @Override
    public void callSlidingLast() {
        Log.e("callSlidingLast", "滑动到最后一个callSlidingLast");
        startAnim();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GuideCustomViews.clear();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.e("onPointerCaptureChanged", "===");
    }



    /**
     * 启动动画
     */
    private void startAnim() {
        // 渐变动画,从完全透明到完全不透明
        AlphaAnimation alpha = new AlphaAnimation(1, 1);
        // 持续时间 2 秒
        alpha.setDuration(500);
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
        GuideCustomViews.startAnimation(alpha);
    }

    /**
     * 跳转到下一个页面
     */
    private void jumpNextPage() {
        startActivity(new Intent(GuideCustomActivity.this, MainActivity.class));
        finish();
    }

}
