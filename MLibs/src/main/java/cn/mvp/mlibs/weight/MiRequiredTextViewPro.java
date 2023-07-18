package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import cn.mvp.mlibs.R;

/**
 * https://blog.csdn.net/u013982652/article/details/94404711
 * Android自定义TextView实现必填项前面的*号
 * 另一种实现方式(推荐使用这种,有非必填情况的话不会有对齐问题)
 * <p>
 * <cn.mvp.mlibs.weight.MiRequiredTextViewPro
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:paddingLeft="12dp"
 * android:text="Hello World!"
 * app:prefixText="c11 "
 * app:prefixTextColor="@color/red"
 * app:prefixVisibility="invisible" />
 */
public class MiRequiredTextViewPro extends androidx.appcompat.widget.AppCompatTextView {

    private String prefixText;
    private int prefixTextColor;
    private int prefixVisibility;
    private int visible = 0, invisible = 1, gone = 2;

    public MiRequiredTextViewPro(Context context) {
        super(context);
        init(null);
    }

    public MiRequiredTextViewPro(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MiRequiredTextViewPro(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            prefixText = a.getString(R.styleable.CustomTextView_prefixText);
            prefixTextColor = a.getColor(R.styleable.CustomTextView_prefixTextColor, Color.RED);
            prefixVisibility = a.getInt(R.styleable.CustomTextView_prefixVisibility, View.VISIBLE);
            a.recycle();
        }
        updatePrefixVisibility();
        updatePrefixText();
        updatePrefixTextColor();
    }

    private void updatePrefixText() {
        if (prefixText != null) {
            setText(prefixText + getText());
        }
    }

    private void updatePrefixTextColor() {
        if (prefixText == null) {
            return;
        }
        SpannableString spannableString = new SpannableString(getText());
        spannableString.setSpan(new ForegroundColorSpan(prefixTextColor), 0, prefixText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }

    /**
     * invisible:不显示,但是占位置
     * gone:彻底不显示,不占位置
     */
    private void updatePrefixVisibility() {
        if (prefixVisibility == invisible) {
            Paint paint = new Paint();
            paint.setTextSize(getTextSize());
            float textWidth = paint.measureText(prefixText);
            prefixText = "";
            setPadding((int) textWidth + getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        } else if (prefixVisibility == gone) {
            prefixText = "";
        }
    }

    public void setPrefixText(String prefixText) {
        this.prefixText = prefixText;
        updatePrefixText();
    }

    public void setPrefixTextColor(int prefixTextColor) {
        this.prefixTextColor = prefixTextColor;
        updatePrefixTextColor();
    }

    public void setPrefixVisibility(int prefixVisibility) {
        this.prefixVisibility = prefixVisibility;
        updatePrefixVisibility();
    }
}