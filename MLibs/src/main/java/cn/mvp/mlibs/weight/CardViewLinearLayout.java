//package cn.mvp.mlibs.weight;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//
//import androidx.cardview.widget.CardView;
//
//public class CardViewLinearLayout extends CardView {
//
//    private LinearLayout linearLayout;
//
//    public CardViewLinearLayout(Context context) {
//        super(context);
//        init();
//    }
//
//    public CardViewLinearLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public CardViewLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void init() {
//        removeAllViews();
//        linearLayout = new LinearLayout(getContext());
//        addView(linearLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        if (getOrientation() != -1) {
//            linearLayout.setOrientation(getOrientation());
//        }
//    }
//
//    public int getOrientation() {
//        return linearLayout.getOrientation();
//    }
//
//    public void setOrientation(int orientation) {
//        linearLayout.setOrientation(orientation);
//    }
//}