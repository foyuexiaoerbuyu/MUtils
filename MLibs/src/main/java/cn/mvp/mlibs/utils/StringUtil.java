package cn.mvp.mlibs.utils;

import android.os.Build;
import android.text.Html;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.mvp.mlibs.other.CheckException;

/**
 * Created by ray on 2018/1/19.
 * String 字符串相关类方法
 */

public class StringUtil {


    public static String[] split(String str, String regx) {
        if (str == null || !str.contains(regx)) {
            return new String[0];
        }
        return str.split(regx);
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
        if (isEmpty(number)) {
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
        if (isEmpty(number)) {
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
        if (isEmpty(number)) {
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

        if (isEmpty(str)) {
            throw new CheckException("字符串不能为空");
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 字符串是否包含中文及中文符号
     *
     * @param str 待校验字符串
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese2symbol(String str) throws CheckException {

        if (isEmpty(str)) {
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
     * @return 字符串或空字符串(返回第一个不为空的字符串)
     */
    public static String str(String... strs) {
        for (String str : strs) {
            if (str != null) {
                return str;
            }
        }
        return "";
    }

    /**
     * @return 忽略大小写比较
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
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
     * @param checkStr .
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
        return str.trim();
    }


    /**
     * 对数字进行补零操作
     *
     * @param number 数字
     * @param length 期望的字符串长度
     * @return 补零后的字符串
     */
    private static String padZero(int number, int length) {
        String numberStr = String.valueOf(number);
        if (numberStr.length() < length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length - numberStr.length(); i++) {
                sb.append("0");
            }
            sb.append(numberStr);
            return sb.toString();
        } else {
            return numberStr;
        }
    }

    /**
     * 补零方法
     *
     * @param num 补零个数
     * @return 补零后的字符串
     */
    public static String zeroFill(int num) {
        return String.format("%02d", num);
    }

    public static String subStr(String str, int startIndex, int endIndex) {
        if (str == null || str.length() < endIndex || startIndex > endIndex) {
            return str;
        }
        return str.substring(startIndex, endIndex);
    }

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

    /**
     * 判断字符串是否有值，不为空
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        return value == null || "".equalsIgnoreCase(value.trim());
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
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

    public static int parseInt(String str) {
        if (str == null || str.trim().length() == 0) {
            return 0;
        }
        return parseInt(str, 0);
    }

    public static int parseInt(String str, int defVal) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defVal;
        }
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
     * @return 统计出现次数
     */
    public static int countMatches(String str, String target) {
        int count = 0;
        int index = 0;
        while (index != -1) {
            index = str.indexOf(target, index);
            if (index != -1) {
                count++;
                index += target.length();
            }
        }
        return count;
    }

    /**
     * 统计出现次数
     */
    public static int countMatches(String str, char symbol) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == symbol) {
                count++;
            }
        }
        return count;
    }

    /**
     * 按行读取字符串
     */
    public static void readStrByLins(String str, IReadLin iReadLin) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                iReadLin.readLin(line.trim());
            }
        } catch (IOException e) {
            iReadLin.readLinEx(e);
        }
        iReadLin.end();
    }

    /**
     * 首字母转大写
     *
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 首字母转小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
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

    public static String subStr(String str, int index) {
        if (str == null || str.length() < index) {
            return str;
        }
        return str.substring(0, index);
    }

    /** 是否纯数字 */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return str.matches("\\d+"); // 使用正则表达式匹配纯数字
    }

    public interface IReadLin {
        void readLin(String lin);

        default void readLinEx(IOException exception) {

        }

        default void end() {

        }
    }

    /** 匹配字符串中的url */
    public static String matchLink(String str, String def) {
        Pattern pattern = Pattern.compile("(?i)\\b((?:https?|ftp)://|www\\.)[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return def;
    }

    /**
     * 批量 匹配字符串中的url
     * List<Share> allItems = shareService.getAllItems();
     * <p>
     * StringUtil.extractURLs(allItems, Share::getContent, url -> {
     * System.out.println("URL matched: " + url);
     * });
     */
    public static <T> void matchLink(List<T> dataList, Function<T, String> getContentFn, URLMatchCallback callback) {
        for (T data : dataList) {
            String content = getContentFn.apply(data);
            Pattern pattern = Pattern.compile("(?i)\\b((?:https?|ftp)://|www\\.)[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String url = matcher.group();
                callback.onURLMatched(url);
            }
        }
    }

    public interface URLMatchCallback {
        void onURLMatched(String url);
    }


//---------------------------Android独有 start----------------------------------------
//---------------------------Android独有 end----------------------------------------

}
