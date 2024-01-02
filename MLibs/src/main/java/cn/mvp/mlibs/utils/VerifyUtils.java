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
     * 核实中国身份证是否有效
     *
     * @param data
     * @return
     */
    public static boolean isChineseCard(String data) {
        String expr = "^[0-9]{17}[0-9xX]$";
        return data.matches(expr);
    }

    /**
     * 验证邮政编码是否有效
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

    /**
     * 正则：手机号（简单）, 1字头＋10位数字即可.
     */
    private static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    private static final Pattern PATTERN_REGEX_MOBILE_SIMPLE = Pattern.compile(REGEX_MOBILE_SIMPLE);

    /**
     * 正则：手机号（精确）, 已知3位前缀＋8位数字
     * <p>
     * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
     * </p>
     * <p>
     * 联通：130、131、132、145、155、156、175、176、185、186
     * </p>
     * <p>
     * 电信：133、153、173、177、180、181、189
     * </p>
     * <p>
     * 全球星：1349
     * </p>
     * <p>
     * 虚拟运营商：170
     * </p>
     */
    private static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
    private static final Pattern PATTERN_REGEX_MOBILE_EXACT = Pattern.compile(REGEX_MOBILE_EXACT);

    /**
     * 正则：固定电话号码,可带区号,然后至少6,8位数字
     */
    private static final String REGEX_TEL = "^(\\d{3,4}-)?\\d{6,8}$";
    private static final Pattern PATTERN_REGEX_TEL = Pattern.compile(REGEX_TEL);

    /**
     * 正则：身份证号码15位, 数字且关于生日的部分必须正确
     */
    private static final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    private static final Pattern PATTERN_REGEX_ID_CARD15 = Pattern.compile(REGEX_ID_CARD15);

    /**
     * 正则：身份证号码18位, 数字且关于生日的部分必须正确
     */
    private static final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    private static final Pattern PATTERN_REGEX_ID_CARD18 = Pattern.compile(REGEX_ID_CARD18);

    /**
     * 正则：邮箱, 有效字符(不支持中文), 且中间必须有@,后半部分必须有.
     */
    private static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    private static final Pattern PATTERN_REGEX_EMAIL = Pattern.compile(REGEX_EMAIL);

    /**
     * 正则：URL, 必须有"://",前面必须是英文,后面不能有空格
     */
    private static final String REGEX_URL = "[a-zA-z]+://[^\\s]*";
    private static final Pattern PATTERN_REGEX_URL = Pattern.compile(REGEX_URL);

    /**
     * 正则：yyyy-MM-dd格式的日期校验,已考虑平闰年
     */
    private static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    private static final Pattern PATTERN_REGEX_DATE = Pattern.compile(REGEX_DATE);

    /**
     * 正则：IP地址
     */
    private static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    private static final Pattern PATTERN_REGEX_IP = Pattern.compile(REGEX_IP);


    /**
     * 正则：车牌号
     */
    private static final String REGEX_CAR = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{3,4}[A-Z0-9挂学警港澳]{1}$";
    private static final Pattern PATTERN_REGEX_CAR = Pattern.compile(REGEX_CAR);


    /**
     * 正则：人名
     */
    private static final String REGEX_NAME = "^([\\u4e00-\u9fa5]{1,20}|[a-zA-Z\\.\\s]{1,20})$";
    private static final Pattern PATTERN_REGEX_NAME = Pattern.compile(REGEX_NAME);

    /**
     * 正则：mac地址
     */
    private static final String REGEX_MAC = "([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}";
    private static final Pattern PATTERN_REGEX_MAC = Pattern.compile(REGEX_MAC);


    //封装方法：

    /**
     * 验证手机号（简单）
     */
    public static boolean isMobileSimple(String str) {
        return isMatch(PATTERN_REGEX_MOBILE_SIMPLE, str);
    }

    /**
     * 验证手机号（精确）
     */
    public static boolean isMobileExact(String str) {
        return isMatch(PATTERN_REGEX_MOBILE_EXACT, str);
    }

    /**
     * 验证固定电话号码
     */
    public static boolean isTel(String str) {
        return isMatch(PATTERN_REGEX_TEL, str);
    }

    /**
     * 验证15或18位身份证号码
     */
    public static boolean isIdCard(String str) {
        return isMatch(PATTERN_REGEX_ID_CARD15, str) || isMatch(PATTERN_REGEX_ID_CARD18, str);
    }

//    /**
//     * 验证邮箱
//     */
//    public static boolean isEmail( String str) {
//        return isMatch(PATTERN_REGEX_EMAIL, str);
//    }
//
//    /**
//     * 验证URL
//     */
//    public static boolean isUrl( String str) {
//        return isMatch(PATTERN_REGEX_URL, str);
//    }

    /**
     * 验证yyyy-MM-dd格式的日期校验,已考虑平闰年
     */
    public static boolean isDate(String str) {
        return isMatch(PATTERN_REGEX_DATE, str);
    }

    /**
     * 验证IP地址
     */
    public static boolean isIp(String str) {
        return isMatch(PATTERN_REGEX_IP, str);
    }

    /**
     * 验证车牌号
     */
    public static boolean isCar(String str) {
        return isMatch(PATTERN_REGEX_CAR, str);
    }

    /**
     * 验证人名
     */
    public static boolean isName(String str) {
        return isMatch(PATTERN_REGEX_NAME, str);
    }

    /**
     * 验证mac
     */
    public static boolean isMac(String str) {
        return isMatch(PATTERN_REGEX_MAC, str);
    }

    public static boolean isMatch(Pattern pattern, String str) {
        return StringUtil.isNotEmpty(str) && pattern.matcher(str).matches();
    }

    /**
     * 正则表达式，匹配中国固定电话号码
     *
     * @param phoneNumber "010-12345678";
     * @return
     */
    public static boolean isValidChinaPhoneNumber(String phoneNumber) {
        // 正则表达式，匹配中国固定电话号码
        String regex = "^(0\\d{2,3}-\\d{7,8})(-(\\d{1,4}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}