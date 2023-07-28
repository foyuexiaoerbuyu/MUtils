package cn.mvp.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.mvp.R;
import cn.mvp.mlibs.other.DataTestUtils;
import cn.mvp.mlibs.weight.LoadMoreAdapter;

public class TestActivity extends AppCompatActivity {

    private LoadMoreAdapter<DataTestUtils.DataTestUser> mAdapter;

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

    private List<String> mDataList;

    private void initData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            mDataList.add("Item " + i);
        }
    }

    private void initView() {
//        CommonAdapter<String> adapter = new CommonAdapter<String>(this, R.layout.list_item, mDataList) {
//            @Override
//            protected void convert(ViewHolder holder, String s, int position) {
//                holder.setText(android.R.id.text1, s);
//            }
//        };

        ArrayList<DataTestUtils.DataTestUser> dataUsers = DataTestUtils.getDataUsers(15);
        mAdapter = new LoadMoreAdapter<>(R.layout.list_item, new LoadMoreAdapter.BindViewByData<DataTestUtils.DataTestUser>() {
            @Override
            public void bindView(LoadMoreAdapter.BaseHolder holder, View itemView, DataTestUtils.DataTestUser data, int postion) {
                holder.setText(android.R.id.text1, data.getUserName());
            }
        });
        mAdapter.setOnLoadMoreListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        dataUsers.addAll(DataTestUtils.getDataUsers(12));
//                        mAdapter.setMoreDataAvailable(false);
                        mAdapter.addDatas(DataTestUtils.getDataUsers(3), 2);
//                        mAdapter.notifyDataSetChanged();
//                        mAdapter.setMoreDataAvailable(false);
//                        mAdapter.notifyDataSetChanged();
                    }
                }, 300);
            }
        });

        RecyclerView refresh = findViewById(R.id.refresh);
        refresh.setLayoutManager(new LinearLayoutManager(this));
        refresh.setAdapter(mAdapter);
        mAdapter.addSwipeRefreshToRecyclerView(refresh, new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.setDatas(DataTestUtils.getDataUsers(13), 22);
            }
        });

        mAdapter.setDatas(dataUsers, 40);
    }
}