package com.think.core.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class InputHelper {

    public static void showKeyboard(Activity activity){
        showKeyboard(activity.getWindow());
    }

    public static void showKeyboard( Window window){
        if(window == null){
            return;
        }
        View focusView = window.getCurrentFocus();
        if(focusView != null){
            requestShow(focusView);
        }else{
            View decorView = window.getDecorView();
            if(decorView != null){
                decorView.requestFocus();
                decorView.requestFocusFromTouch();
                requestShow(decorView);
            }
        }
    }

    public static void hideKeyboard(Activity activity){
        if(activity == null){
            return;
        }
        hideKeyboard(activity.getWindow());
    }

    public static void hideKeyboard(Window window){
        if(window == null){
            return;
        }
        View decorView = window.getDecorView();
        if(decorView == null){
            return;
        }
        View focusView = decorView.findFocus();
        if(focusView == null){
            requestHide(decorView);
        }else{
            requestHide(focusView);
        }
    }

    private static void requestShow(View focusView){
        if(focusView != null){
            ThreadManager.getInstance().postDelayed(() -> {
                Context context = focusView.getContext();
                if(context != null){
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(focusView, 0);
                }
            }, 200L);
        }
    }

    private static void requestHide(View focusView){
        if(focusView != null){
            ThreadManager.getInstance().postDelayed(() -> {
                Context context = focusView.getContext();
                if(context != null){
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                }
            }, 200L);
        }
    }
}
