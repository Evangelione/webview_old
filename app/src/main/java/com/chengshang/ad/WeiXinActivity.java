package com.chengshang.ad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chengshang.ad.wxapi.utils.WxShareAndLoginUtils;

import androidx.appcompat.app.AppCompatActivity;

/**
 * APP名： InitialProject
 * 包名：com.ad.chengshang
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/26
 * 描述：
 * 修订历史：
 */
public class WeiXinActivity extends AppCompatActivity  {
    private Button btn,btn_text,btn_image,btn_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin);
        btn = (Button) findViewById(R.id.btn);
        btn_text = (Button) findViewById(R.id.btn_text);
        btn_image = (Button) findViewById(R.id.btn_image);
        btn_url = (Button) findViewById(R.id.btn_url);
        //        测试微信登录
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WxShareAndLoginUtils.WxLogin(WeiXinActivity.this);
            }
        });
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WxShareAndLoginUtils.WxTextShare(WeiXinActivity.this,"微信分享",WxShareAndLoginUtils.WECHAT_MOMENT);
            }
        });
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                WxShareAndLoginUtils.WxBitmapShare(WeiXinActivity.this,bitmap,WxShareAndLoginUtils.WECHAT_MOMENT);
            }
        });
        btn_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WxShareAndLoginUtils.WxUrlShare(WeiXinActivity.this,"http://www.baidu.com", "百度", "百度一下",
                        "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E7%99%BE%E5%BA%A6logo&step_word=&hs=0&pn=7&spn=0&di=87711457570&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=3295332534%2C276154593&os=1276437059%2C1808669272&simid=3478872735%2C168961746&adpicid=0&lpn=0&ln=1958&fr=&fmq=1517922230276_R&fm=detail&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.swhaifeng.com%2Ffile%2Fupload%2F201406%2F20%2F12-13-15-92-1.png&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bfoiwtujg2_z%26e3Bv54AzdH3Fk6wg1AzdH3Ffi5o-ldl_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0",
                        WxShareAndLoginUtils.WECHAT_FRIEND);
            }
        });
    }
}
