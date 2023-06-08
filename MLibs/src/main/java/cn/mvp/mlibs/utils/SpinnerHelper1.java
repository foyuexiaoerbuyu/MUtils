package cn.mvp.mlibs.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class SpinnerHelper1<T> {

    private Spinner spinner;
    private ArrayAdapter<T> adapter;
    private OnItemSelectedListener<T> onItemSelectedListener;

    public interface OnItemSelectedListener<T> {
        void onItemSelected(int position, T item);
    }

    public interface DisplayTextProvider<T> {
        String getDisplayText(T item);
    }

    public SpinnerHelper1(Spinner spinner, Context context, DisplayTextProvider<T> displayTextProvider) {
        this.spinner = spinner;
        adapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(displayTextProvider.getDisplayText(getItem(position)));
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(displayTextProvider.getDisplayText(getItem(position)));
                return view;
            }
        };
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

    public void setItems(List<T> items) {
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public int getSelectedPosition() {
        return spinner.getSelectedItemPosition();
    }

    public T getSelectedItem() {
        return adapter.getItem(getSelectedPosition());
    }
}