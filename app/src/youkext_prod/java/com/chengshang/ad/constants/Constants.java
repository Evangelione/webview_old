package com.chengshang.ad.constants;

public class Constants {
    // APP_ID 替换为你的应用从官方网站申请到的合法appId 微信key
    public static final String APP_ID = "wxdfd64741e5e50d27";
    public static final String APP_SERECET = "8971742f1b3242c054d62838ceeb6f5a";
    public static final String partnerid = "1505741881";//商户号
    public static final String api_key = "freeelite1982kevin1992limmy1992e";

    public static class ShowMsgActivity {
        public static final String STitle = "showmsg_title";
        public static final String SMessage = "showmsg_message";
        public static final String BAThumbData = "showmsg_thumb_data";
    }
    public static final String BASE_URL= "https://www.9youke.com";
//    public static final String BASE_URL= "http://cs.7youke.com";
//    public static final String BASE_URL= "https://www.91gzt.com";
    public static final String MAINURL = BASE_URL + "/wap.php?g=Wap&c=login&a=centre";//主页地址

    public static final String STAFF_MSG_DETAIL = BASE_URL + "/staff/staff/message";//店员消息列表
    public static final String STAFF_PENDING_ORDER = BASE_URL + "/staff/staffOrder/entryOrder";//店员挂单页面列表
    public static final String STAFF_SERVICE = BASE_URL + "/packapp/storestaff/service.html";//店员服务工单列表



    //    public static final String URL_ADDR = BASE_URL + "/wap.php?g=Wap&c=service&a=index";//服务车
//    public static final String URL_ADDR = BASE_URL + "/wap.php?g=Wap&c=Home&a=index&no_house=1";
}
