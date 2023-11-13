//package cn.mvp.test;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//
//import cn.mvp.R;
//
//public class MCheckbox extends LinearLayout implements View.OnClickListener {
//    private boolean isChecked;
//    private int checkedImageRes;
//    private int uncheckedImageRes;
//    private OnCheckedChangeListener onCheckedChangeListener;
//
//    public MCheckbox(Context context) {
//        super(context);
//        init(context, null);
//    }
//
//    public MCheckbox(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//    }
//
//    public MCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    private void init(Context context, AttributeSet attrs) {
//        LayoutInflater.from(context).inflate(R.layout.m_check_box, this);
//
//        if (attrs != null) {
//            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckableImageView);
//            isChecked = typedArray.getBoolean(R.styleable.CheckableImageView_checked, false);
//            checkedImageRes = typedArray.getResourceId(R.styleable.CheckableImageView_checkedImage, 0);
//            uncheckedImageRes = typedArray.getResourceId(R.styleable.CheckableImageView_uncheckedImage, 0);
//            typedArray.recycle();
//        }
//
//        updateImage();
//    }
//
//    public boolean isChecked() {
//        return isChecked;
//    }
//
//    public void setChecked(boolean checked) {
//        isChecked = checked;
//        updateImage();
//        if (onCheckedChangeListener != null) {
//            onCheckedChangeListener.onCheckedChanged(this, isChecked);
//        }
//    }
//
//    public void setCheckedImageResource(int resId) {
//        checkedImageRes = resId;
//        updateImage();
//    }
//
//    public void setUncheckedImageResource(int resId) {
//        uncheckedImageRes = resId;
//        updateImage();
//    }
//
//    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
//        onCheckedChangeListener = listener;
//    }
//
//    private void updateImage() {
//        if (isChecked) {
//            setImageResource(checkedImageRes);
//        } else {
//            setImageResource(uncheckedImageRes);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        setChecked(!isChecked);
//    }
//
//    public interface OnCheckedChangeListener {
//        void onCheckedChanged(MCheckbox view, boolean isChecked);
//    }
//}