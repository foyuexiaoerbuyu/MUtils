package cn.mvp.test.t1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.mvp.R;

public class ZoomableView extends View {

    private Paint paint;
    private PointF redDot;
    private float scaleFactor = 1.0f;
    private ValueAnimator scaleAnimator;
    private long animationDuration = 3000; // 3秒
    private Bitmap backgroundBitmap;

    public ZoomableView(Context context) {
        this(context, null);
    }

    public ZoomableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);

        // 加载背景图片
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zfb_def);

        scaleAnimator = ValueAnimator.ofFloat(1.0f, 5.0f); // 放大倍数从1到2
        scaleAnimator.setDuration(animationDuration);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleFactor = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 放大结束后，恢复原大小
                resetScale();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            redDot = new PointF(event.getX(), event.getY());
            startScaleAnimation();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 以红点为中心进行缩放和平移
        if (redDot != null) {
            canvas.save();
            canvas.scale(scaleFactor, scaleFactor, redDot.x, redDot.y);

            // 绘制背景图片
            if (backgroundBitmap != null) {
                canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
            }

            // 绘制红点
            paint.setColor(Color.RED);
            canvas.drawCircle(redDot.x, redDot.y, 20, paint);

            canvas.restore();
        }
    }

    private void startScaleAnimation() {
        if (scaleAnimator.isRunning()) {
            scaleAnimator.cancel();
        }
        scaleAnimator.start();
    }

    private void resetScale() {
        ValueAnimator resetAnimator = ValueAnimator.ofFloat(scaleFactor, 1.0f);
        resetAnimator.setDuration(300);
        resetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scaleFactor = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        resetAnimator.start();
    }
}
