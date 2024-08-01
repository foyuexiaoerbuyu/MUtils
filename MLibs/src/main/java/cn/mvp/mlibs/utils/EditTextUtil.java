package cn.mvp.mlibs.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

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

    /** 0：限制只能输入正整数（开头不包括包括0）； */
    public static int INPUT_NUMBER_TYPE_START_NO_0 = 0;
    /** 1：限制只能输入正整数（包括0）； */
    public static int INPUT_NUMBER_TYPE_NORMAL = 1;


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

    /** 5：限制只能输入正负整数（包括0） */
    public static int INPUT_NUMBER_TYPE_POSITIVE_NEGATIVE_INT = 5;
    /** 2：限制只能输入小数 */
    public static int INPUT_NUMBER_TYPE_FRACTIONAL = 2;
    /** 3：限制只能输入金额数 */
    public static int INPUT_NUMBER_TYPE_FRACTIONAL_MONEY = 3;
    /** 4：保留特定长度的小数点后几位。 */
    public static int INPUT_NUMBER_TYPE_FRACTIONAL_3 = 4;//保留3位小数

    /**
     * 限制只能输入数字
     */
    public static void setOnlyFilterNumber0_9(EditText editText) {
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
     * 限制只能输入数字 包含负数
     */
    public static void setOnlyFilterNumber(EditText editText, boolean allowDecimal) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if (!isValidInput(inputText, allowDecimal)) {
                    // 输入无效，移除最后输入的字符
                    editText.removeTextChangedListener(this);
                    editText.setText(inputText.length() > 0 ? inputText.substring(0, inputText.length() - 1) : "");
                    editText.setSelection(editText.getText().length());
                    editText.addTextChangedListener(this);
                }
            }

            private boolean isValidInput(String input, boolean allowDecimal) {
                if (input.isEmpty()) {
                    return true; // 空字符串是有效输入
                }
                if (allowDecimal) {
                    // 允许输入正数、负数和小数
                    return input.matches("-?\\d*\\.?\\d*");
                } else {
                    // 允许输入正数、负数但不允许小数
                    return input.matches("-?\\d*");
                }
            }
        });
    }

    /**
     * 推荐使用
     *
     * @param type 0：限制只能输入正整数（开头不包括包括0）；
     *             int INPUT_NUMBER_TYPE_START_NO_0 = 0;
     *             1：限制只能输入正整数（包括0）；
     *             int INPUT_NUMBER_TYPE_NORMAL = 1;
     *             5：限制只能输入正负整数（包括0）
     *             int INPUT_NUMBER_TYPE_POSITIVE_NEGATIVE_INT = 5;
     *             2：限制只能输入小数
     *             int INPUT_NUMBER_TYPE_FRACTIONAL = 2;
     *             3：限制只能输入金额数
     *             int INPUT_NUMBER_TYPE_FRACTIONAL_MONEY = 3;
     *             4：保留特定长度的小数点后几位。
     *             int INPUT_NUMBER_TYPE_FRACTIONAL_3 = 4;//保留3位小数
     */
    public static void setInputNumberType(EditText editText, final int type) {
        InputFilter[] filters = new InputFilter[1];
        switch (type) {
            case 0: // 限制只能输入正整数
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // 只允许输入数字
                        String regex = "[0-9]+";
                        if (!Pattern.matches(regex, source)) {
                            return "";
                        }
                        // 不允许第一位输入0
                        if (dest.length() == 0 && source.toString().equals("0")) {
                            return "";
                        }
                        return null;
                    }
                };
                break;
            case 1: // 限制只能输入整数（包括0）
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // 只允许输入数字和负号
                        String regex = "[-]?[0-9]+";
                        if (!Pattern.matches(regex, source)) {
                            return "";
                        }
                        // 不允许第一位输入负号
                        if (dest.length() == 0 && source.toString().equals("-")) {
                            return "";
                        }
                        return null;
                    }
                };
                break;
            case 5: // 限制只能输入正负整数（包括0）
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        String input = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);

                        if (input.length() == 1 && input.equals("-")) {
                            return null; // 允许输入负号
                        }

                        if (input.length() > 1 && input.startsWith("00")) {
                            return ""; // 如果输入以00开头，则不接受输入
                        }

                        if (input.length() > 1 && input.startsWith("-00")) {
                            return ""; // 如果输入以-00开头，则不接受输入
                        }

                        if (!input.matches("^-?\\d*\\.?\\d*$")) {
                            return ""; // 不符合正负数和0的要求，不接受输入
                        }

                        return null; // 允许输入
                    }
                };
                break;
            case 2: // 限制只能输入小数（包括负小数）
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // 只允许输入数字、小数点和负号
                        String regex = "[-]?[0-9]+(\\.[0-9]*)?";
                        String inputText = dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()).toString();
                        if (!Pattern.matches(regex, inputText)) {
                            return "";
                        }
                        // 不允许第一位输入负号
                        if (dest.length() == 0 && source.toString().equals("-")) {
                            return "";
                        }
                        // 不允许多个小数点
                        if (source.toString().equals(".") && dest.toString().contains(".")) {
                            return "";
                        }
                        // 不允许第一位输入0，并且第二位不是小数点
                        if (dest.length() == 1 && dest.toString().equals("0") && !source.toString().equals(".")) {
                            return "";
                        }
