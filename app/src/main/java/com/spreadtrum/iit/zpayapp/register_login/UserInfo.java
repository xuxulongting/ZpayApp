package com.spreadtrum.iit.zpayapp.register_login;

/**
 * Created by SPREADTRUM\ting.long on 16-9-29.
 */
public class UserInfo {
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String version;
    public String loginName;
    public String loginPwd;
    public String checkCode;


}
