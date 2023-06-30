package cn.mvp.mlibs.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 禁止滑动的RecyclerView
 * android:scrollbars="none"
 */
public class NoScrollingRecyclerView extends RecyclerView {

    public NoScrollingRecyclerView(Context context) {
        super(context);
    }

    public NoScrollingRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }
}