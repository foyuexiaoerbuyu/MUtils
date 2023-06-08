//package cn.mvp.mlibs.weight;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//
///**
// * 禁止滑动的RecyclerView
// * android:scrollbars="none"
// */
//public class NonScrollingRecyclerView extends RecyclerView {
//
//    public NonScrollingRecyclerView(Context context) {
//        super(context);
//    }
//
//    public NonScrollingRecyclerView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public NonScrollingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        return false;
//    }
//}