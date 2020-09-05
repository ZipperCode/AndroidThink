package com.think.core.dialog;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import com.think.core.fragment.IFragment;

import java.lang.reflect.Constructor;
import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * 实现将{@link android.app.Fragment} 和
 * {@link androidx.fragment.app.Fragment} 适配管理
 * 处理Dialog之间的页面路由
 * @author : zzp
 *  date : 2020/8/11
 **/
public class DialogManager {

    /**
     * 保存实现IFragment的实现类
     */
    private Deque<IDialog> mDialogDeque = new LinkedList<>();
    /**
     * 保存IFragment子类的Tag，实现回退到栈中的某个窗口
     */
    private Deque<String> mDialogTag = new LinkedList<>();

    /**
     * 将一个实现了IFragment的Fragment添加进栈中
     * @param iDialog IFragment以及其子类
     */
    public void push(IDialog iDialog) {
        push(iDialog,false);
    }

    /**
     * 将一个实现了IFragment的Fragment添加进栈中
     * @param iDialog IFragment以及其子类
     * @param isHiding 是否隐藏，true为隐藏，false为销毁
     */
    public void push(IDialog iDialog,boolean isHiding) {
        if (!mDialogDeque.isEmpty()) {
            // 添加一个，将上一个从activity中移除，栈中保留
            IDialog peek = mDialogDeque.peek();
            if(peek != null){
                if(isHiding){
                    peek.hiding();
                }else{
                    peek.dismissing();
                }
            }
        }
        iDialog.showing();
        mDialogDeque.push(iDialog);
        mDialogTag.push(iDialog.getTagName());
    }


    /**
     * 将旧版{@link android.app.Fragment} 实现了{@link IDialog}
     * 的窗口添加到栈中
     * @param activity Act上下文
     * @param tClass 子类的类型
     * @param <T> IFragment 子类泛型
     */
    public <T extends IDialog> void push(Activity activity, Class<T> tClass) {
        try {
            Constructor<T> noArgsConstructor = tClass.getConstructor(Activity.class);
            T iDialog = noArgsConstructor.newInstance(activity);
            push(iDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将新版{@link androidx.fragment.app.Fragment} 实现了{@link IDialog}
     * 的窗口添加到栈中
     * @param activity 实现了{@link FragmentActivity} 的Activity
     * @param tClass  子类的类型
     * @param <T> IFragment 子类泛型
     */
    public <T extends IDialog> void push(FragmentActivity activity, Class<T> tClass) {
        try {
            Constructor<T> noArgsConstructor = tClass.getConstructor(FragmentActivity.class);
            T iDialog = noArgsConstructor.newInstance(activity);
            push(iDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示栈顶的IFragment
     */
    public void showTop(){
        if(mDialogDeque.isEmpty()){
           return;
        }
        IDialog iDialog = mDialogDeque.peek();
        if(iDialog != null){
            iDialog.showing();
        }
    }

    /**
     * 将栈顶中的窗口销毁
     */
    public void pop() {
        if (!mDialogDeque.isEmpty()) {
            IDialog iDialog = mDialogDeque.pop();
            iDialog.dismissing();
            mDialogTag.pop();
        }
    }

    /**
     * 将栈顶中的窗口销毁，并显示下一个栈顶的窗口
     */
    public void popTo() {
        if (!mDialogDeque.isEmpty()) {
            mDialogTag.pop();
            IDialog iDialog = mDialogDeque.pop();
            iDialog.dismissing();
            if (!mDialogDeque.isEmpty()) {
                IDialog peek = mDialogDeque.peek();
                if(peek != null){
                    peek.showing();
                }
            }
        }
    }

    /**
     * 依次将栈中的窗口弹出并销毁
     */
    public void popToEmpty(){
        while (!mDialogDeque.isEmpty()){
            mDialogTag.pop();
            IDialog iDialog = mDialogDeque.pop();
            if(iDialog !=  null){
                iDialog.dismissing();
            }
        }
    }

    /**
     * 将栈中的窗口回退到某一个tag标记的窗口中，并显示
     * @param tag 标志
     */
    public void popTo(String tag) {
        System.out.println("popTp : 之前 =>" + mDialogTag.toString());
        if (!mDialogTag.isEmpty()) {
            pop();
            String pushTag = null;
            while ((pushTag = mDialogTag.peek()) != null) {
                if (tag.equals(pushTag)) {
                    IDialog iDialog = mDialogDeque.peek();
                    if(iDialog != null){
                        iDialog.showing();
                        return;
                    }
                } else {
                    pop();
                }
            }
        }
    }

    private static class InnerInstance {
        private static final DialogManager INSTANCE = new DialogManager();
    }

    public static DialogManager getInstance() {
        return InnerInstance.INSTANCE;
    }
}
