package com.think.core.mvp.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.think.core.R;
import com.think.core.mvp.BaseActivity;

public class MainMvpActivity extends BaseActivity<MainMvpModel,MainMvpPresenter> {

    @Override
    protected MainMvpPresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mvp);
    }
}