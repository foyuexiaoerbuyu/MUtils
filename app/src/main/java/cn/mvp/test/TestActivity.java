package cn.mvp.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.mvp.R;

public class TestActivity extends AppCompatActivity {
    private WindowManager mWindowManager;
    private FrameLayout mFloatLayout;
    private RecyclerView mRecyclerView;
    private int mLastX, mLastY;
    private int mDownX, mDownY;
    private boolean isLongPressed = false;

    public static void open(Context context) {
        Intent starter = new Intent(context, TestActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 创建悬浮窗口布局
        mFloatLayout = new FrameLayout(this);
        mFloatLayout.setBackgroundColor(Color.TRANSPARENT);

        // 初始化 RecyclerView
        mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter());

        // 将 RecyclerView 添加到悬浮窗口布局中
        TextView child = new TextView(this);
        mFloatLayout.addView(mRecyclerView);
        child.setLayoutParams(new ViewGroup.LayoutParams(225, 55));
        child.setText("悬浮框标题");
        mFloatLayout.addView(child);

        // 创建 WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 设置 WindowManager.LayoutParams
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = getResources().getDisplayMetrics().heightPixels / 4;
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = 0;
// 设置长按监听器
//        mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                isLongPressed = true;
//                return true;
//            }
//        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isLongPressed) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            int dx = (int) event.getRawX() - mLastX;
                            int dy = (int) event.getRawY() - mLastY;
                            mLastX = (int) event.getRawX();
                            mLastY = (int) event.getRawY();
                            moveFloatLayout(dx, dy);
                            break;
                        case MotionEvent.ACTION_UP:
                            isLongPressed = false;
                            break;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
//    mFloatLayout  mRecyclerView
//        MyItemTouchListener
        mRecyclerView.addOnItemTouchListener(new OnLongTouchListener(this, new RvOnItemTouchListener() {
            @Override
            public void onLongTouchListener() {
                isLongPressed = true;
            }
        }));

        // 将悬浮窗口布局添加到 WindowManager 中
        mWindowManager.addView(mFloatLayout, layoutParams);
    }

    private void moveFloatLayout(int dx, int dy) {
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatLayout.getLayoutParams();
        layoutParams.x += dx;
        layoutParams.y += dy;
        mWindowManager.updateViewLayout(mFloatLayout, layoutParams);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(TestActivity.this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(16, 16, 16, 16);
            textView.setBackgroundColor(Color.WHITE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("调试信息", "onClick:  " + textView.getText().toString());
                }
            });
            return new MyViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText("Item " + position);
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    interface RvOnItemTouchListener {

        void onLongTouchListener();
    }

    public class OnLongTouchListener implements RecyclerView.OnItemTouchListener {

        RvOnItemTouchListener mRvOnItemTouchListener;
        private GestureDetector gestureDetector;

        public OnLongTouchListener(Context context, RvOnItemTouchListener rvOnItemTouchListener) {
            mRvOnItemTouchListener = rvOnItemTouchListener;
            gestureDetector = new GestureDetector(context, new MyGestureListener());
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            gestureDetector.onTouchEvent(e); // 手势识别
            return false; // 返回 false，确保触摸事件能够传递给 RecyclerView
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            // 在这里可以处理其他触摸事件的逻辑
            // 如果需要传递给 RecyclerView 进行处理，可以调用 recyclerView.onTouchEvent(e)
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            // 空实现
        }

        private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public void onLongPress(MotionEvent e) {
                // 在长按事件中执行你的逻辑
                if (mRvOnItemTouchListener != null) {
                    mRvOnItemTouchListener.onLongTouchListener();
                }
                Log.i("调试信息", "onLongPress:  长按 ");
            }
        }
    }

}