package com.think.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import java.util.List;

public class BorderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int w = intent.getIntExtra("W",0);
        int h = intent.getIntExtra("H",0);
        List<Rect> list = intent.getParcelableArrayListExtra("list");
        setContentView(new BorderView(this,w,h,list));
    }
}