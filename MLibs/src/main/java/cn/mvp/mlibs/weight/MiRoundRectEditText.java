package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import cn.mvp.mlibs.R;

/**
 * 圆角编辑框
 */
public class MiRoundRectEditText extends AppCompatEditText {
    private float mBorderWidth = 1;
    private int mBorderColor = Color.BLACK;
    private float mRadius = 0;
    private float mTopLeftRadius = 0;
    private float mTopRightRadius = 0;
    private float mBottomLeftRadius = 0;
    private float mBottomRightRadius = 0;
    private Paint mPaint;
    private Path mPath;
    private RectF mRectF;

    public MiRoundRectEditText(Context context) {
        this(context, null);
    }

    public MiRoundRectEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiRoundRectEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundRectEditText, defStyleAttr, 0);
        mBorderWidth = a.getDimension(R.styleable.RoundRectEditText_borderWidth, mBorderWidth);
        mBorderColor = a.getColor(R.styleable.RoundRectEditText_borderColor, mBorderColor);
        mRadius = a.getDimension(R.styleable.RoundRectEditText_radius, mRadius);
        mTopLeftRadius = a.getDimension(R.styleable.RoundRectEditText_topLeftRadius, mTopLeftRadius);
        mTopRightRadius = a.getDimension(R.styleable.RoundRectEditText_topRightRadius, mTopRightRadius);
        mBottomLeftRadius = a.getDimension(R.styleable.RoundRectEditText_bottomLeftRadius, mBottomLeftRadius);
        mBottomRightRadius = a.getDimension(R.styleable.RoundRectEditText_bottomRightRadius, mBottomRightRadius);
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        mRectF.set(mBorderWidth / 2, mBorderWidth / 2, getWidth() - mBorderWidth / 2, getHeight() - mBorderWidth / 2);

        float[] radii = new float[]{
                mTopLeftRadius > 0 ? mTopLeftRadius : mRadius,
                mTopLeftRadius > 0 ? mTopLeftRadius : mRadius,
                mTopRightRadius > 0 ? mTopRightRadius : mRadius,
                mTopRightRadius > 0 ? mTopRightRadius : mRadius,
                mBottomRightRadius > 0 ? mBottomRightRadius : mRadius,
                mBottomRightRadius > 0 ? mBottomRightRadius : mRadius,
                mBottomLeftRadius > 0 ? mBottomLeftRadius : mRadius,
                mBottomLeftRadius > 0 ? mBottomLeftRadius : mRadius
        };

        mPath.addRoundRect(mRectF, radii, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }
}