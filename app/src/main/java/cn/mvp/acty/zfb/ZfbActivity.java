package cn.mvp.acty.zfb;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.mvp.R;
import cn.mvp.acty.BaseActivity;
import cn.mvp.mlibs.other.TestUtils;
import cn.mvp.mlibs.utils.DateUtil;
import cn.mvp.mlibs.utils.StatusBarUtil;
import cn.mvp.mlibs.utils.UIUtils;
import cn.mvp.mlibs.weight.NoScrollingRecyclerView;
import cn.mvp.mlibs.weight.adapter.MultiItemTypeAdapter;
import cn.mvp.mlibs.weight.adapter.base.ItemViewDelegate;
import cn.mvp.mlibs.weight.adapter.base.ViewHolder;
import cn.mvp.mlibs.weight.dialog.InputAlertDialog;

/**
 * 支付宝花呗还款界面
 */
public class ZfbActivity extends BaseActivity {
    @BindView(R.id.zfb_tv_all_money)
    AppCompatTextView zfb_tv_all_money;

    @BindView(R.id.zfb_tv_title)
    AppCompatTextView zfb_tv_title;

    @BindView(R.id.zfb_tv_wrzje)
    AppCompatTextView zfb_tv_wrzje;

    @BindView(R.id.zfb_refresh)
    NoScrollingRecyclerView mrv;

    @BindView(R.id.zfb_tv_wrz)
    AppCompatTextView zfb_tv_wrz;
    @BindView(R.id.zfb_img)
    AppCompatImageView zfb_img;


    ArrayList<ZfbBen> zfbBens = new ArrayList<>();
    private MultiItemTypeAdapter<ZfbBen> mAdapter;

    public static void open(Context context) {
        Intent starter = new Intent(context, ZfbActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initView() {
        StatusBarUtil.setColor(this, UIUtils.getColor(R.color.gray));
    }

    @Override
    public void initData() {
        double all = 20583.26 + TestUtils.getRandomNum(123, 999);
        zfb_tv_all_money.setText(all + "");
//        zfb_tv_wrz.setText();
        Glide.with(this).load(R.drawable.zfb_1).into(zfb_img);
        SpannableString ss = new SpannableString("0.00\n淘宝/天猫交易 (除天猫国际、买就返业务外) ，认收货后才会计入账单  ");
        int len = ss.length();
        //图片
        Drawable d = ContextCompat.getDrawable(this, (R.drawable.icon_zfb_jg));
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        //构建ImageSpan
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(span, len - 1, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        zfb_tv_wrzje.setText(ss);
        Calendar calendar = Calendar.getInstance();
        String currYear = DateUtil.formatDate("yyyy");
        zfbBens.add(new ZfbBen(currYear, null, 0));
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            int futureYear = calendar.get(Calendar.YEAR);
            int futureMonth = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以需要加1
            ZfbBen zfbBen = new ZfbBen(futureYear + "", futureMonth + "", all / 12);
            zfbBens.add(zfbBen);
        }
        mAdapter = new MultiItemTypeAdapter<>(this, zfbBens);
        mAdapter.addItemViewDelegate(new ItemViewDelegate<ZfbBen>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_zfb_title;
            }

            @Override
            public boolean isForViewType(ZfbBen item, int position) {
                return position == 0 || !item.getYear().equals(zfbBens.get(position - 1).getYear());
            }

            @Override
            public void convert(ViewHolder holder, ZfbBen zfbBen, int position) {
                holder.setText(R.id.item_zfb_com_nf, zfbBen.getYear());
            }
        });
        mAdapter.addItemViewDelegate(new ItemViewDelegate<ZfbBen>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_zfb_com;
            }

            @Override
            public boolean isForViewType(ZfbBen item, int position) {
                return position != 0 && item.getYear().equals(zfbBens.get(position - 1).getYear());
            }

            @Override
            public void convert(ViewHolder holder, ZfbBen zfbBen, int position) {
                holder.setText(R.id.item_zfb_tv_dhyf, zfbBen.getMonth() + "月待还");

                holder.setText(R.id.item_zfb_tv_dhxx, "剩余待还" + String.format("%.2f", zfbBen.getMonthlyDue()) + "元，还款日" + zfbBen.getMonth() + "月08日");
            }
        });
        mrv.setLayoutManager(new LinearLayoutManager(this));
        mrv.setAdapter(mAdapter);
    }

    @Override
    public int setView() {
        return R.layout.activity_zfb;
    }

    @OnClick({R.id.zfb_tv_all_money, R.id.zfb_tv_title, R.id.zfb_tv_wrzje, R.id.zfb_tv_wrz})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.zfb_tv_all_money:
                break;
            case R.id.zfb_tv_title:
                new InputAlertDialog(this).setEditText("22583.26")
                        .setOkClick(new InputAlertDialog.OnOkClickListener() {
                            @Override
                            public void click(String inputStr) {
                                zfbBens.clear();
                                zfb_tv_all_money.setText(inputStr);
                                int paymentPeriod = 6;
                                double oneMonth = Double.parseDouble(inputStr) / paymentPeriod;
                                Calendar calendar = Calendar.getInstance();
                                String currYear = DateUtil.formatDate("yyyy");
                                zfbBens.add(new ZfbBen(currYear, null, 0));
                                for (int i = 0; i < paymentPeriod; i++) {
                                    calendar.add(Calendar.MONTH, 1);
                                    int futureYear = calendar.get(Calendar.YEAR);
                                    int futureMonth = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以需要加1
                                    ZfbBen zfbBen = new ZfbBen(futureYear + "", futureMonth + "", oneMonth);
                                    zfbBens.add(zfbBen);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }).show();
                break;
            case R.id.zfb_tv_wrzje:
                break;
            case R.id.zfb_tv_wrz:
                break;
            default:
        }
    }

//    @Override
//    public void onBackPressed() {
//        zfb_img.setVisibility(TestUtils.getBoole() ? View.VISIBLE : View.GONE);
//    }
}