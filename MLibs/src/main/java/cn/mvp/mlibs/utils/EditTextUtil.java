package cn.mvp.mlibs.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EditText输入限制 InputFilter 封装工具类
 * 使用示例：
 * <p>
 * EditText editText = findViewById(R.id.editText);
 * // 限制汉字输入长度
 * InputFilterUtil.setChineseInputLengthFilter(editText, 10);
 * // 限制英文及数字输入长度
 * InputFilterUtil.setEnglishAndNumberInputLengthFilter(editText, 10);
 * // 限制只能输入英文
 * InputFilterUtil.setOnlyFilterEnglish(editText);
 * // 限制只能输入数字
 * InputFilterUtil.setOnlyFilterNumber(editText);
 * // 限制只能输入英文和数字
 * InputFilterUtil.setOnlyFilterEnglishAndNumber(editText);
 * 注意：这些方法是互斥的，只能使用其中之一。如果需要同时设置多个过滤规则，请自行修改setFilters()方法中的参数。
 */
public class EditTextUtil {

    /**
     * 限制汉字输入长度
     *
     * @param editText
     * @param maxLength
     */
    public static void setChineseInputLengthFilter(EditText editText, int maxLength) {
        if (editText == null) return;
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int count = 0;
                for (int i = 0; i < dest.length(); i++) {
                    if (Character.toString(dest.charAt(i)).matches("[\u4e00-\u9fa5]")) {
                        count += 2;
                    } else {
                        count += 1;
                    }
                }
                if (count >= maxLength) {
                    return "";
                } else {
                    return source;
                }
            }
        }});
    }

    /**
     * 限制英文及数字输入长度
     */
    public static void setEnglishAndNumberInputLengthFilter(EditText editText, int maxLength) {
        if (editText == null) return;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    /**
     * 限制英文及数字输入长度
     */
    public static void setMoney(EditText editText) {
        if (editText == null) return;
        // 设置只能输入金额的InputFilter
        editText.setFilters(new InputFilter[]{new InputFilter() {
            //输入的最大金额
            private static final int MAX_VALUE = Integer.MAX_VALUE;
            //小数点后的位数
            private static final int POINTER_LENGTH = 2;
            private static final String POINTER = ".";
            private static final String ZERO = "0";
            final Pattern mPattern = Pattern.compile("([0-9]|\\.)*");

            /**
             * @param source    新输入的字符串
             * @param start     新输入的字符串起始下标，一般为0
             * @param end       新输入的字符串终点下标，一般为source长度-1
             * @param dest      输入之前文本框内容
             * @param dstart    原内容起始坐标，一般为0
             * @param dend      原内容终点坐标，一般为dest长度-1
             * @return 输入内容
             */
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String sourceText = source.toString();
                String destText = dest.toString();       //验证删除等按键
                if (TextUtils.isEmpty(sourceText)) {
                    return "";
                }
                Matcher matcher = mPattern.matcher(source);
                //已经输入小数点的情况下，只能输入数字
                if (destText.contains(POINTER)) {
                    if (!matcher.matches()) {
                        return "";
                    } else {
                        if (POINTER.equals(source.toString())) {  //只能输入一个小数点
                            return "";
                        }
                    }           //验证小数点精度，保证小数点后只能输入两位
                    int index = destText.indexOf(POINTER);
                    int length = dend - index;
                    if (length > POINTER_LENGTH) {
                        return dest.subSequence(dstart, dend);
                    }
                } else {
                    /**
                     * 没有输入小数点的情况下，只能输入小数点和数字
                     * 1. 首位不能输入小数点
                     * 2. 如果首位输入0，则接下来只能输入小数点了
                     */
                    if (!matcher.matches()) {
                        return "";
                    } else {
                        if ((POINTER.equals(source.toString())) && TextUtils.isEmpty(destText)) {  //首位不能输入小数点
                            return "";
                        } else if (!POINTER.equals(source.toString()) && ZERO.equals(destText)) { //如果首位输入0，接下来只能输入小数点
                            return "";
                        }
                    }
                }       //验证输入金额的大小
                double sumText = Double.parseDouble(destText + sourceText);
                if (sumText > MAX_VALUE) {
                    return dest.subSequence(dstart, dend);
                }
                return dest.subSequence(dstart, dend) + sourceText;
            }
        }});
    }

    /**
     * 限制只能输入英文
     */
    public static void setOnlyFilterEnglish(EditText editText) {
        if (editText == null) return;
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find()) {
                    return "";
                }
                return null;
            }
        }});
    }

    /**
     * 限制只能输入数字
     */
    public static void setOnlyFilterNumber(EditText editText) {
        if (editText == null) return;
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("^[0-9]+$");
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find()) {
                    return "";
                }
                return null;
            }
        }});
    }

    /**
     * 限制只能输入英文和数字
     */
    public static void setOnlyFilterEnglishAndNumber(EditText editText) {
        if (editText == null) return;
        editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find()) {
                    return "";
                }
                return null;
            }
        }});
    }

    /**
     * 根据字符串限制
     *
     * @param digits "0123456789.-"
     */
    public static void setDigitsInputFilter(EditText editText, String digits) {
        if (editText == null) return;
        InputFilter[] filters = {(source, start, end, dest, dstart, dend) -> {
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isDigit(c) && (digits == null || digits.indexOf(c) != -1)) {
                    builder.append(c);
                }
            }
            return builder.toString();
        }};
        editText.setFilters(filters);
    }

}