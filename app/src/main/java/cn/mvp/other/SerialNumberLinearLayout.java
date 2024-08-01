package cn.mvp.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/** 点击添加序号 */
public class SerialNumberLinearLayout extends LinearLayout {
    /*圆点画笔*/
    private Paint dotPaint;
    /*圆点列表*/
    private List<Pair<Float, Float>> dotList;
    /** 圆点半径 */
    private float dotRadius = 20f;
    /** 圆点颜色 */
    private int dotColor = Color.RED;
//    /*圆点上的序号*/
//    private int dotNumber = 1;

    /** 线画笔 */
    private Paint linePaint;
    /** 划线路径 */
    private Path linePath;

    /** 控制路径的可见性 */
    private boolean isPathVisible = true;

    public SerialNumberLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(dotColor);
        dotList = new ArrayList<>();

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePath = new Path();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 根据 isPathVisible 的值决定是否绘制路径
        if (isPathVisible) {
            canvas.drawPath(linePath, linePaint);
        }
    }

    public void showPath() {
        isPathVisible = true;
        invalidate();
    }

    public void hidePath() {
        isPathVisible = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < dotList.size(); i++) {
            Pair<Float, Float> pair = dotList.get(i);
            canvas.drawCircle(pair.first, pair.second, dotRadius, dotPaint);
            // 绘制数字
            dotPaint.setColor(Color.WHITE); // 设置文字颜色为白色
            dotPaint.setTextSize(dotRadius * 1.5f); // 设置文字大小为圆点半径的1.5倍
            String text = String.valueOf(i + 1);
            float textWidth = dotPaint.measureText(text); // 计算文字宽度
            Paint.FontMetrics fm = dotPaint.getFontMetrics(); // 获取文字高度
            float textHeight = fm.descent - fm.ascent;
            float textX = pair.first - textWidth / 2; // 计算文字绘制起始点X坐标
            float textY = pair.second + textHeight / 2 - fm.descent; // 计算文字绘制起始点Y坐标
            canvas.drawText(text, textX, textY, dotPaint);
            dotPaint.setColor(dotColor); // 恢复圆点颜色
        }
    }

//    /** 外边点击的时候直接调用就行 或者解开下面这些代码 */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            dotList.add(new Pair<>(event.getX(), event.getY()));
////            dotNumber++;
//            invalidate();
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }

    /** 添加圆点 */
    public void addDot(MotionEvent event) {
        dotList.add(new Pair<>(event.getX(), event.getY()));
//        dotNumber++;
        invalidate();
    }

    /** 添加序号圆点 */
    public void addDot(float x, float y) {
        dotList.add(new Pair<>(x, y));
//        dotNumber++;
        invalidate();
    }

    /** 获取圆点 */
    public List<Pair<Float, Float>> getDotList() {
        return dotList;
    }

    /** 设置圆点 */
    public void setDotList(List<Pair<Float, Float>> dotList) {
        this.dotList = dotList;
    }

    /** 设置圆点半径 */
    public void setDotRadius(float radius) {
        dotRadius = radius;
        invalidate();
    }

    /** 设置圆点颜色 */
    public void setDotColor(int color) {
        dotColor = color;
        dotPaint.setColor(dotColor);
        invalidate();
    }

//    /** 获取圆点序号 */
//    public int getDotNumber() {
//        return dotNumber;
//    }

    /*划线_移动划线的画笔到指定位置,按下时移动画笔到指定坐标 MotionEvent.ACTION_DOWN*/
    public void moveTo(float x, float y) {
        linePath.moveTo(x, y);
    }

    /*划线_点到点划线,移动时话线 MotionEvent.ACTION_MOVE*/
    public void lineTo(float x, float y) {
        linePath.lineTo(x, y);
        invalidate();
    }


    /**
     * 开始放大或缩小动画
     * 按下时放大  startAnimation(event.getX(), event.getY(), 1.0f, 3f, rootLayout);
     * 松开时恢复原样 startAnimation(event.getX(), event.getY(), 3f, 1.0f, rootLayout);
     *
     * @param pivotX     缩放中心点的 X 坐标
     * @param pivotY     缩放中心点的 Y 坐标
     * @param fromScale  起始缩放比例
     * @param toScale    结束缩放比例
     * @param rootLayout 需要执行动画的布局
     */
    private void startAnimation(float pivotX, float pivotY, float fromScale, float toScale, LinearLayout rootLayout) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                fromScale, toScale, // 开始和结束时 X 轴的缩放比例
                fromScale, toScale, // 开始和结束时 Y 轴的缩放比例
                pivotX, pivotY); // 缩放中心点 X 和 Y 坐标
        scaleAnimation.setDuration(3000); // 动画持续时间，单位毫秒
        scaleAnimation.setFillAfter(true); // 动画结束后保持最终状态

        rootLayout.startAnimation(scaleAnimation);
    }
}
