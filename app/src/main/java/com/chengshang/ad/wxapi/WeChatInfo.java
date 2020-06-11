package com.chengshang.ad.wxapi;

/**
 * APP名： InitialProject
 * 包名：com.ad.chengshang.wxapi
 * 作者：jiang-pc
 * 版本：
 * 创建日期：2019/4/26
 * 描述：
 * 修订历史：
 */
public class WeChatInfo {
    private int errCode;

    private String openid;

    private int sex;

    private String nickname;

    private String headimgurl;

    private String province;

    private String language;

    private String country;

    private String unionid;

    private String access;

    private String jpush_id;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getSex() {
        return (sex == 0) ? "男" : "女";
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getJpush_id() {
        return jpush_id;
    }

    public void setJpush_id(String jpush_id) {
        this.jpush_id = jpush_id;
    }

    @Override
    public String toString() {
        return "WeChatInfo{" +
                "errCode='" + errCode + '\'' +
                ", openid='" + openid + '\'' +
                ", sex=" + sex +
                ", nickname='" + nickname + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", province='" + province + '\'' +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                ", unionid='" + unionid + '\'' +
                '}';
    }
}
