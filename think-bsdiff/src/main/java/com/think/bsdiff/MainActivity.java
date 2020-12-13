package com.think.bsdiff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView  = findViewById(R.id.textView);
        String ver = "版本号为：" + BuildConfig.VERSION_NAME;
        textView.setText(ver);
    }

    public void update(View view) {
        System.out.println("update");
        BsPatcher.test(this);
    }
}