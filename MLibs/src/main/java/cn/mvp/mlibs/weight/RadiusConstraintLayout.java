package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import cn.mvp.mlibs.R;

public class RadiusConstraintLayout extends ConstraintLayout {
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

    public RadiusConstraintLayout(Context context) {
        this(context, null);
    }

    public RadiusConstraintLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadiusConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadiusConstraintLayout);
        mBorderWidth = typedArray.getDimension(R.styleable.RadiusConstraintLayout_borderWidth, 0);
        mBorderColor = typedArray.getColor(R.styleable.RadiusConstraintLayout_borderColor, 0);
        mCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_cornerRadius, 0);
        mTopLeftCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_topLeftRadius, mCornerRadius);
        mTopRightCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_topRightRadius, mCornerRadius);
        mBottomLeftCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_bottomLeftRadius, mCornerRadius);
        mBottomRightCornerRadius = typedArray.getDimension(R.styleable.RadiusConstraintLayout_bottomRightRadius, mCornerRadius);
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