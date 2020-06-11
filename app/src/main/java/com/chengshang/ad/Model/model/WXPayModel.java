package com.chengshang.ad.Model.model;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.Model.model
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/5/30
 * 描述：
 * 修订历史：{"return_code":"SUCCESS","return_msg":"OK","appid":"wx93ea1873d3e001a4","mch_id":"1504939261","nonce_str":"cOXEGbJ1Zgm84zEE",
 * "sign":"198FC29DB33DF32030D2540B6B897CD1","result_code":"SUCCESS"
 * ,"prepay_id":"wx30173915122330b8bc553fab6251910400","trade_type":"APP","timestamp":1559209154}
 */
public class WXPayModel {
    private String prepay_id;
    private String nonce_str;
    private String timestamp;
    private String sign;
    private String return_code;
    private String return_msg;
//    private String

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }
}
