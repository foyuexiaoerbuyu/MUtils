package cn.mvp.dialog;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.hjq.toast.ToastUtils;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/05/18
 * desc   : 项目中的 Dialog 基类
 */
public final class MyDialogFragment {

    public static class Builder<B extends MyDialogFragment.Builder>
            extends BaseDialogFragment.Builder<B> {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public B setContentView(@NonNull View view) {
            return super.setContentView(view);
        }

        /**
         * 显示吐司
         */
        public void toast(CharSequence text) {
            ToastUtils.show(text);
        }

        public void toast(@StringRes int id) {
            ToastUtils.show(id);
        }

        public void toast(Object object) {
            ToastUtils.show(object);
        }
    }
}