package com.chengshang.ad.Model;

/**
 * APP名： InitialProject
 * 包名：com.chengshang.ad.Model
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/5/15
 * 描述：姚杰接口专用
 * 修订历史：
 */
public class BaseModel {

    /**
     * errorCode : 0
     * errorMsg : success
     * result : []
     */

    private int errorCode;
    private String errorMsg;

    private int error;
    private String msg;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
