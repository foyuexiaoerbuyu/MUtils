package cn.mvp.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.mvp.R;

public class TestActy extends AppCompatActivity {

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, TestActy.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_acty);
    }

}
