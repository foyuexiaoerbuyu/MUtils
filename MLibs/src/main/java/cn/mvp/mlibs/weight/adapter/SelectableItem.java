package cn.mvp.mlibs.weight.adapter;

public interface SelectableItem {
    String getTitle();

    /** 设置是否选中 */
    boolean isSelected();

    /** 设置是否选中 */
    void setSelected(boolean selected);

    /** 是否允许多选 */
    boolean isMultiSelectEnabled();

    void setMultiSelectEnabled(boolean enabled);
}