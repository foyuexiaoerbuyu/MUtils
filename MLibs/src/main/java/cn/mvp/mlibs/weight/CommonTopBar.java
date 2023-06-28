package cn.mvp.mlibs.weight;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.mvp.mlibs.R;


/**
 * Created by wangyunpeng on 2016/11/1.
 * common top bar
 * https://52it.party/2018/07/05/%E4%B8%80%E4%B8%AA%E9%80%9A%E7%94%A8%E7%9A%84%E9%A1%B6%E9%83%A8%E5%AF%BC%E8%88%AA%E6%A0%8F%E5%B8%83%E5%B1%80/
 */
public class CommonTopBar extends FrameLayout {

    private Context mContext;
    private TextView mLeftTv;//left view
    private CommonTopBarClick mCommonTopBarClick;
    private TextView mMidTv;//middle view
    private TextView mRightTv;//right view

    private ImageView mRightImg;
    private ImageView mLeftImg;

    /**
     * @return 左侧图片是否显示
     */
    public boolean isLeftImgIsVisible() {
        return mLeftImg != null && mLeftImg.getVisibility() == View.VISIBLE;
    }

    /**
     * @return 左侧文字是否显示
     */
    public boolean isLeftViewIsVisible() {
        return mLeftTv != null && mLeftTv.getVisibility() == View.VISIBLE;
    }

    /**
     * @return 右侧图片是否显示
     */
    public boolean isRightImgIsVisible() {
        return mRightImg != null && mRightImg.getVisibility() == View.VISIBLE;
    }

    /**
     * @return 右侧文字是否显示
     */
    public boolean isRightViewIsVisible() {
        return mRightTv != null && mRightTv.getVisibility() == View.VISIBLE;
    }


    public CommonTopBar(Context context) {
        this(context, null);

    }

