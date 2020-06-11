package com.chengshang.ad.Model;


import java.io.Serializable;

/**
 * Created by ysh on 2018/4/12.
 *
 * @描述 ${TODO}\
 * <p>
 * Update by $Author on ${}.
 * @描述 晓喻接口专用
 */

public class BaseResponse implements Serializable {

    private static final long   serialVersionUID = -7308090443695973391L;
    /**
     * code : 0
     * msg : Phone cannot be blank.
     * errorfield : phone
     */
    public static final  int    SUCCEEDCODE      = 1;
    private              int    code             = -1;
    private String msg              = "";
    private String errorfield;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorfield() {
        return errorfield;
    }

    public void setErrorfield(String errorfield) {
        this.errorfield = errorfield;
    }

    public static boolean isSucceed(BaseResponse response) {
        return response.getCode() == SUCCEEDCODE;
    }

    //腾讯地图返回值
    private int status;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //下单返回值
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //上传图片
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
