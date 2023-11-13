package cn.mvp.test;

import cn.mvp.mlibs.weight.adapter.SelectableItem;

public class ItemModel implements SelectableItem {
    private String title;
    private boolean isSelected;
    private boolean isMultiSelectEnabled;

    public ItemModel(String title) {
        this.title = title;
        this.isSelected = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean isMultiSelectEnabled() {
        return isMultiSelectEnabled;
    }

    @Override
    public void setMultiSelectEnabled(boolean enabled) {
        isMultiSelectEnabled = enabled;
    }
}