package cn.mvp.acty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cn.mvp.R;
import cn.mvp.adapter.TimeMangeAdapter;
import cn.mvp.db.DbHelp;
import cn.mvp.db.TodosDao;
import cn.mvp.test.TestData;

/**
 * 任务管理器Activity(没做完)
 */
public class TimeMangeActivity extends AppCompatActivity {


    private RecyclerView mTmRv;
    private TimeMangeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_mange);
        initView();
        initData();
    }

    private void initData() {
        TodosDao todosDao = DbHelp.getDaoSession().getTodosDao();
//        Log.i(todosDao.toString());
//        mAdapter.addAll(todos);
        mAdapter = new TimeMangeAdapter(TimeMangeActivity.this, TestData.getData(), R.layout.time_mage_rv_item);
        mTmRv.setLayoutManager(new LinearLayoutManager(TimeMangeActivity.this));
        mTmRv.setAdapter(mAdapter);
    }

    private void initView() {
        mTmRv = findViewById(R.id.tm_recycler_view);

    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, TimeMangeActivity.class);
        context.startActivity(intent);
    }
}