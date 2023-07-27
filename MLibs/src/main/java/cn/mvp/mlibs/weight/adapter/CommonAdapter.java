package cn.mvp.mlibs.weight.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

import cn.mvp.mlibs.weight.adapter.base.ItemViewDelegate;
import cn.mvp.mlibs.weight.adapter.base.ViewHolder;

/**
 * Created by zhy on 16/4/9.
 */
public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    public CommonAdapter(final Context context, final int layoutId, List<T> datas) {
        super(context, datas);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(ViewHolder holder, T t, int position);


    public void addDatas(List<T> newDatas) {
        mDatas.addAll(newDatas);
        notifyItemRangeInserted(getItemCount(), newDatas.size());
    }

    public void setDatas(List<T> newDatas) {
        mDatas.clear();
        mDatas.addAll(newDatas);
        notifyItemRangeInserted(0, newDatas.size());
    }
}
