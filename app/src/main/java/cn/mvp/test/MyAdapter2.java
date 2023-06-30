package cn.mvp.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import cn.mvp.R;
import cn.mvp.mlibs.utils.UIUtils;

public class MyAdapter2<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mItemView;
    private List<T> mDataList;
    private BindViewByData mBindViewByData;
    //    private boolean mIsLoading = false;
    private boolean mIsMoreDataAvailable = false;
    private OnLoadMoreListener mOnLoadMoreListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MyAdapter2(int itemView, List<T> dataList, BindViewByData<T> bindViewByData) {
        mItemView = itemView;
        mDataList = dataList;
        mBindViewByData = bindViewByData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            // Inflate item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(mItemView, parent, false);
            return new ItemViewHolder(view, mBindViewByData);
        } else {
            // Inflate loading layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.default_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position < mDataList.size() && holder instanceof ItemViewHolder) {
            T data = mDataList.get(position);
            //绑定数据到项目视图
            ((ItemViewHolder) holder).bindData(data);
        } else if (holder instanceof LoadingViewHolder && mOnLoadMoreListener != null) {
            //显示加载进度条或旋转器
            //你可以在这里自定义加载布局
            if (!holder.itemView.canScrollVertically(1)) {
                mIsMoreDataAvailable = false;
            } else {
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size() + (mIsMoreDataAvailable ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position < mDataList.size() ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mIsMoreDataAvailable = true;
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        mIsMoreDataAvailable = moreDataAvailable;
    }

    /**
     * @param datas             新增数据
     * @param moreDataAvailable 加载的数量是否小于分页数量
     */
    public void addDatas(List<T> datas, boolean moreDataAvailable) {
        setMoreDataAvailable(moreDataAvailable);
        loadingComplete();
        if (datas == null) return;
        int startPosition = (mIsMoreDataAvailable ? mDataList.size() - 1 : mDataList.size());
        mDataList.addAll(datas);
        notifyItemRangeInserted(startPosition, datas.size());
    }

    public void addDatas(List<T> datas) {
        if (datas == null) return;
        int startPosition = (mIsMoreDataAvailable ? mDataList.size() - 1 : mDataList.size());
        mDataList.addAll(datas);
        notifyItemRangeInserted(startPosition, datas.size());
    }

    public void setDatas(List<T> dataList) {
        if (dataList == null) return;
        mDataList.clear();
        mDataList.addAll(dataList);
        notifyItemRangeInserted(0, dataList.size());
    }

    public void loadingComplete() {
        setMoreDataAvailable(false);
        notifyDataSetChanged();
    }

    public void loadingComplete(boolean moreDataAvailable) {
        setMoreDataAvailable(moreDataAvailable);
        notifyDataSetChanged();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface BindViewByData<T> {
//        void bindView();

//        void bindView(View itemView, T data);

        void bindView(BaseHolder holder, View itemView, T data);
    }

    public static class ItemViewHolder<T> extends BaseHolder {
        private View mItemView;
        private BindViewByData mBindViewByData;

        public ItemViewHolder(@NonNull View itemView, BindViewByData bindViewByData) {
            super(itemView);
            mItemView = itemView;
            // 在这里初始化项视图
            mBindViewByData = bindViewByData;
        }

        public void bindData(T data) {
            mBindViewByData.bindView(this, mItemView, data);
            //将数据绑定到item视图
        }
    }

    private static class LoadingViewHolder extends BaseHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            // 在这里初始化加载视图
        }
    }

    static class BaseHolder extends RecyclerView.ViewHolder {
        private View mItemView;

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        public BaseHolder setText(int id, String text) {
            ((TextView) mItemView.findViewById(id)).setText(text);
            return this;
        }

        public BaseHolder setText(int id, int textId) {
            ((TextView) mItemView.findViewById(id)).setText(UIUtils.getString(textId));
            return this;
        }

        public BaseHolder setTextColor(int id, int textColorId) {
            ((TextView) mItemView.findViewById(id)).setTextColor(UIUtils.getColor(textColorId));
            return this;
        }

        public BaseHolder setTextSize(int id, int textSize) {
            ((TextView) mItemView.findViewById(id)).setTextSize(textSize);
            return this;
        }

        public BaseHolder setImageResource(int id, @DrawableRes int drawable) {
            ((ImageView) mItemView.findViewById(id)).setImageResource(drawable);
            return this;
        }

        public BaseHolder setBackground(int id, @DrawableRes int drawableId) {
            mItemView.findViewById(id).setBackground(UIUtils.getDrawable(drawableId));
            return this;
        }

        public BaseHolder setBackgroundColor(int id, @DrawableRes int color) {
            mItemView.findViewById(id).setBackgroundColor(color);
            return this;
        }

        public BaseHolder setBackgroundResource(int id, @DrawableRes int bgRes) {
            mItemView.findViewById(id).setBackgroundResource(bgRes);
            return this;
        }

        public BaseHolder setVisibility(int id, boolean isVisibility) {
            mItemView.findViewById(id).setVisibility(isVisibility ? View.VISIBLE : View.GONE);
            return this;
        }

        public BaseHolder setChecked(int id, boolean isVisibility) {
            ((CheckBox) mItemView.findViewById(id)).setChecked(isVisibility);
            return this;
        }

        public BaseHolder setProgressMax(int id, int maxVal) {
            ((ProgressBar) mItemView.findViewById(id)).setMax(maxVal);
            return this;
        }

        public BaseHolder setProgress(int id, int maxVal) {
            ((ProgressBar) mItemView.findViewById(id)).setProgress(maxVal);
            return this;
        }

        public BaseHolder setOnItemClickListener(int id, View.OnClickListener onClickListener) {
            mItemView.setOnClickListener(onClickListener);
            return this;
        }

        public BaseHolder setOnClickListener(int id, View.OnClickListener onClickListener) {
            mItemView.findViewById(id).setOnClickListener(onClickListener);
            return this;
        }

        public View getView(int id) {

            return mItemView.findViewById(id);
        }

    }

    public void addSwipeRefreshToRecyclerView(RecyclerView recyclerView, SwipeRefreshLayout.OnRefreshListener listener) {
        //获取RecyclerView的父布局
        ViewGroup parent = (ViewGroup) recyclerView.getParent();
        int index = parent.indexOfChild(recyclerView);
        //从父布局中移除RecyclerView
        parent.removeView(recyclerView);
        //创建SwipeRefreshLayout
        mSwipeRefreshLayout = new SwipeRefreshLayout(recyclerView.getContext());
        //设置下拉刷新监听器
        mSwipeRefreshLayout.setOnRefreshListener(listener);

        //将RecyclerView添加到SwipeRefreshLayout
        mSwipeRefreshLayout.addView(recyclerView);

        //将SwipeRefreshLayout添加到原来RecyclerView的位置
        parent.addView(mSwipeRefreshLayout, index);
    }

    public void setRefreshingComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        setMoreDataAvailable(true);
        notifyDataSetChanged();
    }

    public void setRefreshing(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

}