package cn.mvp.test;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhy.adapter.recyclerview.CommonAdapter;

import java.util.List;

public class MiRecyclerView<T> extends RecyclerView {
    private CommonAdapter<T> mAdapter; // 适配器

    public MiRecyclerView(Context context) {
        super(context);
    }

    public MiRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 设置数据方法
    public void setData(List<T> data, int layoutId, ConvertCallback convertCallback) {
        if (getLayoutManager() == null) {
            setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (mAdapter == null) {
            mAdapter = new CommonAdapter<T>(getContext(), layoutId, data) {
                @Override
                protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, T t, int position) {
                    // 调用外部传入的convertCallback，将holder、item和position传递给外部处理
                    convertCallback.convert(holder, t, position);
                }
            };
            setAdapter(mAdapter); // 设置适配器
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public CommonAdapter<T> getAdapter() {
        return mAdapter;
    }

    // 定义回调接口，用于convert方法的处理
    public interface ConvertCallback {
        void convert(ViewHolder holder, Object item, int position);
    }
}

