package com.think.core.dialog;

/**
 * @author : zzp
 * @date : 2020/9/3
 **/
public interface IDialog {

    void initView();

    void showing();

    void hiding();

    void dismissing();

    void setTagName(String tag);

    String getTagName();
}