    public CommonTopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView(attrs);
    }

    public CommonTopBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(attrs);
    }

    /**
     * init view
     *
     * @param attrs
     */
    @SuppressLint("InflateParams")
    private void initView(AttributeSet attrs) {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.weight_common_bar_layout, null);

        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CommonTopBar);

        String mid_text = ta.getString(R.styleable.CommonTopBar_mid_text);
        String left_text = ta.getString(R.styleable.CommonTopBar_left_text);
        String right_text = ta.getString(R.styleable.CommonTopBar_right_text);

        final boolean leftIsBack = ta.getBoolean(R.styleable.CommonTopBar_leftIsBack, false);

        int mid_text_color = ta.getColor(R.styleable.CommonTopBar_mid_text_color, 0);
        int left_text_color = ta.getColor(R.styleable.CommonTopBar_left_text_color, 0);
        int right_text_color = ta.getColor(R.styleable.CommonTopBar_right_text_color, 0);

        float mid_text_size = ta.getDimension(R.styleable.CommonTopBar_mid_text_size, 0);
        float left_text_size = ta.getDimension(R.styleable.CommonTopBar_left_text_size, 0);
        float right_text_size = ta.getDimension(R.styleable.CommonTopBar_right_text_size, 0);

        int left_img = ta.getResourceId(R.styleable.CommonTopBar_left_img, 0);
        int right_img = ta.getResourceId(R.styleable.CommonTopBar_right_img, 0);
        ta.recycle();
        mMidTv = view.findViewById(R.id.common_top_bar_text_mid);
        mLeftTv = view.findViewById(R.id.common_top_bar_text_left);
        mRightTv = view.findViewById(R.id.common_top_bar_text_right);
        mRightImg = view.findViewById(R.id.common_top_bar_img_right);
        mLeftImg = view.findViewById(R.id.common_top_bar_img_left);

        if (left_img != 0) {
            mLeftImg.setImageResource(left_img);
            mLeftImg.setVisibility(View.VISIBLE);
        }
        if (right_img != 0) {
            mRightImg.setImageResource(right_img);
            mRightImg.setVisibility(View.VISIBLE);
        }

        if (!"".equals(mid_text)) {
            mMidTv.setText(mid_text);
            mMidTv.setVisibility(View.VISIBLE);
            if (mid_text_size != 0) mMidTv.setTextSize(mid_text_size);
            if (mid_text_color != 0) mMidTv.setTextColor(mid_text_color);
        }
        if (!"".equals(left_text)) {
            mLeftTv.setText(left_text);
            mLeftTv.setVisibility(View.VISIBLE);
            if (left_text_size != 0) mLeftTv.setTextSize(left_text_size);
            if (left_text_color != 0) mLeftTv.setTextColor(left_text_color);
        }
        if (!"".equals(right_text)) {
            mRightTv.setText(right_text);
            mRightTv.setVisibility(View.VISIBLE);
            if (right_text_size != 0) mRightTv.setTextSize(right_text_size);
            if (right_text_color != 0) mRightTv.setTextColor(right_text_color);
        }

        mLeftTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (leftIsBack) {
                    ((Activity) mContext).finish();
                    return;
                }
                if (mCommonTopBarClick != null)
                    mCommonTopBarClick.onClickLeft();
            }
        });
        mLeftImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (leftIsBack) {
                    ((Activity) mContext).finish();
                    return;
                }
                if (mCommonTopBarClick != null)
                    mCommonTopBarClick.onClickLeft();
            }
        });
        mRightImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCommonTopBarClick != null)
                    mCommonTopBarClick.onClickRight();
            }
        });
        mRightTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCommonTopBarClick != null)
                    mCommonTopBarClick.onClickRight();
            }
        });
        addView(view);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);
    }

    /**
     * if show right view,default gone
     *
     * @param isShow
     */
    public void isShowRightView(boolean isShow) {
        if (mRightImg != null && isShow)
            mRightImg.setVisibility(View.GONE);
        if (mRightTv != null)
            mRightTv.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void isShowRightImgView(boolean isShow) {
        if (mRightTv != null && isShow)
            mRightTv.setVisibility(View.GONE);
        if (mRightImg != null)
            mRightImg.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设if show left view,default gone
     *
     * @param isShow
     */
    public void isShowLeftView(boolean isShow) {
        if (mLeftImg != null && isShow)
            mLeftImg.setVisibility(View.GONE);
        if (mLeftTv != null)
            mLeftTv.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setLeftTextBold(boolean isBold) {
        if (mLeftTv != null)
            mLeftTv.getPaint().setFakeBoldText(isBold);
    }

    public void isShowLeftImgView(boolean isShow) {
        if (mLeftTv != null && isShow)
            mLeftTv.setVisibility(View.GONE);
        if (mLeftImg != null)
            mLeftImg.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }



    /*public CommonTopBarClick getCommonTopBarClick() {
        return mCommonTopBarClick;
    }*/

    /**
     * set click event listener
     *
     * @param mCommonTopBarClick
     */
    public void setCommonTopBarClick(CommonTopBarClick mCommonTopBarClick) {
        this.mCommonTopBarClick = mCommonTopBarClick;
    }

    /**
     * set right view margin right,the param is dp
     *
     * @param marginRight
     */
    public void addRightMargin(float marginRight) {
        if (mRightTv != null) {
            RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mRightTv.getLayoutParams();
            mLayoutParams.rightMargin = dp2px(mContext, marginRight);
            mRightTv.setLayoutParams(mLayoutParams);
            mRightTv.requestLayout();
        }
    }

    public void addRightImgMargin(float marginRight) {
        if (mRightImg != null) {
            RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mRightImg.getLayoutParams();
            mLayoutParams.rightMargin = dp2px(mContext, marginRight);
            mRightImg.setLayoutParams(mLayoutParams);
            mRightImg.requestLayout();
        }
    }

    /**
     * set left view margin left,the param is dp
     *
     * @param marginLeft
     */
    public void addLeftMargin(float marginLeft) {
        if (mLeftTv != null) {
            RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mLeftTv.getLayoutParams();
            mLayoutParams.leftMargin = dp2px(mContext, marginLeft);
            mLeftTv.setLayoutParams(mLayoutParams);
            mLeftTv.requestLayout();
        }
    }

    public void addLeftImgMargin(float marginLeft) {
        if (mLeftImg != null) {
            RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mLeftImg.getLayoutParams();
            mLayoutParams.leftMargin = dp2px(mContext, marginLeft);
            mLeftImg.setLayoutParams(mLayoutParams);
            mLeftImg.requestLayout();
        }
    }


    /**
     * set
     *
     * @param lenth
     */
    public void setMidTextMaxLenth(int lenth) {
        if (mMidTv != null)
            mMidTv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenth)});
    }

    /**
     * set mid view text
     *
     * @param charSequence
     */
    public void setMidText(CharSequence charSequence) {
        if (mMidTv != null)
            mMidTv.setText(charSequence);
    }

    public void setMidTextBold(boolean isBold) {
        if (mMidTv != null)
            mMidTv.getPaint().setFakeBoldText(isBold);

    }

    /**
     * set mid view text
     *
     * @param textRes
     */
    public void setMidText(@StringRes int textRes) {
        if (mMidTv != null)
            mMidTv.setText(textRes);
    }

    public TextView getRightTv() {
        return mRightTv;
    }

    public TextView getLeftTv() {
        return mLeftTv;
    }

    /**
     * set mid text size
     *
     * @param size
     */
    public void setMidTextSize(float size) {
        if (mMidTv != null)
            mMidTv.setTextSize(size);
    }

    /**
     * set mid text color
     *
     * @param colorStateList
     */
    public void setMidTextColor(@NonNull ColorStateList colorStateList) {
        if (mMidTv != null)
            mMidTv.setTextColor(colorStateList);
    }

    /**
     * set mid text color
     *
     * @param color
     */
    public void setMidTextColor(@ColorInt int color) {
        if (mMidTv != null)
            mMidTv.setTextColor(color);
    }

    /**
     * set left image
     *
     * @param drawable
     */
    public void setLeftImage(@NonNull Drawable drawable) {
        if (mLeftImg != null) {
            if (Build.VERSION.SDK_INT < 16) {
                mLeftImg.setBackgroundDrawable(drawable);
            } else {
                mLeftImg.setBackground(drawable);
            }
        }
    }

    /**
     * set left image
     *
     * @param drawableRes
     */
    public void setLeftImage(@DrawableRes int drawableRes) {
        if (mLeftImg != null)
            mLeftImg.setBackgroundResource(drawableRes);
    }

    /**
     * set left view text
     *
     * @param charSequence
     */
    public void setLeftText(CharSequence charSequence) {
        if (mLeftTv != null)
            mLeftTv.setText(charSequence);
    }

    /**
     * set left view text
     *
     * @param textRes
     */
    public void setLeftText(@StringRes int textRes) {
        if (mLeftTv != null)
            mLeftTv.setText(textRes);
    }

    /**
     * set left text size
     *
     * @param size
     */
    public void setLeftTextSize(float size) {
        if (mLeftTv != null)
            mLeftTv.setTextSize(size);
    }

    /**
     * set left text color
     *
     * @param colorStateList
     */
    public void setLeftTextColor(@NonNull ColorStateList colorStateList) {
        if (mLeftTv != null)
            mLeftTv.setTextColor(colorStateList);
    }

    /**
     * set left text color
     *
     * @param color
     */
    public void setLeftTextColor(@ColorInt int color) {
        if (mLeftTv != null)
            mLeftTv.setTextColor(color);
    }


    /**
     * set right image
     *
     * @param drawable
     */
    public void setRightImage(@NonNull Drawable drawable) {
        if (mRightImg != null) {
            if (Build.VERSION.SDK_INT < 16) {
                mRightImg.setBackgroundDrawable(drawable);
            } else {
                mRightImg.setBackground(drawable);
            }
        }
    }

    /**
     * set right image
     *
     * @param drawableRes
     */
    public void setRightImage(@DrawableRes int drawableRes) {
        if (mRightImg != null)
            mRightImg.setBackgroundResource(drawableRes);
    }

    /**
     * set right view text
     *
     * @param charSequence
     */
    public void setRightText(CharSequence charSequence) {
        if (mRightTv != null)
            mRightTv.setText(charSequence);
    }

    public void setRightTextBold(boolean isBold) {
        if (mRightTv != null)
            mRightTv.getPaint().setFakeBoldText(isBold);

    }

    /**
     * set right view text
     *
     * @param textRes
     */
    public void setRightText(@StringRes int textRes) {
        if (mRightTv != null)
            mRightTv.setText(textRes);
    }

    /**
     * set right text size
     *
     * @param size
     */
    public void setRightTextSize(float size) {
        if (mRightTv != null)
            mRightTv.setTextSize(size);
    }

    /**
     * set right text color
     *
     * @param colorStateList
     */
    public void setRightTextColor(@NonNull ColorStateList colorStateList) {
        if (mRightTv != null)
            mRightTv.setTextColor(colorStateList);
    }

    /**
     * set right text color
     *
     * @param color
     */
    public void setRightTextColor(@ColorInt int color) {
        if (mRightTv != null)
            mRightTv.setTextColor(color);
    }

    /**
     * set DrawPadding
     *
     * @param size dp
     */
    public void setLefTextViewDrawPadding(int size) {
        if (mLeftTv != null)
            mLeftTv.setCompoundDrawablePadding(dp2px(mContext, size));
    }

    /**
     * set left drawImg
     *
     * @param drawableRes
     */
    public void setLeftTextViewDrawLeft(@DrawableRes int drawableRes) {
        if (mLeftTv != null)
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(drawableRes, 0, 0, 0);
    }

    /**
     * set left drawImg
     *
     * @param drawableRes
     */
    public void setLeftTextViewDrawLeft(@NonNull Drawable drawableRes) {
        if (mLeftTv != null)
            mLeftTv.setCompoundDrawablesWithIntrinsicBounds(drawableRes, null, null, null);
    }


    /*转换方法*/

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /*接口*/

    /**
     * Created by wangyunpeng on 2016/3/11.
     */
    public interface CommonTopBarClick {
        void onClickLeft();

        void onClickRight();
    }

    /*
    * 布局:weight_common_bar_layout.xml
    *
     <?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:id="@+id/common_top_bar_text_left"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:gravity="center"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:visibility="gone" />
    <ImageView
        android:id="@+id/common_top_bar_img_left"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentStart="true"
        android:layout_alignParentLeft="true"
        android:layout_centerVertical="true"
        android:visibility="gone" />
    <TextView
        android:id="@+id/common_top_bar_text_mid"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:ellipsize="end"
        android:gravity="center"
        android:maxLength="8"
        android:maxLines="1"
        android:singleLine="true"
        android:textColor="@color/white"
        android:textSize="20sp" />
    <TextView
        android:id="@+id/common_top_bar_text_right"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_alignParentRight="true"
        android:layout_centerVertical="true"
        android:background="?attr/selectableItemBackground"
        android:clickable="true"
        android:gravity="center"
        android:paddingRight="10dp"
        android:textColor="@color/white"
        android:textSize="20sp"
        android:visibility="gone" />
    <ImageView
        android:id="@+id/common_top_bar_img_right"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_alignParentRight="true"
        android:layout_centerVertical="true"
        android:visibility="gone" />
</RelativeLayout>
    * */

}