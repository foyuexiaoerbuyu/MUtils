package cn.mvp.mlibs.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import cn.mvp.mlibs.R;
import cn.mvp.mlibs.utils.UIUtils;

public class MiCheckBox extends AppCompatCheckBox {
    private Drawable selSrc;
    private Drawable unSelSrc;

    public MiCheckBox(Context context) {
        this(context, null);
    }

    public MiCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public MiCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MiSelImg, defStyleAttr, 0);

        selSrc = a.getDrawable(R.styleable.MiSelImg_sel_src);
        unSelSrc = a.getDrawable(R.styleable.MiSelImg_un_sel_src);

        a.recycle();

        updateButtonDrawable();
    }

    private void updateButtonDrawable() {
        setBackground(null);
        if (isChecked()) {
            setButtonDrawable(selSrc);
        } else {
            setButtonDrawable(unSelSrc);
        }
    }

    public void set_sel_src(int selSrc) {
        this.selSrc = UIUtils.getDrawable(selSrc);
    }

    public void set_un_sel_src(int unSelSrc) {
        this.unSelSrc = UIUtils.getDrawable(unSelSrc);
    }

    public void initSelImg(int selSrc, int unSelSrc) {
        this.selSrc = UIUtils.getDrawable(selSrc);
        this.unSelSrc = UIUtils.getDrawable(unSelSrc);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        updateButtonDrawable();
    }
}