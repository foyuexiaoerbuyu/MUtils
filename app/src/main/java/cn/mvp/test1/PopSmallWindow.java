//package cn.mvp.test1;
//
//
//import android.content.Context;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//
//import cn.mvp.R;
//
//public class PopSmallWindow extends LinearLayout {
//
//    /**
//     * 图标起始位置
//     */
//    private static int imageX = 0;
//    private static int imageY = 500;
//    private static PopupWindow popWindow;
//    private static boolean isPoped = false;  //是否弹出了窗口
//
//    public PopSmallWindow(Context context) {
//        super(context);
//    }
//
//    public static void initSmallWindow(){
//        if (isPoped == false){
//            isPoped = true;
//            View gh_View = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.gh_layout, null, false);
//            //1.构造一个PopupWindow，参数依次是加载的View，宽高
//            popWindow = new PopupWindow(gh_View,
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
//            popWindow.setTouchable(true);
//            popWindow.setTouchInterceptor(new FloatingOnTouchListener()); //设置可拖动
//            popWindow.setBackgroundDrawable(MainActivity.mContext.getResources().getDrawable(R.color.transparent));  //设置背景 （transparent是自定义的透明颜色 #00000000）
//
//            // 设置好参数之后再show
//            popWindow.showAtLocation(gh_View, Gravity.TOP | Gravity.LEFT,imageX,imageY);
//        }
//    }
//
//    public static void dismiss(){
//        if (popWindow != null && isPoped == true){
//            isPoped =false;
//            popWindow.dismiss();
//        }
//    }
//
//    private static class FloatingOnTouchListener implements OnTouchListener {
//        private int x;
//        private int y;
//        private int downX;
//        private int downY;
//
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:  //按下
//                    x = (int) event.getRawX();
//                    y = (int) event.getRawY();
//                    downX = (int) event.getRawX();
//                    downY = (int) event.getRawY();
//                    break;
//                case MotionEvent.ACTION_MOVE:  //移动
//                    int nowX = (int) event.getRawX();
//                    int nowY = (int) event.getRawY();
//                    int movedX = nowX - x;
//                    int movedY = nowY - y;
//                    x = nowX;
//                    y = nowY;
//                    imageX = imageX + movedX;
//                    imageY = imageY + movedY;
//                    popWindow.update(imageX,imageY,-1,-1); // -1 应该是代表忽略
//                    break;
//                case MotionEvent.ACTION_UP:  //单击事件
//                    int upX = (int) event.getRawX();
//                    int upY = (int) event.getRawY();
//                    if (downX == upX && downY == upY) {
//                       new PopBigWindow(MainActivity.mContext);
//                    }
//                    break;
//                default:
//                    break;
//            }
//            return false;
//        }
//    }
//}
//
