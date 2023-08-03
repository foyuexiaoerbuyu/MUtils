package cn.mvp.mlibs.weight.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
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
    private int mInputType = InputType.TYPE_CLASS_TEXT;

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
        mBtnCancel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mTvTitle.setText(mTitle);
        mEditText.setText(mText);
        mEditText.setHint(mHintText);
        mEditText.setInputType(mInputType);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.view_input_dialog_btn_cancel) {
            if (mOnCancelClickListener != null) {
                mOnCancelClickListener.click();
            }
        } else if (id == R.id.view_input_dialog_btn_ok) {
            if (mOnOkClickListener != null) {
                mOnOkClickListener.click(mEditText.getText().toString());
            }
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

    /**
     * InputType.TYPE_CLASS_TEXT`：普通文本输入。可以输入任意字符。
     * InputType.TYPE_CLASS_NUMBER`：数字输入。只能输入数字字符。
     * InputType.TYPE_CLASS_PHONE`：电话号码输入。可以输入数字、加号、减号、括号和空格等字符。
     * InputType.TYPE_CLASS_DATETIME`：日期和时间输入。可以输入日期和时间格式的字符。
     * InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS`：电子邮件地址输入。可以输入电子邮件地址格式的字符。
     * InputType.TYPE_TEXT_VARIATION_PASSWORD`：密码输入。输入的字符会被隐藏。
     * InputType.TYPE_NUMBER_VARIATION_PASSWORD`：数字密码输入。输入的数字会被隐藏。
     * InputType.TYPE_TEXT_FLAG_CAP_WORDS`：自动将单词的首字母大写。
     * InputType.TYPE_TEXT_FLAG_CAP_SENTENCES`：自动将句子的首字母大写。
     * InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS`：自动将所有字符大写。
     * InputType.TYPE_TEXT_FLAG_AUTO_CORRECT`：启用自动纠错功能。
     * InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE`：启用自动完成功能。
     * InputType.TYPE_TEXT_FLAG_MULTI_LINE`：多行文本输入。
     * InputType.TYPE_NUMBER_FLAG_DECIMAL`：允许输入小数。
     * InputType.TYPE_NUMBER_FLAG_SIGNED`：允许输入带正负号的数字。
     *
     * @param inputType
     */
    public void setInputType(int inputType) {
        mInputType = inputType;
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