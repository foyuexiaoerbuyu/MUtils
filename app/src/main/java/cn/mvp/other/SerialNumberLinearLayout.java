package cn.mvp.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/** 点击添加序号 */
public class SerialNumberLinearLayout extends LinearLayout {
    private Paint dotPaint;
    private List<Pair<Float, Float>> dotList;
    private float dotRadius = 20f;
    private int dotColor = Color.RED;
    private int dotNumber = 1;

    public SerialNumberLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(dotColor);
        dotList = new ArrayList<>();
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

    //外边点击的时候直接调用就行 或者解开下面这些代码
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            dotList.add(new Pair<>(event.getX(), event.getY()));
//            dotNumber++;
//            invalidate();
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }

    public void add(MotionEvent event) {
        dotList.add(new Pair<>(event.getX(), event.getY()));
        dotNumber++;
        invalidate();
    }

    public void add(float x, float y) {
        dotList.add(new Pair<>(x, y));
        dotNumber++;
        invalidate();
    }

    public List<Pair<Float, Float>> getDotList() {
        return dotList;
    }

    public void setDotList(List<Pair<Float, Float>> dotList) {
        this.dotList = dotList;
    }

    public void setDotRadius(float radius) {
        dotRadius = radius;
        invalidate();
    }

    public void setDotColor(int color) {
        dotColor = color;
        dotPaint.setColor(dotColor);
        invalidate();
    }

    public int getDotNumber() {
        return dotNumber;
    }
}
