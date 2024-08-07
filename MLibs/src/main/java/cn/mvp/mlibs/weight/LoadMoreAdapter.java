package cn.mvp.mlibs.weight;

import android.util.SparseArray;
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

import java.util.ArrayList;
import java.util.List;

import cn.mvp.mlibs.R;
import cn.mvp.mlibs.utils.UIUtils;

public class LoadMoreAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mItemView;
    private List<T> mDataList;
    private BindViewByData<T> mBindViewByData;
    //    private boolean mIsLoading = false;
    private boolean mIsMoreDataAvailable = false;
    private OnLoadMoreListener mOnLoadMoreListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public LoadMoreAdapter(int itemView, BindViewByData<T> bindViewByData) {
        mItemView = itemView;
        mBindViewByData = bindViewByData;
        mDataList = new ArrayList<>();
    }

    /**
     * 逐步废弃  不推荐使用 不推荐使用 不推荐使用 不推荐使用
     */
    public LoadMoreAdapter(int itemView, List<T> dataList, BindViewByData<T> bindViewByData) {
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
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            T data = mDataList.get(position);
            //绑定数据到项目视图
            ((ItemViewHolder) holder).bindData(data, position);
        } else if (mDataList.size() > 0) {
            mOnLoadMoreListener.onLoadMore();
        }
//        if (position < mDataList.size() && holder instanceof ItemViewHolder) {
//            T data = mDataList.get(position);
//            //绑定数据到项目视图
//            ((ItemViewHolder) holder).bindData(data, position);
//        } else if (holder instanceof LoadingViewHolder && mOnLoadMoreListener != null) {
//            //显示加载进度条或旋转器
//            //你可以在这里自定义加载布局
//            if (!holder.itemView.canScrollVertically(1)) {
//                mIsMoreDataAvailable = false;
//            } else {
//                mOnLoadMoreListener.onLoadMore();
//            }
//        }
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
        mIsMoreDataAvailable = false;
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * @param moreDataAvailable 是否显示上拉加载
     */
    public void setMoreDataAvailable(boolean moreDataAvailable) {
        mIsMoreDataAvailable = moreDataAvailable;
    }

    /**
     * @param datas             新增数据
     * @param moreDataAvailable 加载的数量是否小于分页数量
     * @deprecated
     */
    public void addDatas(List<T> datas, boolean moreDataAvailable) {
        if (datas == null) return;
        setMoreDataAvailable(moreDataAvailable);
        mDataList.addAll(datas);
        notifyItemRangeInserted(mDataList.size(), datas.size());
    }

    /**
     * @param pageSize 分页数量:用于判断是否还能进行加载,当加载的数量小于分页数量,隐藏底部上拉加载布局
     */
    public void addDatas(List<T> datas, int pageSize) {
        if (datas == null) return;
        if (datas.size() < pageSize) {
            setMoreDataAvailable(false);
        }
        mDataList.addAll(datas);
        notifyItemRangeInserted(mDataList.size(), datas.size());
    }

    public void setDatas(List<T> dataList, int pageSize) {
        setRefreshingComplete();
        if (dataList == null) return;
        mDataList.clear();
        mDataList.addAll(dataList);
        setMoreDataAvailable(dataList.size() < pageSize);
        notifyDataSetChanged();
//        notifyItemRangeInserted(0, dataList.size());
    }

//    /** 暂时不用
//     * @return 数据是否已经占满全屏
//     */
//    public boolean isFullScreen() {
//        // Your code here
//        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
////        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
//        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
//        return lastVisiblePosition >= getItemCount() - 1;
//    }

    public T getItem(int pos) {
        return mDataList.get(pos);
    }

    public List<T> getDataList() {
        return mDataList;
    }

    //    /**
//     * 可能会有加载数量太少导致 item不满一屏时显示上拉加载视图
//     */
//    public void loadingComplete() {
//        setMoreDataAvailable(false);
//        notifyDataSetChanged();
//    }

//    /**
//     * @param moreDataAvailable 传加载的数量是否小于加载一页的数量
//     */
//    public void loadingComplete(boolean moreDataAvailable) {
//        setMoreDataAvailable(moreDataAvailable);
//        notifyDataSetChanged();
//    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface BindViewByData<T> {
//        void bindView();

//        void bindView(View itemView, T data);

        void bindView(BaseHolder holder, View itemView, T data, int postion);
    }

    public static class ItemViewHolder<T> extends BaseHolder {
        private View mItemView;
        private BindViewByData<T> mBindViewByData;

        public ItemViewHolder(@NonNull View itemView, BindViewByData<T> bindViewByData) {
            super(itemView);
            mItemView = itemView;
            // 在这里初始化项视图
            mBindViewByData = bindViewByData;
        }

        public void bindData(T data, int position) {
            mBindViewByData.bindView(this, mItemView, data, position);
            //将数据绑定到item视图
        }
    }

    private static class LoadingViewHolder extends BaseHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            // 在这里初始化加载视图
        }
    }

    public static class BaseHolder extends RecyclerView.ViewHolder {
        private View mItemView;
        private SparseArray<View> mViews;

        public BaseHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mViews = new SparseArray<>();
        }


        private View getItemRootView() {
            return mItemView;
        }

        private <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mItemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public BaseHolder setText(int id, String text) {
            ((TextView) getView(id)).setText(text);
            return this;
        }

        public BaseHolder setText(int id, int textId) {
            ((TextView) getView(id)).setText(UIUtils.getString(textId));
            return this;
        }

        public BaseHolder setTextColor(int id, int textColorId) {
            ((TextView) getView(id)).setTextColor(UIUtils.getColor(textColorId));
            return this;
        }

        public BaseHolder setTextSize(int id, int textSize) {
            ((TextView) getView(id)).setTextSize(textSize);
            return this;
        }

        public BaseHolder setImageResource(int id, @DrawableRes int drawable) {
            ((ImageView) getView(id)).setImageResource(drawable);
            return this;
        }

        public BaseHolder setBackground(int id, @DrawableRes int drawableId) {
            getView(id).setBackground(UIUtils.getDrawable(drawableId));
            return this;
        }

        public BaseHolder setBackgroundColor(int id, @DrawableRes int color) {
            getView(id).setBackgroundColor(color);
            return this;
        }

        public BaseHolder setBackgroundResource(int id, @DrawableRes int bgRes) {
            getView(id).setBackgroundResource(bgRes);
            return this;
        }

        public BaseHolder setVisibility(int id, boolean isVisibility) {
            getView(id).setVisibility(isVisibility ? View.VISIBLE : View.GONE);
            return this;
        }

        public BaseHolder setChecked(int id, boolean isVisibility) {
            ((CheckBox) getView(id)).setChecked(isVisibility);
            return this;
        }

        public BaseHolder setProgressMax(int id, int maxVal) {
            ((ProgressBar) getView(id)).setMax(maxVal);
            return this;
        }

        public BaseHolder setProgress(int id, int maxVal) {
            ((ProgressBar) getView(id)).setProgress(maxVal);
            return this;
        }

        public BaseHolder setOnItemClickListener(int id, View.OnClickListener onClickListener) {
            mItemView.setOnClickListener(onClickListener);
            return this;
        }

        public BaseHolder setOnClickListener(int id, View.OnClickListener onClickListener) {
            getView(id).setOnClickListener(onClickListener);
            return this;
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
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
//        setMoreDataAvailable(true);
//        notifyDataSetChanged();
    }

    public void setRefreshing(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

}
