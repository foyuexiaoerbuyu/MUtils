package cn.mvp.mlibs.weight.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.mvp.mlibs.R;

public class MsgAlertDialog extends AlertDialog implements View.OnClickListener {
    private TextView mTvContent;
    private Button mBtnCancel, mBtnOk;
    private TextView mTvTitle;
    private String mTitle;
    private String mContent;
    private OnOkClickListener mOnOkClickListener;
    private OnCancelClickListener mOnCancelClickListener;

    private MsgAlertDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_msg_alert_dialog);
        mBtnCancel = findViewById(R.id.view_msg_dialog_btn_cancel);
        mBtnOk = findViewById(R.id.view_msg_dialog_btn_ok);
        mTvTitle = findViewById(R.id.view_msg_dialog_tv_title);
        mTvContent = findViewById(R.id.view_msg_dialog_tv_content);

        mTvTitle.setText(mTitle);
        mTvContent.setText(mContent);
        mBtnCancel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.view_msg_dialog_btn_cancel) {
            if (mOnCancelClickListener != null) mOnCancelClickListener.click();
        } else if (id == R.id.view_msg_dialog_btn_ok) {
            if (mOnOkClickListener != null) mOnOkClickListener.click();
        }
        dismiss();
    }

    public interface OnOkClickListener {
        void click();
    }

    public interface OnCancelClickListener {
        void click();
    }

    public static class Builder {
        private final Context context;
        private String title;
        private String content;
        private String okBtnName;
        private String cancelBtnName;
        private OnOkClickListener onOkClickListener;
        private OnCancelClickListener onCancelClickListener;
        private boolean cancelable;
        private boolean isBtnOk = true;
        private boolean isBtnCancel = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setBtnOkVisibility(boolean isBtnOk) {
            this.isBtnOk = isBtnOk;
            return this;
        }

        public Builder setBtnCancelVisibility(boolean isBtnCancel) {
            this.isBtnCancel = isBtnCancel;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setOnOkBtnClickListener(OnOkClickListener onOkClickListener) {
            this.onOkClickListener = onOkClickListener;
            return this;
        }

        public Builder setOnCancelBtnClickListener(OnCancelClickListener onCancelClickListener) {
            this.onCancelClickListener = onCancelClickListener;
            return this;
        }

        public Builder setOnOkBtnClickListener(String okBtnName, OnOkClickListener onOkClickListener) {
            this.onOkClickListener = onOkClickListener;
            this.okBtnName = okBtnName;
            return this;
        }

        public Builder setOnCancelBtnClickListener(String cancelBtnName, OnCancelClickListener onCancelClickListener) {
            this.onCancelClickListener = onCancelClickListener;
            this.cancelBtnName = cancelBtnName;
            return this;
        }

        public MsgAlertDialog build() {
            MsgAlertDialog dialog = new MsgAlertDialog(context);
            dialog.mTitle = this.title;
            dialog.mContent = this.content;
            dialog.mOnOkClickListener = this.onOkClickListener;
            dialog.mOnCancelClickListener = this.onCancelClickListener;
            dialog.setCancelable(cancelable);

            if (okBtnName != null) {
                dialog.mBtnOk.setText(okBtnName);
            }

            if (cancelBtnName != null) {
                dialog.mBtnCancel.setText(cancelBtnName);
            }
            if (dialog.mBtnOk != null) {
                dialog.mBtnOk.setVisibility(isBtnOk ? View.VISIBLE : View.GONE);
            }
            if (dialog.mBtnCancel != null) {
                dialog.mBtnCancel.setVisibility(isBtnCancel ? View.VISIBLE : View.GONE);
            }
            return dialog;
        }
    }
}
