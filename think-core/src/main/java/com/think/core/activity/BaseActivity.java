package com.think.core.activity;

import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.think.core.util.ScreenUtils;

public class BaseActivity extends AppCompatActivity {


    /**
     * 实现点击键盘外空白位置关闭软键盘显示
     *
     * @param ev 事件
     * @return true
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                if (ScreenUtils.isHideKeyBoard(view, ev)) {
                    ScreenUtils.closeKeyboard(this);
                }
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
