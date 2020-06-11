package com.chengshang.ad.constants;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.constants
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/5/20
 * 描述：
 * 修订历史：
 */
public class ApiUrl {
    //获取tabBar
    public static final String TABBAR = Constants.BASE_URL + "/wap.php?g=wap&c=Home&a=ajax_index";
    //上传定位信息
    public static final String LOCATION_CODE = Constants.BASE_URL + "/wap.php?g=Wap&c=Service&a=upload_location";
    //微信支付
    public static final String WX_PAY = Constants.BASE_URL +"/appapi.php?g=appapi&c=pay&a=getPrepayId";
    //获取消息列表
    public static final String NOTICE_LIST = Constants.BASE_URL + "/appapi.php?g=appapi&c=my&a=template_news_list";
    //上传极光ID
    public static final String  SEND_JPUSHID = Constants.BASE_URL +"/appapi.php?g=appapi&c=my&a=bind_jpush";

}