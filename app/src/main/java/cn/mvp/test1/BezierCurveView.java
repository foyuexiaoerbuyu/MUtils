package cn.mvp.test1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class BezierCurveView extends View {

    private Paint mPaint;
    private Path mPath;

    public BezierCurveView(Context context) {
        super(context);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 控制点和终点坐标
        float startX = 100;
        float startY = 400;
        float controlX1 = 300;
        float controlY1 = 100;
        float controlX2 = 500;
        float controlY2 = 700;
        float endX = 700;
        float endY = 400;

        // 绘制第一段贝塞尔曲线
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.quadTo(controlX1, controlY1, (controlX1 + controlX2) / 2, (controlY1 + controlY2) / 2);
        canvas.drawPath(mPath, mPaint);

        // 创建第一段渐变色
        Shader shader1 = new LinearGradient(startX, startY, (controlX1 + controlX2) / 2, (controlY1 + controlY2) / 2,
                Color.BLUE, Color.GREEN, Shader.TileMode.CLAMP);

        // 应用第一段渐变色到画笔
        mPaint.setShader(shader1);

        // 绘制第一段贝塞尔曲线
        canvas.drawPath(mPath, mPaint);

        // 绘制第二段贝塞尔曲线
        mPath.reset();
        mPath.moveTo((controlX1 + controlX2) / 2, (controlY1 + controlY2) / 2);
        mPath.quadTo(controlX2, controlY2, endX, endY);
        canvas.drawPath(mPath, mPaint);

        // 创建第二段渐变色
        Shader shader2 = new LinearGradient((controlX1 + controlX2) / 2, (controlY1 + controlY2) / 2, endX, endY,
                Color.GREEN, Color.YELLOW, Shader.TileMode.CLAMP);

        // 应用第二段渐变色到画笔
        mPaint.setShader(shader2);

        // 绘制第二段贝塞尔曲线
        canvas.drawPath(mPath, mPaint);

        // 绘制第三段贝塞尔曲线
        mPath.reset();
        mPath.moveTo(endX, endY);
        mPath.lineTo(startX, startY);
        canvas.drawPath(mPath, mPaint);

        // 创建第三段渐变色
        Shader shader3 = new LinearGradient(endX, endY, startX, startY,
                Color.YELLOW, Color.RED, Shader.TileMode.CLAMP);

        // 应用第三段渐变色到画笔
        mPaint.setShader(shader3);

        // 绘制第三段贝塞尔曲线
        canvas.drawPath(mPath, mPaint);
    }
}
