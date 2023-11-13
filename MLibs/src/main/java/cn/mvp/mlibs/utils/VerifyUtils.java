package cn.mvp.mlibs.utils;


import android.text.TextUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wlj
 * @date 2017/3/29
 * @email wanglijundev@gmail.com
 * @packagename wanglijun.vip.androidutils.utils
 * @desc: 验证数据合法性
 */

public class VerifyUtils {
    /**
     * verify whether email is valid
     * 验证电子邮件是否有效
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)" +
                        "+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * verify whether email is valid
     * 验证email是否有效
     *
     * @param email
     * @return
     */
    public static boolean isEmail2(String email) {
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return email.matches(expr);
    }

    /**
     * verify whether mobile number is valid
     * 验证手机号码是否有效
     *
     * @param number
     * @return
     */
    public static boolean isMobileNumber(String number) {
        String expr = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        return number.matches(expr);
    }

    /**
     * verify whether url is valid
     * 验证url是否有效
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern patt = Pattern.compile(regex);
        Matcher matcher = patt.matcher(url);
        return matcher.matches();
    }

    /**
     * verify whether number and letter are only contained
     * 验证是否只包含字母数字
     *
     * @param data
     * @return
     */
    public static boolean isNumberAndLetter(String data) {
        String expr = "^[A-Za-z0-9]+$";
        return data.matches(expr);
    }

    /**
     * verify whether number is only contained
     * 验证是否只包含数字
     *
     * @param data
     * @return
     */
    public static boolean isNumber(String data) {
        String expr = "^[0-9]+$";
        return data.matches(expr);
    }

    /**
     * verify whether letter is only contained
     * 验证是否只包含字母
     *
     * @param data
     * @return
     */
    public static boolean isLetter(String data) {
        String expr = "^[A-Za-z]+$";
        return data.matches(expr);
    }

    /**
     * verify whether Chinese is only contained
     * 验证是否只包含中文
     *
     * @param data
     * @return
     */
    public static boolean isChinese(String data) {
        String expr = "^[\u0391-\uFFE5]+$";
        return data.matches(expr);
    }

    /**
     * verify whether Chinese is contained
     * 确认是否包含中文
     *
     * @param data
     * @return
     */
    public static boolean isContainsChinese(String data) {
        String chinese = "[\u0391-\uFFE5]";
        if (!TextUtils.isEmpty(data)) {
            for (int i = 0; i < data.length(); i++) {
                String temp = data.substring(i, i + 1);
                boolean flag = temp.matches(chinese);
                if (flag) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * verify whether chinese identity card is valid
     *
     * @param data
     * @return
     */
    public static boolean isChineseCard(String data) {
        String expr = "^[0-9]{17}[0-9xX]$";
        return data.matches(expr);
    }

    /**
     * verify whether post code is valid
     *
     * @param data
     * @return
     */
    public static boolean isPostCode(String data) {
        String expr = "^[0-9]{6,10}";
        return data.matches(expr);
    }


    /**
     * @param arg    字符串参数
     * @param tipMsg 提示信息
     */
    public static void checkBlank(String arg, String tipMsg) throws IOException {
        if (arg == null || arg.trim().length() == 0) {
            throw new IOException(tipMsg);
        }
    }

    public static void checkSame(String arg0, String arg1, String tipMsg) throws IOException {
        if (StringUtil.equals(arg0, arg1)) {
            throw new IOException(tipMsg);
        }
    }

    public static void checkTrue(boolean arg0, String tipMsg) throws IOException {
        if (arg0) {
            throw new IOException(tipMsg);
        }
    }

}