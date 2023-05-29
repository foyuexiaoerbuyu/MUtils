package cn.mvp.mlibs.utils;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.mvp.mlibs.other.CheckException;

/**
 * Created by ray on 2018/1/19.
 * String 字符串相关类方法
 */

public class StringUtil {

    /**
     * 作用:                  去除来都结尾的逗号
     *
     * @param stringBuilder 要操作的字符串
     * @return 截取开头结尾之后的字符串
     */
    public static StringBuilder subStrBuildComma(StringBuilder stringBuilder) {
        if (stringBuilder != null && !"".equals(stringBuilder.toString())) {
            StringBuilder stringBuilder1 = stringBuilder;
            if (!"".equals(stringBuilder.toString())) {
                if (stringBuilder.indexOf(",") == 0) {
                    String substring = stringBuilder.substring(1);
                    stringBuilder1 = new StringBuilder(substring);
                    return subStrBuildComma(stringBuilder1);
                } else if (stringBuilder.lastIndexOf(",") == stringBuilder.length() - 1) {
                    if (stringBuilder.length() >= 1) {
                        String substring = stringBuilder.substring(0, stringBuilder.length() - 1);
                        stringBuilder1 = new StringBuilder(substring);
                        return subStrBuildComma(stringBuilder1);
                    }
                }
            }
            return stringBuilder1;
        }
        return stringBuilder;
    }

    /**
     * 如果参数为null或者"null"字符串返回""空字符串
     * 注:org.apache.commons.lang.StringUtils#clean效果类似不同点在于"null"字符串不会返回""空字符串
     *
     * @param str 要判断的字符串
     * @return 字符串
     */
    public static String valueOf(Object str) {
        return isBlank(String.valueOf(str)) ? "" : String.valueOf(str);
    }

    /**
     * 如果参数为null或者"null"字符串返回""空字符串
     * 注:org.apache.commons.lang.StringUtils#clean效果类似不同点在于"null"字符串不会返回""空字符串
     *
     * @param str 要判断的字符串
     * @return 字符串
     */
    public static String valueOf(String str) {
        return isBlank(str) ? "" : str;
    }

    /** 判断字符串是否有值，如果为null或者是空字符串或者只有空格，则返回true，否则则返回false */
    public static boolean isEmpty(String value) {
        return value == null || "".equalsIgnoreCase(value.trim());
    }

    /** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
    public static boolean isBlank(Object value) {
        return value == null || "".equalsIgnoreCase(value.toString().trim()) || "null".equalsIgnoreCase(value.toString().trim());
    }

    /**
     * 一次只能替换一个属性
     *
     * @param resStr      要操作的网页style
     * @param oldStylePrt 样式属性
     * @return 操作后的网页代码
     */
    public static String optionHtmlStyle(String resStr, String oldStylePrt, String newStylePrt) {
        if (resStr.contains(oldStylePrt)) {
            String temp = resStr.substring(resStr.indexOf(oldStylePrt));
            resStr = resStr.replace(temp.substring(0, temp.indexOf(";") + 1), newStylePrt);
        }
        return resStr;
    }

    /**
     * @param str    源字符串("","null","  ",以上三种都为"")
     * @param defVal 默认字符串
     * @return 字符串为空时的默认值
     */
    public static String defaultIfBlank(String str, String defVal) {
        return isBlank(str) ? defVal : str;
    }

    public static String[] split(String str, String regx) {
        if (str == null || !str.contains(regx)) {
            return new String[0];
        }
        return str.split(regx);
    }

    /**
     * 作用:       去除开头结尾的逗号
     *
     * @param str 要操作的字符串
     * @return 截取后的字符串
     */
    private String subStrComma(String str) {
        if (!"".equals(str) && str != null) {
            if (str.indexOf(",") == 0) {
                String substring = str.substring(1);
                return subStrComma(substring);
            } else if (str.lastIndexOf(",") == str.length() - 1) {
                String substring = str.substring(0, str.length() - 1);
                return subStrComma(substring);
            }
        }
        return str;
    }

    /**
     * 作用:       移除开头结尾,指定的字符串
     *
     * @param str    要操作的字符串
     * @param symbol 要去除的字符
     * @return 截取后的字符串
     */
    private String clearSymbol(String str, String symbol) {
        if (str != null && !"".equals(str)) {
            if (str.indexOf(symbol) == 0) {
                String substring = str.substring(1);
                return clearSymbol(substring, symbol);
            } else if (str.lastIndexOf(symbol) == str.length() - 1) {
                String substring = str.substring(0, str.length() - 1);
                return clearSymbol(substring, symbol);
            }
        }
        return str;
    }

    /**
     * TeXView显示html
     *
     * @param htmlStr htmlStr
     * @return str
     */
    public static CharSequence fromHtml(String htmlStr) {
        if (isBlank(htmlStr)) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(htmlStr);
        }
    }

    // Equals
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two CharSequences, returning {@code true} if they represent
     * equal sequences of characters.</p>
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null}
     * references are considered to be equal. The comparison is <strong>case sensitive</strong>.</p>
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param cs1 the first CharSequence, may be {@code null}
     * @param cs2 the second CharSequence, may be {@code null}
     * @return {@code true} if the CharSequences are equal (case-sensitive), or both {@code null}
     * @see Object#equals(Object)
     * @see #equalsIgnoreCase(CharSequence, CharSequence)
     * @since 3.0 Changed signature from equals(String, String) to equals(CharSequence, CharSequence)
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        // Step-wise comparison
        final int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    //把String转化为float
    public static float convertToFloat(String number, float defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    //把String转化为double
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    //把String转化为int
    public static int convertToInt(String number, int defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChineseSimple(String str) throws CheckException {

        if (StringUtils.isEmpty(str)) {
            throw new CheckException("字符串不能为空");
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * @return 去除字符串所有中文
     */
    public static String replaceAllChinese(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        }
        //// 中文正则
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(str).replaceAll("");
    }

    /**
     * 字符串是否包含中文及中文符号
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese2symbol(String str) throws CheckException {

        if (StringUtils.isEmpty(str)) {
            throw new CheckException("字符串不能为空");
        }
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * @return 字符串或空字符串
     */
    public static String str(Object str) {
        return str == null ? "" : str.toString();
    }

    /**
     * @return 字符串或空字符串
     */
    public static String strNull(String value) {
        return checkStringIsNull(value) ? "" : value;
    }

    /**
     * 检查是不是null  是不是空串  是不是"null"
     *
     * @param checkStr
     * @return 非null "null" 值
     */
    public static boolean checkStringIsNull(String checkStr) {
        return checkStr == null || checkStr.isEmpty() || checkStr.equals("null");
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 去掉所有空格
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null) {
            return "";
        }
        return str.replace(" ", "");
    }

}
