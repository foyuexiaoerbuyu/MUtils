package cn.mvp.adapter;

import android.content.Context;

import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;

import cn.mvp.R;
import cn.mvp.db.Todos;
import cn.mvp.mlibs.utils.UIUtils;

/**
 * 时间管理适配器
 */
public class TimeMangeAdapter extends EasyRVAdapter<Todos> {

    public TimeMangeAdapter(Context context, List<Todos> list, int... layoutIds) {
        super(context, list, layoutIds);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, Todos item) {
        viewHolder.setText(R.id.time_mage_rv_item_title, item.getMatterTitle());
        if (item.getImportantLevel() == 1) {
            viewHolder.getView(R.id.time_mage_rv_item_tv_important_level).setBackgroundColor(UIUtils.getColor(R.color.DeepRed));
        } else if (item.getImportantLevel() == 2) {
            viewHolder.getView(R.id.time_mage_rv_item_tv_important_level).setBackgroundColor(UIUtils.getColor(R.color.DeepOrange));
        } else if (item.getImportantLevel() == 3) {
            viewHolder.getView(R.id.time_mage_rv_item_tv_important_level).setBackgroundColor(UIUtils.getColor(R.color.DeepBlue));
        } else {
            viewHolder.getView(R.id.time_mage_rv_item_tv_important_level).setBackgroundColor(UIUtils.getColor(R.color.DeepGreen));
        }
    }
}
