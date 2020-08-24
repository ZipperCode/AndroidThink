package com.think.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.think.ui.ParticleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ParticleView(this));
    }
}
