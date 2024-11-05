package cn.mvp.mlibs.weight;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.mvp.mlibs.R;


/**
 * https://blog.csdn.net/qq_35698774/article/details/82344830
 * https://freewechat.com/a/MzAxMTI4MTkwNQ==/2650826772/1
 * http://www.demodashi.com/demo/16909.html
 * https://download.csdn.net/download/JczmDeveloper/13206695
 * 两端分散对齐TextView 推荐 AlignTextView
 * Created by wjs on 2018/9/3.
 */
@Deprecated
@SuppressLint("AppCompatCustomView")
public class MiAlignTextView extends TextView {

    private Align mAlign = Align.HORTAL;
    private int canvasLength;
    private double lineSpacing;

    public MiAlignTextView(Context context) {
        super(context);
    }

    public MiAlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!this.isInEditMode())
            initTypedArray(context, attrs, -1, R.style.AlignTextView_Default);
    }

    public MiAlignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!this.isInEditMode())
            initTypedArray(context, attrs, defStyle, R.style.AlignTextView_Default);
    }

    @SuppressLint("NewApi")
    public MiAlignTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!this.isInEditMode())
            initTypedArray(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initTypedArray(Context context, AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MiAlignTextView, defStyleAttr, defStyleRes);
        int alignStyle = a.getInt(R.styleable.MiAlignTextView_align, 0);
        a.recycle();
        switch (alignStyle) {
            case 1:
                mAlign = Align.HORTAL;
                break;
            case 2:
                mAlign = Align.VERCAL;
                break;
            default:
                mAlign = Align.HORTAL;
                break;
        }
    }

    public enum Align {
        HORTAL,
        VERCAL,
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String text = getText().toString();
        Paint paint = new Paint();
        paint.setTextSize(getTextSize());
        paint.setColor(getCurrentTextColor());
        paint.setAntiAlias(true);
        int textLengthHeight = 0;
        Rect r = new Rect();
        int[] Arr = new int[(text.length())];
        float xOffset = 0;
        int yOffset = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        switch (mAlign) {

            case VERCAL:
                canvasLength = getMeasuredHeight();
                lineSpacing = 0.0;
                if (!TextUtils.isEmpty(getText()) && getText().length() > 1) {

                    for (int i = 0; i < text.length(); i++) {
                        paint.getTextBounds(text.substring(i, i + 1), 0, 1, r);
                        textLengthHeight = textLengthHeight + (r.bottom - r.top);
                        Arr[i] = r.bottom - r.top;
                        if (i == 0) {
                            Arr[i] = r.bottom - r.top + 12;
                        }
                    }
                    lineSpacing = (double) (canvasLength - textLengthHeight - 16) / (text.length() - 1);
                }

                float arrlength = 0;
                for (int i = 0; i < text.length(); i++) {
                    arrlength = arrlength + Arr[i];
                    canvas.drawText(text.substring(i, i + 1), xOffset, (float) (i * lineSpacing + arrlength), paint);
                }

                break;
            case HORTAL:
            default:
                canvasLength = getMeasuredWidth();
                float textLength = paint.measureText(text);
                lineSpacing = 0;
                if (!TextUtils.isEmpty(getText()) && getText().length() > 1) {
                    lineSpacing = (canvasLength - textLength) / (text.length() - 1) / paint.getTextSize();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.translate((float) (-lineSpacing * getTextSize() / 2), 0);
                    paint.setLetterSpacing((float) lineSpacing);
                    canvas.drawText(text, xOffset, yOffset, paint);
                } else {
                    float spacePercentage = (float) lineSpacing;
                    for (int i = 0; i < text.length(); i++) {
                        if (canvas != null) {
                            String s = String.valueOf(text.charAt(i));
                            canvas.drawText(s, xOffset, yOffset, paint);
                            canvas.translate(paint.measureText(s), 0);
                            canvas.translate(getTextSize() * spacePercentage, 0);
                        }
                    }
                }
                break;
        }


    }

    /**
     * 根据指定汉字数量修改宽度
     *
     * @param words 汉字宽度数量
     */
    public void modifyWidthByWords(int words) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = (int) (getPaint().measureText("汉") * words + getPaddingLeft() + getPaddingRight());
        setLayoutParams(layoutParams);
    }
}
