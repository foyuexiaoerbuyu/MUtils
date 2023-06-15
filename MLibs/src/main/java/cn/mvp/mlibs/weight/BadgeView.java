package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import cn.mvp.mlibs.R;


public class BadgeView extends AppCompatTextView {

    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mBackgroundColor;
    private int mTextColor;
    private String mBadgeText;
    private int mBadgeSize;
    private int mPadding; // 新增变量，用于设置文字到边界的距离

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeTextView);
        mBackgroundColor = typedArray.getColor(R.styleable.BadgeTextView_badgeBackgroundColor, Color.RED);
        mTextColor = typedArray.getColor(R.styleable.BadgeTextView_badgeTextColor, Color.WHITE);
        mBadgeText = typedArray.getString(R.styleable.BadgeTextView_badgeText);
        mBadgeSize = typedArray.getDimensionPixelSize(R.styleable.BadgeTextView_badgeTextSize, 20);
        mPadding = typedArray.getDimensionPixelSize(R.styleable.BadgeTextView_badgePadding, 4); // 从XML属性中获取文字到边界的距离，默认值为4
        typedArray.recycle();

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mBadgeSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int newSize = mBadgeSize + 2 * mPadding; // 根据边距调整控件的大小
        setMeasuredDimension(newSize, newSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int newSize = mBadgeSize + 2 * mPadding; // 根据边距调整控件的大小
        canvas.drawCircle(newSize / 2, newSize / 2, newSize / 2, mBackgroundPaint);

        if (mBadgeText != null) {
            Rect textBounds = new Rect();
            mTextPaint.getTextBounds(mBadgeText, 0, mBadgeText.length(), textBounds);
            int textHeight = textBounds.height();
            float textBaseline = newSize / 2 + textHeight / 2;
            canvas.drawText(mBadgeText, newSize / 2, textBaseline, mTextPaint);
        }
    }

    public void setBadgeBackgroundColor(int color) {
        mBackgroundColor = color;
        mBackgroundPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public void setBadgeTextColor(int color) {
        mTextColor = color;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setBadgeText(String text) {
        mBadgeText = text;
        invalidate();
    }

    public void setBadgeTextSize(int size) {
        mBadgeSize = size;
        mTextPaint.setTextSize(mBadgeSize);
        requestLayout();
        invalidate();
    }

    // 新增方法，用于设置文字到边界的距离
    public void setBadgePadding(int padding) {
        mPadding = padding;
        requestLayout();
        invalidate();
    }
}