package cn.mvp.utils;

import com.tencent.mmkv.MMKV;

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
        if (str == null) return new CfgInfo();
        return GsonUtil.fromJson(str, CfgInfo.class);
    }

    /**
     * @return 提示音效开关
     */
    private static void saveCfgInfo(CfgInfo cfgInfo) {
        putStr(SpUtils.KEY_CFG_CONFIG, GsonUtil.toJson(cfgInfo));
    }


    public static void saveNewIp(String newIp) {
        CfgInfo cfgInfo = getCfgInfo();
        cfgInfo.addConnectIp(newIp);
        saveCfgInfo(cfgInfo);
    }

    public static void setLastConnIp(String newIp) {
        CfgInfo cfgInfo = getCfgInfo();
        cfgInfo.setLastConnIp(newIp);
        saveCfgInfo(cfgInfo);
    }

    public static void delIp(String ip) {
        CfgInfo cfgInfo = getCfgInfo();
        cfgInfo.getConnectIps().remove(ip);
        saveCfgInfo(cfgInfo);
    }
}