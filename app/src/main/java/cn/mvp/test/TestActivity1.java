package cn.mvp.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.hjq.toast.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.mvp.R;

public class TestActivity1 extends AppCompatActivity {

    List<Pos> mPos = new ArrayList<>();
    float potStartX;
    float potStartY;
    private Button btn_save, btn_resume;
    private ImageView iv_canvas;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;

    public static void open(Context context) {
        Intent starter = new Intent(context, TestActivity1.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);


        // 初始化一个画笔，笔触宽度为5，颜色为红色
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_resume = (Button) findViewById(R.id.btn_resume);

        btn_save.setOnClickListener(v -> {
//            saveBitmap();
//            LogUtils.i(GsonUtil.toJson(mPos));
            mPos.clear();
        });
        btn_resume.setOnClickListener(v -> {
            resumeCanvas();
        });
        iv_canvas.setOnTouchListener(new View.OnTouchListener() {
            // 定义手指开始触摸的坐标
            float startX;
            float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // 用户按下动作
                    case MotionEvent.ACTION_DOWN:
                        // 第一次绘图初始化内存图片，指定背景为白色
                        // 记录开始触摸的点的坐标
                        startX = event.getX();
                        startY = event.getY();
                        if (baseBitmap == null) {
                            baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
                                    iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
                            canvas = new Canvas(baseBitmap);
                            canvas.drawColor(Color.WHITE);
                        }
                        break;
                    // 用户手指在屏幕上移动的动作
                    case MotionEvent.ACTION_MOVE:
                        // 记录移动位置的点的坐标
//                        float stopX = event.getX();
//                        float stopY = event.getY();

                        //根据两点坐标，绘制连线
                        canvas.drawLine(startX, startY, event.getX(), event.getY(), paint);

                        // 更新开始点的位置
                        startX = event.getX();
                        startY = event.getY();
                        mPos.add(new Pos(startX, startY));
                        // 把图片展示到ImageView中
                        iv_canvas.setImageBitmap(baseBitmap);
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();

                        // 计算放大倍数为2倍
                        float scale = 2f;

                        // 创建属性动画，实现画布放大效果
                        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, scale);
                        scaleAnimator.setDuration(3000); // 3秒钟完成动画
                        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (Float) animation.getAnimatedValue();
                                iv_canvas.setScaleX(value);
                                iv_canvas.setScaleY(value);

                                // 计算放大或缩小的中心点
                                float centerX = endX; // 以 endX 为放大或缩小的中心点
                                float centerY = endY; // 以 endY 为放大或缩小的中心点
                                iv_canvas.setPivotX(centerX);
                                iv_canvas.setPivotY(centerY);
                            }
                        });
                        scaleAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                iv_canvas.setScaleX(1f);
                                iv_canvas.setScaleY(1f);
                            }
                        });
                        scaleAnimator.start();

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    /**
     * 保存图片到SD卡上
     */
    protected void saveBitmap() {
        try {
            // 保存图片到SD卡上
            File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
            FileOutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ToastUtils.show("保存图片成功");

            // Android设备Gallery应用只会在启动的时候扫描系统文件夹
            // 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(Environment
                    .getExternalStorageDirectory()));
            sendBroadcast(intent);
        } catch (Exception e) {
            ToastUtils.show("保存图片失败");
            e.printStackTrace();
        }
    }

    /**
     * 清除画板
     */
    protected void resumeCanvas() {
        // 手动清除画板的绘图，重新创建一个画板
        if (baseBitmap != null) {
            Bitmap baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(), iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(baseBitmap);
            canvas.drawColor(Color.WHITE);
            iv_canvas.setImageBitmap(baseBitmap);

            LogUtils.i("  btn_save = " + mPos.size());
            // 立即重新绘制之前保存的点
//            for (int i = 0; i < mPos.size() - 1; i++) {
//                canvas.drawLine(mPos.get(i).startX, mPos.get(i).startY, mPos.get(i + 1).startX, mPos.get(i + 1).startY, paint);
//            }
//            // 更新ImageView显示
//            iv_canvas.setImageBitmap(baseBitmap);
            final Path path = new Path();
            // 使用贝塞尔曲线连接所有点
            for (int i = 0; i < mPos.size() - 1; i++) {
                float startX = mPos.get(i).startX;
                float startY = mPos.get(i).startY;
                float endX = mPos.get(i + 1).startX;
                float endY = mPos.get(i + 1).startY;
                float controlX = (startX + endX) / 2;
                float controlY = (startY + endY) / 2;
                if (i == 0) {
                    path.moveTo(startX, startY);
                }
                path.quadTo(controlX, controlY, endX, endY);
            }

            // 创建属性动画
            final PathMeasure pathMeasure = new PathMeasure(path, false);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, pathMeasure.getLength());
            valueAnimator.setDuration(13000); // 13秒钟完成动画
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    // 获取当前路径上对应长度的点
                    float[] point = new float[2];
                    pathMeasure.getPosTan(value, point, null);
                    paint.setStrokeWidth(5);
                    long currentPlayTime = animation.getCurrentPlayTime() / 1000;
                    if (currentPlayTime > 2 && currentPlayTime < 5) {
                        paint.setColor(Color.BLACK);
                    } else if (currentPlayTime > 8) {
                        paint.setColor(Color.BLUE);
                    }
                    // 绘制点
                    canvas.drawPoint(point[0], point[1], paint);
                    iv_canvas.setImageBitmap(baseBitmap);
                }
            });
            valueAnimator.start();

            ToastUtils.show("清除画板成功，可以重新开始绘图");
        }
    }

    class Pos {
        float startX;
        float startY;

        public Pos(float startX, float startY) {
            this.startX = startX;
            this.startY = startY;
        }
    }
}