package com.chengshang.ad.event;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.event
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/29
 * 描述：
 * 修订历史：
 */
public class MessageEvent {
    private String message;
    private String code; //"10" 微信登录成功后    "100" 微信分享成功后     "1000" 接收首页URL
    public  MessageEvent(String message,String code){
        this.message=message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
