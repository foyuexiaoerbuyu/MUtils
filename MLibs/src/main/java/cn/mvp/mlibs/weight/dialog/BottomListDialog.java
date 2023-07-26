package cn.mvp.mlibs.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.mvp.mlibs.R;
import cn.mvp.mlibs.utils.StringUtil;
import cn.mvp.mlibs.weight.ent.Item;

/**
 * List<Item<Test>> items = new ArrayList<>();
 * items.add(new Item<>("item1", new Test(1, "test1")));
 * items.add(new Item<>("item2", new Test(2, "test2")));
 * items.add(new Item<>("item3", new Test(3, "test3")));
 * <p>
 * BottomListDialog<Test> bottomListDialogUtil = new BottomListDialog<>();
 * bottomListDialogUtil.showBottomListDialog(this, items, new BottomListDialog.OnItemSelectedListener<Test>() {
 *
 * @Override public void onItemSelected(int position, Item<Test> item) {
 * Log.i("调试信息", "onItemSelected:  " + item.getVal().toString() + item.getData().toString());
 * }
 * });
 * 底部弹框
 */
public class BottomListDialog<T> {
    private List<Item<T>> mItemList = null;

    public BottomListDialog addData(String val, T t) {
        if (mItemList == null) {
            mItemList = new ArrayList<>();
        }
        mItemList.add(new Item<>(val, t));
        return this;
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(int position, Item<T> item);
    }

    public void showBottomListDialog(Context context, OnItemSelectedListener<T> listener) {
        showBottomListDialog(context, mItemList, listener);
    }

    public void showBottomListDialog(Context context, List<Item<T>> list, OnItemSelectedListener<T> listener) {
        Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bottom_list);

        Window window = dialog.getWindow();
        if (window != null) {
            // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM;
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // 给 DecorView 设置背景颜色，很重要，不然导致 Dialog 内容显示不全，有一部分内容会充当 padding，上面例子有举出
            window.getDecorView().setBackgroundColor(Color.WHITE);
            window.setAttributes(layoutParams);
        }

        ListView listView = dialog.findViewById(R.id.list_view);
        PersonAdapter adapter = new PersonAdapter(context, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    Item<T> tItem = list.get(position);
                    listener.onItemSelected(position, tItem);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class PersonAdapter extends ArrayAdapter<Item<T>> {
        public PersonAdapter(Context context, List<Item<T>> persons) {
            super(context, 0, persons);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item<T> person = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView nameTextView = convertView.findViewById(android.R.id.text1);
            if (person != null) {
                nameTextView.setText(StringUtil.valueOf(person.getVal()));
            }

            return convertView;
        }
    }
}