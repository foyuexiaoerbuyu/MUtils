package cn.mvp.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import cn.mvp.R;
import cn.mvp.mlibs.weight.adapter.MultiItemTypeAdapter;
import cn.mvp.mlibs.weight.adapter.SelectAdapter;
import cn.mvp.mlibs.weight.adapter.base.ItemViewDelegate;
import cn.mvp.mlibs.weight.adapter.base.ViewHolder;

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
        RecyclerView recyclerView = findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<ItemModel> itemList = new ArrayList<>();
        itemList.add(new ItemModel("Item 1"));
        itemList.add(new ItemModel("Item 2"));
        itemList.add(new ItemModel("Item 3"));
        itemList.add(new ItemModel("Item 4"));

        SelectAdapter<ItemModel> adapter = new SelectAdapter<>(this, itemList);

        adapter.addItemViewDelegate(new ItemViewDelegate<ItemModel>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_layout;
            }

            @Override
            public boolean isForViewType(ItemModel item, int position) {
                //什么情况下显示这个布局
                return true;
            }

            @Override
            public void convert(ViewHolder holder, ItemModel item, int position) {
                holder.setText(R.id.tv_title, item.getTitle());
                holder.setChecked(R.id.checkbox_item, item.isSelected());

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.toggleSelection(position);
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);
        findViewById(R.id.test_btn_single1).setOnClickListener(v -> {
            //禁用多选
            adapter.disableMultiSelect();
        });
        findViewById(R.id.test_btn_all1).setOnClickListener(v -> {
            // 启用多选
            adapter.enableMultiSelect();
        });

        AppCompatButton test_btn_single = findViewById(R.id.test_btn_single);
        AppCompatButton test_btn_all = findViewById(R.id.test_btn_all);

        test_btn_all.setOnClickListener(v -> {
            adapter.selectAll();
        });

        test_btn_single.setOnClickListener(v -> {
            adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//                    adapter.clearSelection();
                    adapter.toggleSelection(position);
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
                    return false;
                }
            });
        });

        // 全选
        /*
        adapter.selectAll();
        */

        // 反选
        /*
        adapter.selectInverse();
        */

        // 获取选中的项的数量
        /*
        int selectedCount = adapter.getSelectedItemCount();
        */

        // 获取选中的项的位置
        /*
        List<Integer> selectedPositions = adapter.getSelectedPositions();
        */
    }

}