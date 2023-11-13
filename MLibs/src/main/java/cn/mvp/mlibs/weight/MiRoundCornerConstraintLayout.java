package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import cn.mvp.mlibs.R;

/**
 * 圆角约束布局
 */
public class MiRoundCornerConstraintLayout extends ConstraintLayout {
    private float mBorderWidth;
    private int mBorderColor;
    private float mCornerRadius;
    private float mTopLeftCornerRadius;
    private float mTopRightCornerRadius;
    private float mBottomLeftCornerRadius;
    private float mBottomRightCornerRadius;

    private Paint mPaint;
    private RectF mRectF;
    private Path mPath;
    private int mBackgroundColor;

    public MiRoundCornerConstraintLayout(Context context) {
        this(context, null);
    }

    public MiRoundCornerConstraintLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiRoundCornerConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadiusConstraintLayout);
        mBorderWidth = typedArray.getDimension(R.styleable.RadiusConstraintLayout_rclBorderWidth, 0);
        mBorderColor = typedArray.getColor(R.styleable.RadiusConstraintLayout_rclBorderColor, 0);
        mCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_rclCornerRadius, 0);
        mTopLeftCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_rclTopLeftRadius, mCornerRadius);
        mTopRightCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_rclTopRightRadius, mCornerRadius);
        mBottomLeftCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_rclBottomLeftRadius, mCornerRadius);
        mBottomRightCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_rclBottomRightRadius, mCornerRadius);
        mBackgroundColor = typedArray.getColor(R.styleable.RadiusConstraintLayout_rclBackgroundColor, Color.TRANSPARENT);
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);

        mRectF = new RectF();
        mPath = new Path();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mPath.reset();
        canvas.drawColor(mBackgroundColor);
        mRectF.set(mBorderWidth / 2, mBorderWidth / 2, getWidth() - mBorderWidth / 2, getHeight() - mBorderWidth / 2);

        mPath.addRoundRect(mRectF,
                new float[]{
                        mTopLeftCornerRadius, mTopLeftCornerRadius,
                        mTopRightCornerRadius, mTopRightCornerRadius,
                        mBottomRightCornerRadius, mBottomRightCornerRadius,
                        mBottomLeftCornerRadius, mBottomLeftCornerRadius
                },
                Path.Direction.CW);

        canvas.save();
        canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
        canvas.restore();

        canvas.drawPath(mPath, mPaint);
    }
}