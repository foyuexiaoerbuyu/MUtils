package cn.mvp.acty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

import java.util.TimerTask;

import butterknife.BindView;
import cn.mvp.R;
import cn.mvp.mlibs.other.TestUtils;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.StatusBarUtil;
import cn.mvp.mlibs.utils.TimerUtils;
import cn.mvp.mlibs.utils.UIUtils;

/**
 * 电量剩余界面
 */
public class ElectricQuantityActivity extends BaseActivity {

    @BindView(R.id.electric_quantity_iv_bg)
    AppCompatImageView electric_quantity_iv_bg;
    @BindView(R.id.electric_quantity_tv_time)
    AppCompatTextView electric_quantity_tv_time;
    private TimerUtils mTimerUtils;

    public static void open(Context context) {
        Intent starter = new Intent(context, ElectricQuantityActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initStateBar() {
//        StatusBarUtil.setColor(this, UIUtils.getColor(R.color.white));
        // 隐藏状态栏
//        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    @Override
    public void initView() {
        mTimerUtils = new TimerUtils();
        mTimerUtils.schedule(new TimerTask() {
            @Override
            public void run() {
                UIUtils.runOnUIThread(() -> electric_quantity_tv_time.setText(DateUtil.formatDate("HH:mm")));
            }
        }, 0, 2000);
        Glide.with(this).load(R.drawable.bg_phone).into(electric_quantity_iv_bg);
        // 全屏显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 仅当缺口区域完全包含在状态栏之中时，才允许窗口延伸到刘海区域显示
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
            // 永远不允许窗口延伸到刘海区域
//            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            // 始终允许窗口延伸到屏幕短边上的刘海区域
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        electric_quantity_iv_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickNum = TestUtils.setClickNum();
                if (clickNum == 1) {
                    Log.i("调试信息", "onClick:  ==1==");
                } else if (clickNum == 2) {
                    Log.i("调试信息", "onClick:  =2===");
                } else if (clickNum == 3) {
                    Log.i("调试信息", "onClick:  ==3==");
                } else {
                    Log.i("调试信息", "onCl充值ick:  " + TestUtils.reSetClickNum());
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimerUtils.cancel();
    }

    @Override
    public int setView() {
        return R.layout.activity_electric_quantity;
    }
}