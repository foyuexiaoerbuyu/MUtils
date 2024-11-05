package cn.mvp.mlibs.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * 获取特定语言字符串
 * // 获取英文字符串
 * String enString = LanguageHelper.getStringForLocale(this, R.string.welcome_message, new Locale("en"));
 * <p>
 * // 获取中文字符串
 * String zhString = LanguageHelper.getStringForLocale(this, R.string.welcome_message, new Locale("zh"));
 * <p>
 * // 打印结果
 * Log.d("LanguageHelper", "English: " + enString);
 * Log.d("LanguageHelper", "Chinese: " + zhString);
 */
public class LanguageHelper {

    public static Resources getResourcesForLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        return context.createConfigurationContext(configuration).getResources();
    }

    public static String getStringForLocale(Context context, int resourceId, Locale locale) {
        Resources resources = getResourcesForLocale(context, locale);
        return resources.getString(resourceId);
    }
}