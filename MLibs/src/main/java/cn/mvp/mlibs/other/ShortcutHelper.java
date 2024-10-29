package cn.mvp.mlibs.other;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 长按启动图标动态添加快捷方式工具类
 */
public class ShortcutHelper {

    private Context context;
    private ShortcutManager shortcutManager;

    public ShortcutHelper(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager = context.getSystemService(ShortcutManager.class);
        }
    }

    /**
     * 添加长按快捷方式 打开网页
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void addDynamicShortcut(String id, String shortLabel, String longLabel, int iconResId, String url) {
        addDynamicShortcut(id, shortLabel, longLabel, iconResId, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * 添加长按快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void addDynamicShortcut(String id, String shortLabel, String longLabel, int iconResId, Intent intent) {
        if (shortcutManager == null) return;

        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, id)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, iconResId))
                .setIntent(intent)
                .build();

        List<ShortcutInfo> shortcuts = new ArrayList<>();
        shortcuts.add(shortcut);
        shortcutManager.setDynamicShortcuts(shortcuts);
    }

    /**
     * 创建快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public ShortcutInfo creatDynamicShortcuts(String id, String shortLabel, String longLabel, int iconResId, String url) {
        if (shortcutManager == null) return null;
        return new ShortcutInfo.Builder(context, id)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, iconResId))
                .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                .build();
    }

    /**
     * 创建快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public ShortcutInfo creatDynamicShortcuts(String id, String shortLabel, String longLabel, int iconResId, Intent intent) {
        if (shortcutManager == null) return null;
        return new ShortcutInfo.Builder(context, id)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, iconResId))
                .setIntent(intent)
                .build();
    }

    /**
     * 添加长按快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void addDynamicShortcuts(List<ShortcutInfo> shortcutInfos) {
        if (shortcutManager == null) return;
        shortcutManager.setDynamicShortcuts(shortcutInfos);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void removeDynamicShortcut(List<String> ids) {
        if (shortcutManager == null) return;
        shortcutManager.removeDynamicShortcuts(ids);
    }

    /**
     * 更新单个动态快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void updateDynamicShortcut(ShortcutInfo shortcutInfo) {
        if (shortcutManager == null) return;
        shortcutManager.updateShortcuts(Arrays.asList(shortcutInfo));
    }

    /**
     * 更新多个动态快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void updateDynamicShortcuts(List<ShortcutInfo> shortcutInfos) {
        if (shortcutManager == null) return;
        shortcutManager.updateShortcuts(shortcutInfos);
    }

    /**
     * 查询所有动态快捷方式
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public List<ShortcutInfo> getAllDynamicShortcuts() {
        if (shortcutManager == null) return Collections.emptyList();
        return shortcutManager.getDynamicShortcuts();
    }
}