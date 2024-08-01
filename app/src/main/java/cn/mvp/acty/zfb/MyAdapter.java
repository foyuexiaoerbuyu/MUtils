//package cn.mvp.acty.zfb;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.time.Month;
//import java.util.List;
//
//import cn.mvp.R;
//
//public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int VIEW_TYPE_YEAR = 0;
//    private static final int VIEW_TYPE_MONTH = 1;
//
//    private List<Month> months;
//
//    // 构造函数
//    public MyAdapter(List<Month> months) {
//        this.months = months;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0 || months.get(position).getYear() != months.get(position - 1).getYear()) {
//            return VIEW_TYPE_YEAR;
//        } else {
//            return VIEW_TYPE_MONTH;
//        }
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//
//        if (viewType == VIEW_TYPE_YEAR) {
//            View yearView = inflater.inflate(R.layout.item_year, parent, false);
//            return new YearViewHolder(yearView);
//        } else {
//            View monthView = inflater.inflate(R.layout.item_month, parent, false);
//            return new MonthViewHolder(monthView);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Month month = months.get(position);
//
//        if (holder instanceof YearViewHolder) {
//            YearViewHolder yearViewHolder = (YearViewHolder) holder;
//            yearViewHolder.yearTextView.setText(String.valueOf(month.getYear()));
//        } else if (holder instanceof MonthViewHolder) {
//            MonthViewHolder monthViewHolder = (MonthViewHolder) holder;
//            monthViewHolder.monthTextView.setText(String.valueOf(month.getMonth()));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return months.size();
//    }
//
//    // 年份ViewHolder
//    private static class YearViewHolder extends RecyclerView.ViewHolder {
//        TextView yearTextView;
//
//        YearViewHolder(View itemView) {
//            super(itemView);
//            yearTextView = itemView.findViewById(R.id.year_text_view);
//        }
//    }
//
//    // 月份ViewHolder
//    private static class MonthViewHolder extends RecyclerView.ViewHolder {
//        TextView monthTextView;
//
//        MonthViewHolder(View itemView) {
//            super(itemView);
//            monthTextView = itemView.findViewById(R.id.month_text_view);
//        }
//    }
//}
