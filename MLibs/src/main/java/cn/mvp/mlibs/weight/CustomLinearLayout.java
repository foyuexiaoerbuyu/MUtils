package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.mvp.mlibs.R;

/**
 * <com.example.CustomLinearLayout
 * xmlns:android="http://schemas.android.com/apk/res/android"
 * xmlns:app="http://schemas.android.com/apk/res-auto"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * app:borderWidth="2dp"
 * app:borderColor="@android:color/darker_gray"
 * app:cornerRadius="8dp"
 * app:topLeftRadius="16dp"
 * app:topRightRadius="16dp"
 * app:bottomLeftRadius="16dp"
 * app:bottomRightRadius="16dp">
 * <p>
 * <p>
 * </com.example.CustomLinearLayout>
 * <p>
 * 这个自定义线性布局并设置边框厚度、边框颜色、圆角角度以及每个角的单独角度：
 */
public class CustomLinearLayout extends LinearLayout {
    private int borderWidth;
    private int borderColor;
    private float cornerRadius;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;

    private Paint borderPaint;
    private RectF borderRect;
    private Path borderPath;

    public CustomLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomLinearLayout);
            borderWidth = typedArray.getDimensionPixelSize(R.styleable.CustomLinearLayout_borderWidth, 0);
            borderColor = typedArray.getColor(R.styleable.CustomLinearLayout_borderColor, Color.BLACK);
            cornerRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_cornerRadius, 0);
            topLeftRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_topLeftRadius, cornerRadius);
            topRightRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_topRightRadius, cornerRadius);
            bottomLeftRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_bottomLeftRadius, cornerRadius);
            bottomRightRadius = typedArray.getDimension(R.styleable.CustomLinearLayout_bottomRightRadius, cornerRadius);
            typedArray.recycle();
        } else {
            borderWidth = 0;
            borderColor = Color.BLACK;
            cornerRadius = 0;
            topLeftRadius = 0;
            topRightRadius = 0;
            bottomLeftRadius = 0;
            bottomRightRadius = 0;
        }

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);

        borderRect = new RectF();
        borderPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        borderRect.set(borderWidth / 2f, borderWidth / 2f, w - borderWidth / 2f, h - borderWidth / 2f);

        borderPath.reset();
        borderPath.addRoundRect(borderRect, new float[]{
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
        }, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawPath(borderPath, borderPaint);
        super.dispatchDraw(canvas);
    }
}