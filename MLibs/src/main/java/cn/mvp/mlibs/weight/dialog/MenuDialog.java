package cn.mvp.mlibs.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.mvp.mlibs.R;

/**
 * 列表弹框
 */
public class MenuDialog<T> extends Dialog {
    private List<T> mData;
    private IShowContent<T> mIShowContent;
    private OnItemClickListener<T> mClickListener;

    private MenuDialog(Context context) {
        super(context);
    }

    public MenuDialog(Context context, List<T> data, IShowContent<T> iShowContent) {
        super(context);
        mData = data;
        mIShowContent = iShowContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_dialog);

        RecyclerView recyclerView = findViewById(R.id.menu_dialog_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        MenuDialogAdapter<T> adapter = new MenuDialogAdapter<>(mData, mIShowContent, mClickListener);

        recyclerView.setAdapter(adapter);

//        // 设置对话框大小和位置
//        Window window = getWindow();
//        if (window != null) {
//            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//            int windowWidth = (2 * displayMetrics.widthPixels) / 3; // 计算出屏幕宽度的2/3
//            window.setLayout(windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//            WindowManager.LayoutParams layoutParams = window.getAttributes();
//            layoutParams.gravity = Gravity.CENTER; // 修改为居中显示
//            window.setAttributes(layoutParams);
//        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T entity);

        default void onItemLongClick(View view, int position, T entity) {

        }
    }

    public interface IShowContent<T> {

        String showContent(T entity);
    }


    private static class MenuDialogAdapter<T> extends RecyclerView.Adapter<MenuDialogAdapter<T>.ViewHolder> {
        private List<T> mData;
        private IShowContent<T> mIShowContent;
        private OnItemClickListener<T> mClickListener;

        public MenuDialogAdapter(List<T> data, IShowContent<T> iShowContent, OnItemClickListener<T> clickListener) {
            mData = data;
            mIShowContent = iShowContent;
            mClickListener = clickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_dialog_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            T entity = mData.get(position);
            if (mIShowContent != null) {
                holder.textView.setText(mIShowContent.showContent(entity));
                holder.itemView.setOnClickListener(v -> {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(holder.itemView, position, entity);
                    }
                });
                holder.itemView.setOnLongClickListener(v -> {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(holder.itemView, position, entity);
                    }
                    return true;
                });
            }
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.menu_dialog_item_tv);
            }
        }
    }

}
