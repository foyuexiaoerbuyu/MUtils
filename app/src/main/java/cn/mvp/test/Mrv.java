package cn.mvp.test;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.mvp.mlibs.weight.adapter.CommonAdapter;
import cn.mvp.mlibs.weight.adapter.wrapper.LoadMoreWrapper;

public class Mrv<E> extends RecyclerView {

    private CommonAdapter<E> mMAdapter;
    private LoadMoreWrapper<E> mLoadMoreWrapper;

    public Mrv(@NonNull Context context) {
        super(context);
    }

    public Mrv(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Mrv(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapters(int itemLayout, List<E> datas, IItemConvert<E> itemConvert) {
        mMAdapter = new CommonAdapter<E>(getContext(), itemLayout, datas) {
            @Override
            protected void convert(cn.mvp.mlibs.weight.adapter.base.ViewHolder holder, E o, int position) {
                itemConvert.convert(holder, o, position);
            }
        };
        setAdapter(mMAdapter);
    }

    public void setAdapterRef(int itemLayout, List<E> datas, IRefCallBack<E> itemConvert) {
        mMAdapter = new CommonAdapter<E>(getContext(), itemLayout, datas) {
            @Override
            protected void convert(cn.mvp.mlibs.weight.adapter.base.ViewHolder holder, E o, int position) {
                itemConvert.convert(holder, o, position);
            }
        };
        mLoadMoreWrapper = new LoadMoreWrapper<>(mMAdapter);
        setAdapter(mLoadMoreWrapper);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                itemConvert.onLoadMore(mLoadMoreWrapper);
            }
        });
    }

    public CommonAdapter<E> getMAdapter() {
        return mMAdapter;
    }

    public void addDatas(List<E> newDatas) {
        if (mMAdapter != null) {
            mMAdapter.addDatas(newDatas);
        } else if (mLoadMoreWrapper != null) {
        }
    }

    public void setDatas(List<E> newDatas) {
        if (mMAdapter != null) {
            mMAdapter.setDatas(newDatas);
        }
    }

    public interface IItemConvert<E> {
        void convert(cn.mvp.mlibs.weight.adapter.base.ViewHolder holder, E t, int position);
    }

    public interface IRefCallBack<E> {
        void convert(cn.mvp.mlibs.weight.adapter.base.ViewHolder holder, E t, int position);

        void onRef();

        void onLoadMore(LoadMoreWrapper<E> loadMoreWrapper);
    }
}