package cn.mvp.utils;

import com.tencent.mmkv.MMKV;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import cn.mvp.global.CfgInfo;
import cn.mvp.mlibs.utils.GsonUtil;

public class SpUtils {

    /*配置: 配置*/
    public static final String KEY_CFG_CONFIG = "CONFIG";

    public static void putStr(String key, String val) {
        MMKV.defaultMMKV().encode(key, val);
    }

    public static String getStr(String key, String defVal) {
        return MMKV.defaultMMKV().decodeString(key, defVal);
    }

    public static void putBoolean(String key, boolean val) {
        MMKV.defaultMMKV().encode(key, val);
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return MMKV.defaultMMKV().decodeBool(key, defVal);
    }

    public static void remove(String key) {
        MMKV.defaultMMKV().remove(key);
    }

    /**
     * @return 提示音效开关
     */
    public static CfgInfo getCfgInfo() {
        String str = getStr(SpUtils.KEY_CFG_CONFIG, null);
        if (str == null) return null;
        return GsonUtil.fromJson(str, CfgInfo.class);
    }


}