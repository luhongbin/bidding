package org.linlinjava.litemall.wx.dto;

public class DdLoginInfo {
    private String userid;
    private UserInfo userInfo;

    public String getCode() {
        return userid;
    }

    public void setCode(String userid) {
        this.userid = userid;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
