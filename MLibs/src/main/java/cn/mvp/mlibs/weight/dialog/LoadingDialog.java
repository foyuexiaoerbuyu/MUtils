package cn.mvp.mlibs.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import cn.mvp.mlibs.R;

public class LoadingDialog {

    private Dialog sDialog;

    public void showProgress(Context context, boolean isCancelable, String msg) {
        if (sDialog == null) init(context, isCancelable, msg);
        sDialog.show();
    }

    public void showProgress(Context context, boolean isCancelable) {
        if (sDialog == null) init(context, isCancelable, "加载中...");
        sDialog.show();
    }

    public void showProgress(Context context) {
        if (sDialog == null) init(context, false, "加载中...");
        sDialog.show();
    }

    public void dismissProgress() {
        if (sDialog.isShowing()) sDialog.dismiss();
    }

    private void init(Context context, boolean isCancelable, String msg) {
        sDialog = new Dialog(context);
        // 修改对话框默认背景为透明，因为不这么设置的话，对话框默认是白色的
        // 然后你自定义的背景是黑色且有圆角，相信我，你不想看到的
        Window window = sDialog.getWindow();
        if (window != null) window.setBackgroundDrawableResource(android.R.color.transparent);
        // 设置自己编写的布局文件，即刚才有 ProgressBar 和 TextView 的那个布局文件
        sDialog.setContentView(R.layout.loading_dialog_progress);
        ((TextView) sDialog.findViewById(R.id.loading_dialog_progress_msg)).setText(msg);
        // 设置不可点击或点按返回键取消对话框，这样相当于禁止了用户与界面的交互
        // 实际情况根据需求而定
        sDialog.setCancelable(isCancelable);
//        sDialog.setCanceledOnTouchOutside(false);
    }
}
