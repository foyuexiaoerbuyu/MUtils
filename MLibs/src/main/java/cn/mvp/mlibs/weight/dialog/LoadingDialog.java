package cn.mvp.mlibs.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.TextView;

import cn.mvp.mlibs.R;

public class LoadingDialog {

    private Dialog mDialog;
    private Handler mHandler;
    private TextView mMsgTv;

    private LoadingDialog(Builder builder) {
        mHandler = new Handler(Looper.getMainLooper());
        initDialog(builder.context, builder.isCancelable, builder.msg);
        setOnCancelListener(builder.cancelListener);
        setOnDismissListener(builder.dismissListener);
        setOnKeyListener(builder.keyListener);
    }

    private void initDialog(Context context, boolean isCancelable, String msg) {
        if (mDialog == null) {
            mDialog = new Dialog(context);
            Window window = mDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
            mDialog.setContentView(R.layout.loading_dialog_progress);
            mMsgTv = mDialog.findViewById(R.id.loading_dialog_progress_msg);
            mDialog.setCancelable(isCancelable);
        }
        updateMsg(msg);
    }

    public void show() {
        mHandler.post(() -> {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        });
    }

    public void dismiss() {
        mHandler.post(() -> {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        });
    }

    public void updateMsg(String msg) {
        mHandler.post(() -> {
            if (mMsgTv != null) {
                mMsgTv.setText(msg);
            }
        });
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mHandler.post(() -> {
            if (mDialog != null) {
                mDialog.setOnCancelListener(listener);
            }
        });
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mHandler.post(() -> {
            if (mDialog != null) {
                mDialog.setOnDismissListener(listener);
            }
        });
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener listener) {
        mHandler.post(() -> {
            if (mDialog != null) {
                mDialog.setOnKeyListener(listener);
            }
        });
    }

    public static class Builder {
        private final Context context;
        private boolean isCancelable = false;
        private String msg = "加载中...";
        private DialogInterface.OnCancelListener cancelListener;
        private DialogInterface.OnDismissListener dismissListener;
        private DialogInterface.OnKeyListener keyListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setMessage(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener listener) {
            this.keyListener = listener;
            return this;
        }

        public LoadingDialog build() {
            return new LoadingDialog(this);
        }
    }
}