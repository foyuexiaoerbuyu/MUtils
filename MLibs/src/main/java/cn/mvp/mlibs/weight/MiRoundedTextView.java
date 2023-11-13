package cn.mvp.mlibs.weight;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.mvp.mlibs.R;


/**
 * https://www.jianshu.com/p/11739859ca34
 * <p>
 * <io.github.imwyy.RoundTextView
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:text="浏览"
 * android:textColor="@color/text_black_select_color"
 * android:textSize="@dimen/sp_14"
 * app:rtvRadius="6dp"
 * app:rtvBgColor="@color/colorSearchArea"/>
 * <p>
 * 支持圆角的TextView
 * Created by stephen on 2017/12/18.
 */
public class MiRoundedTextView extends android.support.v7.widget.AppCompatTextView {

    public MiRoundedTextView(Context context) {
        this(context, null);
    }

    public MiRoundedTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiRoundedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundTextView, defStyleAttr, 0);

        if (attributes != null) {

            int rtvBorderWidth = attributes.getDimensionPixelSize(R.styleable.RoundTextView_rtvBorderWidth, 0);
            int rtvBorderColor = attributes.getColor(R.styleable.RoundTextView_rtvBorderColor, Color.BLACK);
            float rtvRadius = attributes.getDimension(R.styleable.RoundTextView_rtvRadius, 0);
            int rtvBgColor = attributes.getColor(R.styleable.RoundTextView_rtvBgColor, Color.WHITE);
            attributes.recycle();

            GradientDrawable gd = new GradientDrawable();//创建drawable
            gd.setColor(rtvBgColor);
            gd.setCornerRadius(rtvRadius);
            if (rtvBorderWidth > 0) {
                gd.setStroke(rtvBorderWidth, rtvBorderColor);
            }

            this.setBackground(gd);
        }
    }

    public void setBackgroungColor(@ColorInt int color) {
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setColor(color);
    }
}

