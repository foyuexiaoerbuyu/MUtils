package cn.mvp.acty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import cn.mvp.R;

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

    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, ShortcutSettingActy.class);
        context.startActivity(intent);
    }
}