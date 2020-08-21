package com.think.core.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

public class ViewUtils {

    /**
     * 设置输入框可获取焦点
     *
     * @param editText 编辑框
     */
    public static void setFocusable(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
    }

    /**
     * 设置输入框可获取焦点
     *
     * @param editTexts 多个编辑框
     */
    public static void setFocusable(EditText... editTexts) {
        for (final EditText editText : editTexts) {
            setFocusable(editText);
        }
    }

    public static void setTextFilter(EditText editText, int length) {
        editText.setFilters(new InputFilter[]{new InputFilter() { //过滤空格
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) return "";
                else return null;
            }
        }, new InputFilter.LengthFilter(length)});
    }

    public static void clearInput(EditText input) {
        input.setText("");
    }


    public static void checkViewAndSetEvent(View view, View.OnClickListener onClickListener) {
        if (view != null) {
            view.setOnClickListener(onClickListener);
        }
    }

    public static int checkViewAnRetViewId(View view) {
        return view == null ? 0 : view.getId();
    }


    public void injectOnClickListener(View.OnClickListener listener, View... views) {
        if (views != null) {
            for (View view : views) {
                view.setOnClickListener(listener);
            }
        }
    }
}
