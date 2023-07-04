package cn.mvp.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.mvp.R;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.UIUtils;
import cn.mvp.mlibs.weight.LoadMoreAdapter;

public class TestActivity extends AppCompatActivity {

    public static void open(Context context) {
        Intent starter = new Intent(context, TestActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initData();
        initView();

    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyAdapter1 adapter;
    private List<String> dataList;

    private void initData() {
        dataList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dataList.add("Item " + i);
        }
    }

    private void initView() {
//        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new MyAdapter1(dataList);
//        recyclerView.setAdapter(adapter);
        LoadMoreAdapter<String> adapter1 = new LoadMoreAdapter<>(R.layout.list_item, dataList, new LoadMoreAdapter.BindViewByData<String>() {


            @Override
            public void bindView(LoadMoreAdapter.BaseHolder holder, View itemView, String data, int postion) {
                holder.setText(android.R.id.text1, data);
//                ((TextView) itemView.findViewById(android.R.id.text1)).setText(data);
            }
        });
        recyclerView.setAdapter(adapter1);
        adapter1.addSwipeRefreshToRecyclerView(recyclerView, new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UIUtils.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter1.setRefreshingComplete();
                    }
                }, 1000);
            }
        });
        adapter1.setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                UIUtils.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("调试信息", "run:  -------");
                        if (dataList.size() < 30) {
                            List<String> dataList = new ArrayList<>();
                            for (int i = 0; i < 4; i++) {
                                dataList.add(DateUtil.formatDate(DateUtil.REGEX_DATE_TIME) + " " + i);
                            }
                            adapter1.addDatas(dataList, 10);
//                            adapter1.notifyDataSetChanged();
                        }
//                        adapter1.setLoading(false);
//                        adapter1.setLoading(false);
//                        adapter1.notifyItemRangeInserted(startPosition, newDataList.size());
                    }
                }, 1000);
            }
        });

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // 下拉刷新
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 更新数据
//                        initData();
//                        adapter1.notifyDataSetChanged();
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 2000);
//            }
//        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//                int itemCount = layoutManager.getItemCount();
//                if (lastVisibleItemPosition >= itemCount - 1 && dy > 0) {
//                    // 上拉加载
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 添加数据
//                            int start = dataList.size();
//                            for (int i = start; i < start + 10; i++) {
//                                dataList.add("Item " + i);
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//                    }, 2000);
//                }
//            }
//        });
    }
}