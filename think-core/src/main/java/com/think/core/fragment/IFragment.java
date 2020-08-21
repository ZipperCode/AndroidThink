package com.think.core.fragment;

/**
 * 适配器模式，适配新旧的Fragment
 * @author zzp
 * data: 2020-8-20
 */
public interface IFragment {

    void setTagName(String tag);

    /**
     * 获取TagName
     * @return 窗口标志
     */
    String getTagName();

    /**
     * 将显示的动作交由自身去实现
     */
    void show();

    /**
     * 将隐藏交由自身实现
     */
    void hide();

    /**
     * 做某些资源的回收
     */
    void dispose();
}
