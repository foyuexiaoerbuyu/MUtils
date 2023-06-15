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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerButtonDrawable();
    }

    /**
     * @return 。这个数组包含了左、上、右、下的Drawable，您可以通过索引来获得相应位置的Drawable。对于CheckBox，我们可以使用左边（索引0）的Drawable
     */
    private Drawable getCompatButtonDrawable() {
        Drawable[] drawables = getCompoundDrawables();
        return drawables[0];
    }

    private void centerButtonDrawable() {
        Drawable buttonDrawable = getCompatButtonDrawable();
        // getBackground();
        if (buttonDrawable != null) {
            // 获取ButtonDrawable的宽度和高度
            int drawableWidth = buttonDrawable.getIntrinsicWidth();
            int drawableHeight = buttonDrawable.getIntrinsicHeight();

            // 计算ButtonDrawable的居中位置
            int left = (getWidth() - drawableWidth) / 2;
            int top = (getHeight() - drawableHeight) / 2;
            int right = left + drawableWidth;
            int bottom = top + drawableHeight;

            // 设置ButtonDrawable的位置
            buttonDrawable.setBounds(left, top, right, bottom);
        }
    }

    //现在，当MiCheckBox的文字为空时，ButtonDrawable应该会居中显示。请注意，这个实现假设MiCheckBox的宽度和高度足够容纳ButtonDrawable。如果不是这样，可能需要调整MiCheckBox的大小或ButtonDrawable的大小以适应居中显示。
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