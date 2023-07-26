package cn.mvp.mlibs.weight.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.mvp.mlibs.R;
import cn.mvp.mlibs.utils.UIUtils;

/**
 * Created by AaronPasi on 2017/9/16.
 */
public class MsgAlertDialog extends AlertDialog implements View.OnClickListener {
    private TextView mTvContent;
    private Button mBtnCancel, mBtnOk;
    private Context mContext;
    private OnOkClickListener mOnOkClickListener;
    private OnCancelClickListener mOnCancelClickListener;
    private TextView mTvTitle;
    private String mTitle;
    private String mContent;

    public MsgAlertDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_msg_alert_dialog);
        mBtnCancel = (Button) findViewById(R.id.view_msg_dialog_btn_cancel);
        mBtnOk = (Button) findViewById(R.id.view_msg_dialog_btn_ok);
        mTvTitle = (TextView) findViewById(R.id.view_msg_dialog_tv_title);
        mTvContent = (TextView) findViewById(R.id.view_msg_dialog_tv_content);
        //保证EditText能弹出键盘
//        if (getWindow() != null) {
//            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        }
//        this.setCancelable(false);
        mTvTitle.setText(mTitle);
        Log.i("调试信息", "onCreatmContente:  " + mContent);
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
        this.dismiss();
    }

    @Override
    public void setTitle(int titleId) {
        setTitleStr(UIUtils.getString(titleId));
    }

    private void setTitleStr(String title) {
        mTitle = title;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title != null) {
            setTitleStr(title.toString());
        }
    }

    public TextView getTvContent() {
        return mTvContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setMessage1(String message) {
        mContent = message;
    }

    public Button getBtnCancel() {
        return mBtnCancel;
    }

    public Button getBtnOk() {
        return mBtnOk;
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public void setOkClick(OnOkClickListener onClickListener) {
        mOnOkClickListener = onClickListener;
    }

    public void setCancelClick(OnCancelClickListener onClickListener) {
        mOnCancelClickListener = onClickListener;
    }

    @Override
    public void setMessage(CharSequence message) {

    }

    public interface OnOkClickListener {
        void click();
    }

    public interface OnCancelClickListener {
        void click();
    }

}