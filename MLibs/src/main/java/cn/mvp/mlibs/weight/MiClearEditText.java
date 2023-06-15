package cn.mvp.mlibs.weight;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;


/**
 * https://blog.csdn.net/xiaanming/article/details/11066685
 * <p>
 * 清除输入框内容
 * <com.sv.sip.webrtc.ClearEditText
 * android:id="@+id/login_edt_pwd"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * android:layout_margin="50dp"
 * android:layout_marginTop="25dp"
 * android:background="@drawable/edittext_rounded_border"
 * android:drawableEnd="@drawable/icon_login_pwd_hid"
 * android:hint="请输入密码"
 * android:inputType="textPassword"
 * android:maxLength="24"
 * android:maxLines="1"
 * android:padding="8dp"
 * android:singleLine="true"
 * app:layout_constraintLeft_toLeftOf="parent"
 * app:layout_constraintRight_toRightOf="parent"
 * app:layout_constraintTop_toBottomOf="@id/login_edt_username" />
 * <p>
 * 圆角边框
 * <?xml version="1.0" encoding="utf-8"?>
 * <shape xmlns:android="http://schemas.android.com/apk/res/android">
 * <solid android:color="#FFFFFF" /> <!-- EditText的背景颜色 -->
 * <corners android:radius="10dp" /> <!-- 圆角的半径大小 -->
 * <stroke android:color="#000000" android:width="1dp"/> <!-- 边框的颜色和宽度 -->
 * </shape>
 * 使用
 * login_edt_username.setIClickRightIcon(new ClearEditText.IClickRightIcon() {
 *
 * @Override public void onClick(View view) {
 * login_edt_username.clear();
 * }
 * });
 */
public class MiClearEditText extends AppCompatEditText implements OnFocusChangeListener, TextWatcher {
    private final Context mContext;
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    public MiClearEditText(Context context) {
        this(context, null);
    }

    public MiClearEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MiClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }


    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getDrawable(2);
//        if (mClearDrawable == null) {
//            mClearDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_com_del);
//        }
        if (mClearDrawable != null) {
            mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        }

        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }


    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getDrawable(2) != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    if (mIClickRightIcon != null) {
                        mIClickRightIcon.onClick(this);
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }


    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getDrawable(0), getDrawable(1), right, getDrawable(3));
    }

    /**
     * 设置右侧图标
     */
    public void setRightIconDrawable(int drawable) {
        mClearDrawable = ContextCompat.getDrawable(mContext, drawable);
        if (mClearDrawable != null) {
            mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
            setCompoundDrawables(getDrawable(0), getDrawable(1), mClearDrawable, getDrawable(3));
        }
    }


    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }


    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    public IClickRightIcon mIClickRightIcon;

    /**
     * 点击右侧图标显示或隐藏密码
     *
     * @param iconShow 显示图标
     * @param iconHide 隐藏图标
     */
    public void showOrHidePwd(int iconShow, int iconHide) {
        if (getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            setRightIconDrawable(iconShow);
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            setRightIconDrawable(iconHide);
            setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public interface IClickRightIcon {
        void onClick(View view);
    }

    public void setIClickRightIcon(IClickRightIcon IClickRightIcon) {
        mIClickRightIcon = IClickRightIcon;
    }

    public void clear() {
        this.setText("");
    }

    private Drawable getDrawable(int index) {
        Drawable drawableEnd = this.getCompoundDrawablesRelative()[index];
        if (drawableEnd == null) {
            drawableEnd = this.getCompoundDrawables()[index];
        }
        return drawableEnd;
    }

    public String getTrimStr() {
        Editable text = getText();
        if (text != null) {
            return text.toString().trim();
        }
        return "";
    }
}