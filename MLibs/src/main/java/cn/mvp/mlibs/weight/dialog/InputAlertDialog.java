package cn.mvp.mlibs.weight.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.mvp.mlibs.R;
import cn.mvp.mlibs.utils.UIUtils;

/**
 * Created by AaronPasi on 2017/9/16.
 */
public class InputAlertDialog extends AlertDialog implements View.OnClickListener {
    private EditText mEditText;
    private Button mBtnCancel, mBtnOk;
    private Context mContext;
    private OnOkClickListener mOnOkClickListener;
    private OnCancelClickListener mOnCancelClickListener;
    private TextView mTvTitle;
    private String mTitle;
    private String mText;
    private String mHintText;

    public InputAlertDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_input_alert_dialog);
        mEditText = (EditText) findViewById(R.id.view_input_dialog_edt);
        mBtnCancel = (Button) findViewById(R.id.view_input_dialog_btn_cancel);
        mTvTitle = (TextView) findViewById(R.id.view_input_dialog_tv_title);
        mBtnOk = (Button) findViewById(R.id.view_input_dialog_btn_ok);
        //保证EditText能弹出键盘
        if (this.getWindow() != null) {
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        this.setCancelable(false);
        mBtnCancel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mTvTitle.setText(mTitle);
        mEditText.setText(mText);
        mEditText.setHint(mHintText);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.view_input_dialog_btn_cancel) {
            mOnCancelClickListener.click();
        } else if (id == R.id.view_input_dialog_btn_ok) {
            mOnOkClickListener.click(mEditText.getText().toString());
        }
        this.dismiss();
    }

    @Override
    public void setTitle(int titleId) {
        mTitle = UIUtils.getString(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title == null) return;
        mTitle = title.toString();
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setHint(String hintText) {
        mHintText = hintText;
    }

    public void setEditText(String text) {
        mText = text;
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

    public interface OnOkClickListener {
        void click(String inputStr);
    }

    public interface OnCancelClickListener {
        void click();
    }

}