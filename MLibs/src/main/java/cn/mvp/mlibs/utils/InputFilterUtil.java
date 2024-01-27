package cn.mvp.mlibs.utils;

import android.text.InputFilter;
import android.text.Spanned;
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
public class InputFilterUtil {

    /**
     * 限制汉字输入长度
     *
     * @param editText
     * @param maxLength
     */
    public static void setChineseInputLengthFilter(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{getChineseInputLengthFilter(maxLength)});
    }

    /**
     * 限制英文及数字输入长度
     */
    public static void setEnglishAndNumberInputLengthFilter(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{getEnglishAndNumberInputLengthFilter(maxLength)});
    }

    /**
     * 限制只能输入英文
     */
    public static void setOnlyFilterEnglish(EditText editText) {
        editText.setFilters(new InputFilter[]{getOnlyFilterEnglish()});
    }

    /**
     * 限制只能输入数字
     */
    public static void setOnlyFilterNumber(EditText editText) {
        editText.setFilters(new InputFilter[]{getOnlyFilterNumber()});
    }

    /**
     * 限制只能输入英文和数字
     */
    public static void setOnlyFilterEnglishAndNumber(EditText editText) {
        editText.setFilters(new InputFilter[]{getOnlyFilterEnglishAndNumber()});
    }

    /**
     * 根据字符串限制
     *
     * @param digits "0123456789.-"
     */
    public static void setDigitsInputFilter(EditText editText, String digits) {
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

    private static InputFilter getChineseInputLengthFilter(final int maxLength) {
        return new InputFilter() {
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
        };
    }

    private static InputFilter getEnglishAndNumberInputLengthFilter(final int maxLength) {
        return new InputFilter.LengthFilter(maxLength);
    }

    private static InputFilter getOnlyFilterEnglish() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find()) {
                    return "";
                }
                return null;
            }
        };
    }

    private static InputFilter getOnlyFilterNumber() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("^[0-9]+$");
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find()) {
                    return "";
                }
                return null;
            }
        };
    }

    private static InputFilter getOnlyFilterEnglishAndNumber() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
                Matcher matcher = pattern.matcher(source.toString());
                if (!matcher.find()) {
                    return "";
                }
                return null;
            }
        };
    }
}