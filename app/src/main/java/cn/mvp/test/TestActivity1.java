package cn.mvp.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.mvp.R;

public class TestActivity1 extends AppCompatActivity {

    public static void open(Context context) {
        Intent starter = new Intent(context, TestActivity1.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

    }
}