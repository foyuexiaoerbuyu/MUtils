//package cn.mvp.mlibs.weight;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//
//import androidx.cardview.widget.CardView;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//public class CardViewConstraintLayout extends CardView {
//
//    private ConstraintLayout mConstraintLayout;
//
//    public CardViewConstraintLayout(Context context) {
//        super(context);
//        init();
//    }
//
//    public CardViewConstraintLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public CardViewConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void init() {
//        removeAllViews();
//        mConstraintLayout = new ConstraintLayout(getContext());
//        addView(mConstraintLayout, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//    }
//}