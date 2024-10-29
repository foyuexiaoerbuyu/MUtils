package cn.mvp.mlibs.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 单选或多选适配器
 *
 * @param <T> 传入实体类必须实现 SelectableItem接口
 *            使用方式:
 *            <code>
 *            //           List<ItemModel> itemList = new ArrayList<>();
 *            //         itemList.add(new ItemModel("Item 1"));
 *            //         itemList.add(new ItemModel("Item 2"));
 *            //         itemList.add(new ItemModel("Item 3"));
 *            //         itemList.add(new ItemModel("Item 4"));
 *            //
 *            //         SelectAdapter<ItemModel> adapter = new SelectAdapter<>(this, itemList);
 *            //
 *            //         adapter.addItemViewDelegate(new ItemViewDelegate<ItemModel>() {
 *            //             @Override
 *            //             public int getItemViewLayoutId() {
 *            //                 return R.layout.item_layout;
 *            //             }
 *            //
 *            //             @Override
 *            //             public boolean isForViewType(ItemModel item, int position) {
 *            //                 //什么情况下显示这个布局
 *            //                 return true;
 *            //             }
 *            //
 *            //             @Override
 *            //             public void convert(ViewHolder holder, ItemModel item, int position) {
 *            //                 holder.setText(R.id.tv_title, item.getTitle());
 *            //                 holder.setChecked(R.id.checkbox_item, item.isSelected());
 *            //
 *            //                 holder.getConvertView().setOnClickListener(new View.OnClickListener() {
 *            //                     @Override
 *            //                     public void onClick(View v) {
 *            //                         adapter.toggleSelection(position);
 *            //                     }
 *            //                 });
 *            //             }
 *            //         });
 *            </code>
 */
public class SelectAdapter<T extends SelectableItem> extends MultiItemTypeAdapter<T> {
    public SelectAdapter(Context context, List<T> datas) {
        super(context, datas);
    }

    // 启用多选
    public void enableMultiSelect() {
        for (T item : getDatas()) {
            item.setMultiSelectEnabled(true);
        }
    }

    // 禁用多选
    public void disableMultiSelect() {
        for (T item : getDatas()) {
            item.setMultiSelectEnabled(false);
            item.setSelected(false);
        }
        notifyDataSetChanged();
    }

    // 切换项的选中状态
    public void toggleSelection(int position) {
        T item = getDatas().get(position);
        if (item.isMultiSelectEnabled()) {
            item.setSelected(!item.isSelected());
        } else {
            // Single selection logic
            clearSelection();
            item.setSelected(true);
        }
        notifyDataSetChanged();
    }

    // 获取选中的项的数量
    public int getSelectedItemCount() {
        int count = 0;
        for (T item : getDatas()) {
            if (item.isSelected()) {
                count++;
            }
        }
        return count;
    }

    // 获取选中的项的位置
    public List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (int i = 0; i < getDatas().size(); i++) {
            T item = getDatas().get(i);
            if (item.isSelected()) {
                selectedPositions.add(i);
            }
        }
        return selectedPositions;
    }

    // 全选
    public void selectAll() {
        for (T item : getDatas()) {
            if (item.isMultiSelectEnabled()) {
                item.setSelected(true);
            }
        }
        notifyDataSetChanged();
    }

    // 反选
    public void selectInverse() {
        for (T item : getDatas()) {
            if (item.isMultiSelectEnabled()) {
                item.setSelected(!item.isSelected());
            }
        }
        notifyDataSetChanged();
    }

    // 清除选中的项
    public void clearSelection() {
        for (T item : getDatas()) {
            item.setSelected(false);
        }
        notifyDataSetChanged();
    }

}
