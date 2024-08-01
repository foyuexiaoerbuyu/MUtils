package cn.mvp.mlibs.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import cn.mvp.mlibs.R;

public class LoadingDialog {

    private Dialog mDialog;
    private Handler mHandler;
    private DialogInterface.OnCancelListener mCancelListener;
    private DialogInterface.OnDismissListener mDismissListener;
    private DialogInterface.OnKeyListener mKeyListener;
    private TextView mMsgTv;


    public void showProgress(Context context, boolean isCancelable, String msg) {
        mHandler = new Handler(context.getMainLooper());
        mHandler.post(() -> {
            if (mDialog == null) {
                mDialog = new Dialog(context);
                // 修改对话框默认背景为透明，因为不这么设置的话，对话框默认是白色的
                // 然后你自定义的背景是黑色且有圆角，相信我，你不想看到的
                Window window = mDialog.getWindow();
                if (window != null)
                    window.setBackgroundDrawableResource(android.R.color.transparent);
                // 设置自己编写的布局文件，即刚才有 ProgressBar 和 TextView 的那个布局文件
                mDialog.setContentView(R.layout.loading_dialog_progress);
                mMsgTv = mDialog.findViewById(R.id.loading_dialog_progress_msg);
                mMsgTv.setText(msg);
                // 设置不可点击或点按返回键取消对话框，这样相当于禁止了用户与界面的交互
                // 实际情况根据需求而定
                mDialog.setCancelable(isCancelable);
//        sDialog.setCanceledOnTouchOutside(false);
            }
            mDialog.show();
        });
    }

    public void showProgress(Context context, boolean isCancelable) {
        showProgress(context, isCancelable, "加载中...");
    }

    public void showProgress(Context context) {
        showProgress(context, false, "加载中...");
    }

    public void dismissProgress() {
        if (mDialog.isShowing()) mDialog.dismiss();
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mCancelListener = listener;
        if (mDialog != null) {
            mDialog.setOnCancelListener(mCancelListener);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mDismissListener = listener;
        if (mDialog != null) {
            mDialog.setOnDismissListener(mDismissListener);
        }
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener listener) {
        mKeyListener = listener;
        if (mDialog != null) {
            mDialog.setOnKeyListener(mKeyListener);
        }
    }

    public void updateMsg(String msg) {
        mHandler.post(() -> {
            if (mMsgTv != null) mMsgTv.setText(msg);
        });
    }
}
