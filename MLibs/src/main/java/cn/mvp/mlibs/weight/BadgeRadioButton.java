package cn.mvp.mlibs.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;

/**
 * 单选按钮 带角标
 * https://blog.csdn.net/yoonerloop/article/details/107589057
 */
public class BadgeRadioButton extends AppCompatRadioButton {

    private Paint mPaint;
    private boolean isShowDot;
    private boolean isShowNumberDot;

    //小红点半径
    private final int circleDotRadius = dp2px(4);
    //icon的尺寸，高 == 宽
    private final int drawableSize = dp2px(22);
    //小红点距离icon的padding
    private final int drawablePadding = dp2px(2);
    //矩形角标数字左右padding
    private final int rectFPaddingX = dp2px(4);
    //矩形角标数字上下padding
    private final int rectFPaddingY = dp2px(3);
    //角标矩形背景
    private int rectFRadius = dp2px(8);
    //圆点和角标颜色
    private final int PAINT_COLOR_DEFAULT = Color.parseColor("#FD481E");

    private String numberText;

    /**
     * 圆点和未读消息的坐标
     */
    private float pivotX;
    private float pivotY;

    public BadgeRadioButton(Context context) {
        super(context);
        init();
        updatePadding();
    }

    public BadgeRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        updatePadding();
    }

    public BadgeRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        updatePadding();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(PAINT_COLOR_DEFAULT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getScrollX(), 0);

        //调整角标位置
        pivotX = getWidth() / 2 + drawableSize / 2 + drawablePadding;
        pivotY = getHeight() / 2 - drawableSize /*/ 2 - drawablePadding*/;

        if (isShowDot) {
            canvas.drawCircle(pivotX, pivotY, circleDotRadius, mPaint);
        } else if (isShowNumberDot && !TextUtils.isEmpty(numberText)) {
            if (Integer.parseInt(numberText) > 99) {
                numberText = "99+";
            }
            if (numberText.length() == 1) {
                rectFRadius = dp2px(6);
            }
            mPaint.setColor(PAINT_COLOR_DEFAULT);
            mPaint.setTextSize(dp2px(12));
            float textWidth = mPaint.measureText(numberText);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float textHeight = Math.abs((fontMetrics.top + fontMetrics.bottom));
            RectF rect = new RectF(pivotX - dp2px(4), pivotY - textHeight / 2 - rectFPaddingY, pivotX + textWidth + rectFPaddingX * 2, pivotY + textHeight / 2 + rectFPaddingY);
            canvas.drawRoundRect(rect, rectFRadius, rectFRadius, mPaint);
            mPaint.setColor(Color.parseColor("#ffffff"));
            float baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2 - dp2px(1);
            mPaint.setTextAlign(Paint.Align.CENTER);
            //绘制文本
            canvas.drawText(numberText, pivotX + textWidth / 2 + rectFPaddingX - dp2px(4) / 2, baseline, mPaint);
        }
    }

    /**
     * `
     * 设置是否显示小圆点
     */
    public void setShowSmallDot(boolean isShowDot) {
        this.isShowDot = isShowDot;
        invalidate();
    }

    /**
     * 设置是否显示数字
     */
    public void setNumberDot(boolean isShowNumberDot, @NonNull String text) {
        this.isShowNumberDot = isShowNumberDot;
        this.numberText = text;
        if (isShowNumberDot) {
            isShowDot = false;
        }
        invalidate();
    }

    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 加一个默认的顶部内边距,否则显示圆点不全
     */
    private void updatePadding() {
        // 获取当前的内边距
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        // 增加顶部内边距
        int newTopPadding = top + dp2px(12);

        // 设置新的内边距
        setPadding(left, newTopPadding, right, bottom);
    }
}