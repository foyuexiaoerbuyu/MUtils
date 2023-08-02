package cn.mvp.mlibs.weight;

import android.view.View;

public interface ItemWeightClick {
    <T> void onItemWeightClick(View view, T t, int pos);

//    default <T> void onItemWeightClick(View view, T t, int pos) {
//
//    }
}
