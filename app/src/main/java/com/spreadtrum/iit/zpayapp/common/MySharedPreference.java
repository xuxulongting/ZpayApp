package com.spreadtrum.iit.zpayapp.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.spreadtrum.iit.zpayapp.register_login.UserInfo;

/**
 * Created by SPREADTRUM\ting.long on 16-11-2.
 */

public class MySharedPreference {
    public static void saveToken(Context context,String token){
        SharedPreferences pref = context.getSharedPreferences("token",Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token",token);
        editor.commit();
    }

    public static String getToken(Context context){
        SharedPreferences pref = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = pref.getString("token","204");
        return token;
    }

    public static void saveUserInfo(Context context,boolean isChecked,String userName,String pwd){

        SharedPreferences pref = context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user",userName);
        if(isChecked){
            editor.putBoolean("remember_userinfo",true);
            editor.putString("password",pwd);
        }
        else {
            editor.putBoolean("remember_userinfo",false);
            editor.putString("password","");
        }
        editor.commit();

    }

    public static UserInfo getUserInfo(Context context){
        SharedPreferences pref = context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemembered = pref.getBoolean("remember_userinfo",false);
        UserInfo userInfo = new UserInfo();
        String userName="";
        String pwd="";
        userName = pref.getString("user","");
        if(isRemembered){
            pwd = pref.getString("password","");
        }
        userInfo.setLoginName(userName);
        userInfo.setLoginPwd(pwd);
        userInfo.setRemembered(isRemembered);
        return userInfo;
    }
}
