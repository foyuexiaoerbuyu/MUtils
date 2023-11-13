package cn.mvp.mlibs.other;

import android.view.View;

/**
 * 通用回调方法
 */
public interface ICallBack {
    void back();

    default void back(int pos) {

    }

    default void back(View view, int pos) {

    }
}
