package com.think.core.mvp.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.think.core.R;
import com.think.core.mvp.BaseActivity;
import com.think.core.mvp.BasePresenter;
import com.think.core.mvp.IModel;
import com.think.core.mvp.IView;

public class MainMvpActivity extends BaseActivity<MainMvpModel,MainMvpView,MainMvpPresenter> implements MainMvpView{

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter();
    }

    private TextView tvRunState;
    private Button btnRunTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mvp);
        tvRunState = findViewById(R.id.tv_run_state);
        btnRunTask = findViewById(R.id.btn_run_task);
        btnRunTask.setOnClickListener((v)->{
            presenter.executeTask();
        });
    }



    @Override
    public void showSuccess() {
        tvRunState.setText("Success!");
    }
}