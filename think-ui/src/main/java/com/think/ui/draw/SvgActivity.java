package com.think.ui.draw;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.think.ui.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SvgActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream open = getAssets().open("o.svg");
                    List<SvgBean> parse = Parser.parse(open);
                    ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                    ViewGroup viewById = decorView.findViewById(android.R.id.content);
                    if( viewById != null){
                        viewById.addView(new DrawView(SvgActivity.this, parse));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}