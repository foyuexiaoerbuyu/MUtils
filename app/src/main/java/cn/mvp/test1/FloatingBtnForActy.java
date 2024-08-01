package cn.mvp.test1;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * https://juejin.cn/post/6911582503212384263?searchId=20231117141230EBAC4691F30D65051BC1#heading-3
 * 方案二：addContentView实现
 * 在单Activity内悬浮的可拖拽按钮
 *
 * @author zhuxiaoxin
 * 可拖拽贴边的view
 */
public class FloatingBtnForActy extends RelativeLayout {

    //手指拖拽得到的位置
    int mLeft, mRight, mTop, mBottom;

    //view所在的位置
    int mLastX, mLastY;

    /**
     * 屏幕宽度|高度
     */
    int mScreenWidth, mScreenHeight;

    /**
     * view的宽度|高度
     */
    int mWidth, mHeight;


    /**
     * 是否在拖拽过程中
     */
    boolean isDrag = false;

    /**
     * 系统最小滑动距离
     *
     * @param context
     */
    int mTouchSlop = 0;

    public FloatingBtnForActy(Context context, final int floatImgId) {
        this(context, null);
        setClickable(true);
        final ImageView floatView = new ImageView(context);
        floatView.setImageResource(floatImgId);
        floatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击了悬浮球", Toast.LENGTH_SHORT).show();
            }
        });
        floatView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "长按了悬浮球", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(floatView, params);
    }

    public FloatingBtnForActy(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingBtnForActy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLeft = getLeft();
                mRight = getRight();
                mTop = getTop();
                mBottom = getBottom();
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int dx = x - mLastX;
                int dy = y - mLastY;
                if (Math.abs(dx) > mTouchSlop) {
                    isDrag = true;
                }
                mLeft += dx;
                mRight += dx;
                mTop += dy;
                mBottom += dy;
                if (mLeft < 0) {
                    mLeft = 0;
                    mRight = mWidth;
                }
                if (mRight >= mScreenWidth) {
                    mRight = mScreenWidth;
                    mLeft = mScreenWidth - mWidth;
                }
                if (mTop < 0) {
                    mTop = 0;
                    mBottom = getHeight();
                }
                if (mBottom > mScreenHeight) {
                    mBottom = mScreenHeight;
                    mTop = mScreenHeight - mHeight;
                }
                mLastX = x;
                mLastY = y;
                //根据拖动举例设置view的margin参数，实现拖动效果
                LayoutParams params = new LayoutParams(mWidth, mHeight);
                params.setMargins(mLeft, mTop, 0, 0);
                setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起，执行贴边动画
                if (isDrag) {
                    startAnim();
                    isDrag = false;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    //执行贴边动画
    private void startAnim() {
        ValueAnimator valueAnimator;
        if (mLeft < mScreenWidth / 2) {
            valueAnimator = ValueAnimator.ofInt(mLeft, 0);
        } else {
            valueAnimator = ValueAnimator.ofInt(mLeft, mScreenWidth - mWidth);
        }
        //动画执行时间
        valueAnimator.setDuration(100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeft = (int) animation.getAnimatedValue();
                //动画执行依然是使用设置margin参数实现
                LayoutParams params = new LayoutParams(mWidth, mHeight);
                params.setMargins(mLeft, getTop(), 0, 0);
                setLayoutParams(params);
            }
        });
        valueAnimator.start();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mWidth == 0) {
            //获取view的高宽
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    private RelativeLayout mFloatContainer;

    public void show(Activity activity) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if (mFloatContainer == null) {
            mFloatContainer = new RelativeLayout(activity);
        }
        RelativeLayout.LayoutParams floatViewParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        floatViewParams.setMargins(0, (int) (mScreenHeight * 0.4), 0, 0);
        mFloatContainer.addView(this, floatViewParams);
        activity.addContentView(mFloatContainer, params);

    }

}
