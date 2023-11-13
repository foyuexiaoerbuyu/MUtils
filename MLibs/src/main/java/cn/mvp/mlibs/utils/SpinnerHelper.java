package cn.mvp.mlibs.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class SpinnerHelper {

    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener {
        void onItemSelected(int position, String item);
    }

    public SpinnerHelper(Spinner spinner, Context context) {
        this.spinner = spinner;
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(position, adapter.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setItems(List<String> items) {
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
    public void setDefaultItem(String defaultItem) {
        int position = adapter.getPosition(defaultItem);
        spinner.setSelection(position);
    }

    public int getSelectedPosition() {
        return spinner.getSelectedItemPosition();
    }

    public String getSelectedItem() {
        return adapter.getItem(getSelectedPosition());
    }

}