package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;


import cn.mvp.mlibs.R;

public class MiRadioButton extends AppCompatRadioButton {
    private Drawable selSrc;
    private Drawable unSelSrc;

    public MiRadioButton(Context context) {
        this(context, null);
    }

    public MiRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MiSelImg);
        selSrc = typedArray.getDrawable(R.styleable.MiSelImg_sel_src);
        unSelSrc = typedArray.getDrawable(R.styleable.MiSelImg_un_sel_src);
        typedArray.recycle();

        // 设置选中和未选中状态的图片
        setButtonDrawable(null);
        if (selSrc != null && unSelSrc != null) {
            setCompoundDrawablesWithIntrinsicBounds(null, unSelSrc, null, null);
        }

        // 监听选中状态变化，改变图片
        setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setCompoundDrawablesWithIntrinsicBounds(null, selSrc, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, unSelSrc, null, null);
            }
        });
    }
}