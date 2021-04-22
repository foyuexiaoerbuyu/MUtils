package cn.mvp.acty;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;

import cn.mvp.MainActivity;
import cn.mvp.R;
import cn.mvp.mlibs.utils.DeviceUtils;

/**
 * 快捷方式设置类
 * 最多支持 5 个快捷方式
 *长按集成
 *	支付宝扫一扫
 *	微信扫一扫
 *	我的名片(微信)
 *	首付款(微信)
 *	首付款(支付宝)
 *	乘车码(微信)
 *	乘车码(支付宝)
 * https://blog.csdn.net/m0_37218227/article/details/84071043
 * https://www.bbsmax.com/A/pRdBjjednx/
 */
public class ShortcutSettingActy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_setting_acty);
        //要确保API Level 大于等于 25才可以创建动态shortcut，否则会报异常。
        initDynamicShortcuts();
    }

    /**
     * 为App创建动态Shortcuts
     */
    private void initDynamicShortcuts() {
        if (DeviceUtils.is7_1()) {
            //①、创建动态快捷方式的第一步，创建ShortcutManager
            ShortcutManager scManager = null;

            scManager = getSystemService(ShortcutManager.class);
            //②、构建动态快捷方式的详细信息
            ShortcutInfo scInfoOne = new ShortcutInfo.Builder(this, "dynamic_one")
                    .setShortLabel("Dynamic Web site")
                    .setLongLabel("to open Dynamic Web Site")
//                .setIcon(Icon.createWithResource(this, R.mipmap.tool_music_icon))
                    .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baidu.com")))
                    .build();

            ShortcutInfo scInfoTwo = new ShortcutInfo.Builder(this, "dynamic_two")
                    .setShortLabel("Dynamic Activity")
                    .setLongLabel("to open dynamic one activity")
//                .setIcon(Icon.createWithResource(this, R.mipmap.tool_luck_icon))
                    .setIntents(new Intent[]{
                            new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),//加该FLAG的目的是让MainActivity作为根activity，清空已有的任务
//                        new Intent(DynamicASOneActivity.ACTION)
                    })
                    .build();
            //③、为ShortcutManager设置动态快捷方式集合
            scManager.setDynamicShortcuts(Arrays.asList(scInfoOne, scInfoTwo, scInfoTwo, scInfoTwo, scInfoTwo));

            //如果想为两个动态快捷方式进行排序，可执行下面的代码
            ShortcutInfo dynamicWebShortcut = new ShortcutInfo.Builder(this, "dynamic_one")
                    .setRank(0)
                    .build();
            ShortcutInfo dynamicActivityShortcut = new ShortcutInfo.Builder(this, "dynamic_two")
                    .setRank(1)
                    .build();

            //④、更新快捷方式集合
            scManager.updateShortcuts(Arrays.asList(dynamicWebShortcut, dynamicActivityShortcut));
        }
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, ShortcutSettingActy.class);
        context.startActivity(intent);
    }
}