//                        // 处理类似 "000.12" 这样的情况
//                        String[] parts = inputText.split("\\.");
//                        if (parts.length > 1 && parts[1].startsWith("0") && !parts[1].equals("0")) {
//                            return "";
//                        }
                        return null;
                    }
                };
                break;
            case 3: // 限制只能输入金额数（保留两位小数的非负数）
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // 只允许输入数字、小数点和删除键
                        if (!Pattern.matches("[0-9.]*", source)) {
                            return "";
                        }
                        // 只允许输入一个小数点
                        if (source.toString().equals(".") && dest.toString().contains(".")) {
                            return "";
                        }
                        // 限制小数点后最多两位
                        String[] split = dest.toString().split("\\.");
                        if (split.length > 1 && split[1].length() == 2 && end > 0) {
                            return "";
                        }
                        // 不允许输入负数
                        if (dest.toString().contains("-") && source.toString().contains("-")) {
                            return "";
                        }
                        // 不允许第一位输入0，并且第二位不是小数点
                        if (dest.length() == 1 && dest.toString().equals("0") && !source.toString().equals(".")) {
                            return "";
                        }
                        return null;
                    }
                };
                break;
            case 4: // 保留特定长度的小数点后几位
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // 只允许输入数字、小数点和删除键
                        if (!Pattern.matches("[0-9.]*", source)) {
                            return "";
                        }
                        // 只允许输入一个小数点
                        if (source.toString().equals(".") && dest.toString().contains(".")) {
                            return "";
                        }
                        // 限制小数点后最多指定位数
                        String[] split = dest.toString().split("\\.");
                        if (split.length > 1 && split[1].length() == 3 && end > 0) { // 这里假设只能输入三位小数(split[1].length() == 3)
                            return "";
                        }
                        // 不允许输入负数
                        if (dest.toString().contains("-") && source.toString().contains("-")) {
                            return "";
                        }
                        // 不允许第一位输入0，并且第二位不是小数点
                        if (dest.length() == 1 && dest.toString().equals("0") && !source.toString().equals(".")) {
                            return "";
                        }
                        return null;
                    }
                };
                break;
            default:
                break;
        }
        editText.setFilters(filters);
    }

    /**
     * 限制输入两位小数
     */
    private void editTextLimitTwoDecimalPlaces(@NonNull final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            boolean deleteLastChar;// 是否需要删除末尾

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    // 如果点后面有超过三位数值,则删掉最后一位
                    int length = s.length() - s.toString().lastIndexOf(".");
                    // 说明后面有三位数值
                    deleteLastChar = length >= 4;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                if (deleteLastChar) {
                    // 设置新的截取的字符串
                    editText.setText(s.toString().substring(0, s.toString().length() - 1));
                    // 光标强制到末尾
                    editText.setSelection(editText.getText().length());
                }
                // 以小数点开头，前面自动加上 "0"
                if (s.toString().startsWith(".")) {
                    editText.setText("0" + s);
                    editText.setSelection(editText.getText().length());
                }
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editText1 = (EditText) v;
                // 以小数点结尾，去掉小数点
                if (!hasFocus && editText1.getText() != null && editText1.getText().toString().endsWith(".")) {
                    editText.setText(editText1.getText().subSequence(0, editText1.getText().length() - 1));
                    editText.setSelection(editText.getText().length());
                }
            }
        });

    }


}