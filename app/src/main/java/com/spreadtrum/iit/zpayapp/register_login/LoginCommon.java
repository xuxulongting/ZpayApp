package com.spreadtrum.iit.zpayapp.register_login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by SPREADTRUM\ting.long on 16-9-29.
 */
public class LoginCommon {
    public static String stringFilter(String str)throws PatternSyntaxException {
        String regEx = "[/\\:;?!#$%^&*()<>,.|\"\n\t]";//限制的字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    public interface BackBtnClickListener{
        void BackBtnClick();    //接口类只有全局常量和公共抽象方法
    }

    public static int maxUsernameLength = 30;//设置允许输入的字符长度
    public static int maxPwdLength = 20;
    public static int maxRepwdLength = 20;
}
