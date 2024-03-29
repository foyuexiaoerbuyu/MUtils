package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.drawable.Drawable;
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
public class MiRoundCornerLinearLayout extends LinearLayout {
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

    public MiRoundCornerLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public MiRoundCornerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MiRoundCornerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadiusLinearLayout);
            borderWidth = typedArray.getDimensionPixelSize(R.styleable.RadiusLinearLayout_rllBorderWidth, 0);
            borderColor = typedArray.getColor(R.styleable.RadiusLinearLayout_rllBorderColor, Color.BLACK);
            cornerRadius = typedArray.getDimension(R.styleable.RadiusLinearLayout_rllCornerRadius, 0);
            topLeftRadius = typedArray.getDimension(R.styleable.RadiusLinearLayout_rllTopLeftRadius, cornerRadius);
            topRightRadius = typedArray.getDimension(R.styleable.RadiusLinearLayout_rllTopRightRadius, cornerRadius);
            bottomLeftRadius = typedArray.getDimension(R.styleable.RadiusLinearLayout_rllBottomLeftRadius, cornerRadius);
            bottomRightRadius = typedArray.getDimension(R.styleable.RadiusLinearLayout_rllBottomRightRadius, cornerRadius);
            int cornerBackgroundColor = typedArray.getColor(R.styleable.RadiusLinearLayout_rllCornerBackgroundColor, Color.WHITE);
            typedArray.recycle();
            // 设置圆角区域的背景颜色
            Drawable backgroundDrawable = new Drawable() {
                @Override
                public void draw(@NonNull Canvas canvas) {
                    // 绘制圆角区域的背景颜色
                    canvas.drawPath(borderPath, new Paint(Paint.ANTI_ALIAS_FLAG) {{
                        setColor(cornerBackgroundColor);
                        setStyle(Paint.Style.FILL);
                    }});
                }

                @Override
                public void setAlpha(int alpha) {}

                @Override
                public void setColorFilter(@Nullable ColorFilter colorFilter) {}

                @Override
                public int getOpacity() {
                    return PixelFormat.OPAQUE;
                }
            };

            setBackground(backgroundDrawable);
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