package cn.mvp.mlibs.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;

import cn.mvp.mlibs.R;
import cn.mvp.mlibs.utils.UIUtils;

/**
 * 底部弹出对话框
 */
public class BottomDialog {
    private final View mRootView;
    private Dialog dialog;
    private Context context;
    private int mLayoutResId;
    private boolean isShowShadow = true;
    private float shadowTransparency = 0.5f;
    private int shadowColor;
    private View contentView;

    public BottomDialog(Context context, int layoutResId) {
        this.context = context;
        mLayoutResId = layoutResId;
        mRootView = UIUtils.inflate(mLayoutResId);
    }

    public void showDialog(IClickCallBack iClickCallBack, int... clickIds) {
        dismissDialog();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(mRootView);
        // 设置弹框位置为底部
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.getWindow().setWindowAnimations(R.style.BottomDialogAnim);

        // 设置背景透明
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置是否显示全屏阴影遮罩及颜色
        if (isShowShadow) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(shadowTransparency); // 遮罩透明度，默认是0.5
            if (shadowColor > 0) {
                dialog.getWindow().setDimAmount(shadowColor); // 遮罩颜色
            }
        } else {
            dialog.getWindow().setDimAmount(Color.TRANSPARENT);
        }
        if (clickIds != null) {
            for (int clickId : clickIds) {
                dialog.findViewById(clickId).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iClickCallBack.click(clickId, v, mRootView);
                    }
                });
            }
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * @param showShadow 是否显示遮罩
     */
    public void setShowShadow(boolean showShadow) {
        isShowShadow = showShadow;
    }

    /**
     * @param transparency 遮罩透明度 默认0.5
     */
    public void setShadowTransparency(float transparency) {
        this.isShowShadow = true;
        this.shadowTransparency = transparency;
    }

    /**
     * @param shadowColor 遮罩颜色
     */
    public void setShadowColor(@ColorRes int shadowColor) {
        this.isShowShadow = true;
        this.shadowColor = shadowColor;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public interface IClickCallBack {
        void click(int clickId, View clickView, View rootView);
    }

    public View getRootView() {
        return mRootView;
    }
}
