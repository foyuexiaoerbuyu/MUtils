//package cn.mvp.test;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import cn.mvp.R;
//
//public class LoadMoreWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private static final int ITEM_VIEW_TYPE_CONTENT = 0;
//    private static final int ITEM_VIEW_TYPE_LOAD_MORE = 1;
//
//    private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
//    private boolean mIsLoading = false;
//    private OnLoadMoreListener mOnLoadMoreListener;
//
//    public LoadMoreWrapperAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, OnLoadMoreListener onLoadMoreListener) {
//        mAdapter = adapter;
//        mOnLoadMoreListener = onLoadMoreListener;
//    }
//
//    @Override
//    public int getItemCount() {
//        // 如果正在加载，则底部加载进度条视为一个item
//        return mIsLoading ? mAdapter.getItemCount() + 1 : mAdapter.getItemCount();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // 根据位置判断当前item的类型
//        if (position == mAdapter.getItemCount()) {
//            return ITEM_VIEW_TYPE_LOAD_MORE; // 底部加载进度条类型
//        } else {
//            return ITEM_VIEW_TYPE_CONTENT; // 内容item类型
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // 创建ViewHolder
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        if (viewType == ITEM_VIEW_TYPE_LOAD_MORE) {
//            // 加载底部进度条布局
//            View itemView = inflater.inflate(R.layout.default_loading, parent, false);
//            return new LoadMoreViewHolder(itemView);
//        } else {
//            // 调用原始adapter的onCreateViewHolder方法创建内容item的ViewHolder
//            return mAdapter.onCreateViewHolder(parent, viewType);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        // 绑定数据
//        if (holder instanceof LoadMoreViewHolder) {
//            // 如果是底部加载进度条ViewHolder，根据状态显示不同的文本
//            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
//            if (mIsLoading) {
//                loadMoreViewHolder.progressBar.setVisibility(View.VISIBLE);
//                loadMoreViewHolder.textView.setText("正在加载...");
//            } else {
//                loadMoreViewHolder.progressBar.setVisibility(View.GONE);
//                loadMoreViewHolder.textView.setText("点击加载更多");
//            }
//        } else {
//            // 调用原始adapter的onBindViewHolder方法绑定内容item的数据
//            mAdapter.onBindViewHolder(holder, position);
//        }
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//
//        // 监听RecyclerView的滚动事件
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                // 判断是否滚动到了最后一项可见item，并且当前没有正在加载
//                if (!mIsLoading && isLastItemVisible(recyclerView)) {
//                    // 触发加载更多回调
//                    if (mOnLoadMoreListener != null) {
//                        mOnLoadMoreListener.onLoadMore();
//                    }
//                }
//            }
//        });
//    }
//
//    private boolean isLastItemVisible(RecyclerView recyclerView) {
//        // 判断是否滚动到了最后一项可见item
//        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
//        int itemCount = mAdapter.getItemCount();
//        return lastVisiblePosition >= itemCount - 1;
//    }
//
//    public void setLoading(boolean isLoading) {
//        // 设置加载状态
//        mIsLoading = isLoading;
//        notifyDataSetChanged();
//    }
//
//    private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
//        ProgressBar progressBar;
//        TextView textView;
//
//        LoadMoreViewHolder(View itemView) {
//            super(itemView);
//            progressBar = itemView.findViewById(R.id.default_loading_progress_bar);
//            textView = itemView.findViewById(R.id.default_loading_loading_text);
//        }
//    }
//
//    public interface OnLoadMoreListener {
//        void onLoadMore();
//    }
//}
