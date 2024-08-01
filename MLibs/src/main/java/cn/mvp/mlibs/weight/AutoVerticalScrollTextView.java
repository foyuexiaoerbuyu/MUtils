package cn.mvp.mlibs.weight;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;

import cn.mvp.mlibs.R;

/**
 * 自动垂直滚动的TextView
 * String[] strings = {"于晴兑换了云上汽车ESLOE水杯", "王东春兑换了云上汽车ESLOE水杯", "黄春岩兑换了炫彩时代雨点杯两件套", "赵轲兑换了炫彩多用组合盆", "关若瀛兑换了炫彩多用组合盆"};
 * <p>
 * AutoVerticalScrollTextView verticalScrollTV = findViewById(R.id.home_tv_notify_avst);
 * <p>
 * List<String> list = Arrays.asList(strings);
 * verticalScrollTV.setList(list);
 * verticalScrollTV.setTextStillTime(3000);//设置停留时长间隔
 * verticalScrollTV.setAnimTime(300);//设置进入和退出的时间间隔
 * verticalScrollTV.startAutoScroll();
 */
public class AutoVerticalScrollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory {

    List<String> list;
    private Context mContext;
    //mInUp,mOutUp分别构成向下翻页的进出动画
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;
    private boolean isRunning = true;
    private int number = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 199) {
                next();
                number++;
                setText(list.get(number % list.size()));
            }
        }
    };
    private int textStillTime = 3000;//停留时长间隔
    private int animTime = 300;//执行动画的时间

    public AutoVerticalScrollTextView(Context context) {
        this(context, null);
    }

    public AutoVerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();

    }

    private void init() {

        setFactory(this);

        mInUp = createAnim(true, true);
        mOutUp = createAnim(false, true);

        setInAnimation(mInUp);//当View显示时动画资源ID
        setOutAnimation(mOutUp);//当View隐藏是动画资源ID。

    }

    private Rotate3dAnimation createAnim(boolean turnIn, boolean turnUp) {

        Rotate3dAnimation rotation = new Rotate3dAnimation(turnIn, turnUp);
        rotation.setDuration(animTime);//执行动画的时间
        rotation.setFillAfter(false);//是否保持动画完毕之后的状态
        rotation.setInterpolator(new AccelerateInterpolator());//设置加速模式

        return rotation;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void startAutoScroll() {
        number = 0;
        isRunning = true;
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    handler.sendEmptyMessage(199);
                    SystemClock.sleep(textStillTime);

                }
            }
        }.start();
    }

    public void stopAutoScroll() {
        isRunning = false;
    }

    /**
     * 设置停留时长间隔
     */
    public void setTextStillTime(int textStillTime) {
        this.textStillTime = textStillTime;
    }

    /**
     * 设置进入和退出的时间间隔
     */
    public void setAnimTime(int animTime) {
        this.animTime = animTime;
    }

    //这里返回的TextView，就是我们看到的View,可以设置自己想要的效果
    public View makeView() {

        TextView textView = new TextView(mContext);
//        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(33);
        textView.setSingleLine(true);
        textView.setPadding(20, 30, 20, 30);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(getResources().getColor(R.color.white));
        return textView;

    }

    //定义动作，向上滚动翻页
    public void next() {
        //显示动画
        if (getInAnimation() != mInUp) {
            setInAnimation(mInUp);
        }
        //隐藏动画
        if (getOutAnimation() != mOutUp) {
            setOutAnimation(mOutUp);
        }
    }

    class Rotate3dAnimation extends Animation {
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private float mCenterX;
        private float mCenterY;
        private Camera mCamera;

        public Rotate3dAnimation(boolean turnIn, boolean turnUp) {
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight();
            mCenterX = getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1 : -1;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection * mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection * mCenterY * (interpolatedTime), 0.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

}
 