package com.think.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class VerificationCodeView extends LinearLayout implements TextWatcher, View.OnKeyListener {
    /**
     * 输入类型
     */
    private static final int TYPE_NUMBER = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_PASSWORD = 2;
    /**
     * 默认属性参数 默认数量
     */
    private static final int DEFAULT_CHILD_NUM = 4;
    private static final int DEFAULT_CHILD_WIDTH = 50;
    private static final int DEFAULT_CHILD_HEIGHT = 50;
    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_SPACING = 10;
    private static final int MAX_CHARACTER_LENGTH = 1;

    private static final int DEFAULT_CHILD_PADDING = 10;

    private StringBuilder stringBuilder;
    /**
     * 输入框数量
     */
    private int childNum;
    /**
     * 当前输入框位置
     */
    private int childCurrentIndex;
    /**
     * 输入框的宽度
     */
    private int childWidth;
    /**
     * 输入框高度
     */
    private int childHeight;
    /**
     * 输入框字体颜色
     */
    private int textColor;
    /**
     * 字体大小
     */
    private int textSize;
//    /**
//     * 输入框样式 圆角边框等
//     */
//    private Drawable childStyle;
    /**
     * 是否显示光标
     */
    private boolean isChildCursorVisible;
    /**
     * 输入框输入类型
     */
    private Integer inputType ;
    /**
     * 输入框的间隔
     */
    private int childSpacing ;
    /**
     * 输入框水平的间距
     */
    private int childHorizontalSpacing;
    /**
     * 输入框垂直的间距
     */
    private int childVerticalSpacing;
    /**
     * 输入框获取焦点时的样式
     */
    private Drawable childFocusStyle;
    /**
     * 输入框失去焦点时的样式
     */
    private Drawable childNormalStyle;

    public VerificationCodeView(Context context) {
        this(context,null);
    }

    public VerificationCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VerificationCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerificationCodeView);

        this.childNum = typedArray.getInteger(R.styleable.VerificationCodeView_child_number, DEFAULT_CHILD_NUM);
        this.isChildCursorVisible = typedArray.getBoolean(R.styleable.VerificationCodeView_child_cursor_visible, false);
        this.inputType = typedArray.getInteger(R.styleable.VerificationCodeView_child_inputType, TYPE_NUMBER);
        this.childWidth = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_child_width, DEFAULT_CHILD_WIDTH);
        this.childHeight = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_child_height, DEFAULT_CHILD_HEIGHT);
        this.textColor = typedArray.getColor(R.styleable.VerificationCodeView_child_text_color, Color.BLACK);
        this.textSize = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_child_text_size, DEFAULT_TEXT_SIZE);
//        this.childStyle = typedArray.getDrawable(R.styleable.VerificationCodeView_child_style);
        this.childSpacing = typedArray.getDimensionPixelOffset(R.styleable.VerificationCodeView_child_spacing,DEFAULT_SPACING);
        this.childHorizontalSpacing = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_child_horizontal_spacing,DEFAULT_SPACING);
        this.childVerticalSpacing = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_child_vertical_spacing,DEFAULT_SPACING);
        this.childFocusStyle = typedArray.getDrawable(R.styleable.VerificationCodeView_child_focus_style);
        this.childNormalStyle = typedArray.getDrawable(R.styleable.VerificationCodeView_child_normal_style);
        typedArray.recycle();
        initView();
        this.stringBuilder = new StringBuilder();
    }


    private void initView() {
        for (int i = 0; i <= this.childNum; i++) {
            // 创建editText输入框
            EditText editText = new EditText(getContext());
            initEditView(editText);
            addView(editText);
            if (i == 0) {
                // 第一个获取焦点以及弹出键盘
                editText.setEnabled(true);
                setCursorVisible(editText,true);
                editText.requestFocus();
                editText.setBackground(this.childFocusStyle);
            }
            // 多一个editText 让删除变得更加灵活
            if(i == this.childNum){
                editText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 初始化输入框 设置大小样式事件等
     *
     * @param editText
     */
    private void initEditView(EditText editText) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(childWidth, childHeight);
//        layoutParams.weight = 1;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.height = this.childHeight;
        layoutParams.width = this.childWidth;
        // 默认间距
        layoutParams.bottomMargin = this.childSpacing;
        layoutParams.leftMargin = this.childSpacing;
        layoutParams.topMargin = this.childSpacing;
        layoutParams.rightMargin = this.childSpacing;
        // 如果有设置水平和垂直间距的话
        layoutParams.bottomMargin = this.childVerticalSpacing;
        layoutParams.topMargin = this.childVerticalSpacing;
        layoutParams.leftMargin = this.childHorizontalSpacing;
        layoutParams.rightMargin = this.childHorizontalSpacing;

        editText.setLayoutParams(layoutParams);
        editText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        editText.setGravity(Gravity.CENTER);
        editText.setTextColor(this.textColor);
        editText.setTextSize(this.textSize);
        editText.setCursorVisible(this.isChildCursorVisible);
        // 过滤字符串长度为1
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARACTER_LENGTH)});
        editText.setBackground(this.childNormalStyle);
        editText.setVisibility(View.VISIBLE);
        editText.setPadding(DEFAULT_CHILD_PADDING,DEFAULT_CHILD_PADDING,DEFAULT_CHILD_PADDING,DEFAULT_CHILD_PADDING/2);
        switch (this.inputType) {
            case TYPE_TEXT:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TYPE_PASSWORD:
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case TYPE_NUMBER:
            default:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        // 触摸获取焦点
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(false);
        editText.setOnKeyListener(this);
        editText.addTextChangedListener(this);
    }

    /**
     * 得到前一个EditText的焦点
     */
    private void previousFocus() {
        // 处于第一个的EditText不需要获取焦点
        if (this.childCurrentIndex == 0) {
            EditText firstEditText = (EditText) getChildAt(this.childCurrentIndex);
            firstEditText.setText("");
            // 设置游标
            setCursorVisible(firstEditText,true);
            firstEditText.requestFocus();
            firstEditText.setBackground(this.childFocusStyle);
            return;
        }
        EditText currentEditText = (EditText) getChildAt(this.childCurrentIndex);
        // 当前获取焦点的EditText是否存在值
        if(currentEditText.getText().length() == 0){
            this.childCurrentIndex--;
            // 获取前一个
            EditText previousEditText = (EditText) getChildAt(this.childCurrentIndex);
            previousEditText.setEnabled(true);
            previousEditText.setText("");
            setCursorVisible(previousEditText,true);
            previousEditText.requestFocus();
            previousEditText.setBackground(this.childFocusStyle);
            // 当前输入设置为不可用,设置失去焦点样式
            currentEditText.setEnabled(false);
            currentEditText.setBackground(this.childNormalStyle);
            // 取消光标
            setCursorVisible(currentEditText,false);
            return;
        }
        // 内容置空
        currentEditText.setText("");

    }

    /**
     * 得到下一个输入框的焦点
     */
    private void nextFocus() {
        EditText currentEditText = (EditText) getChildAt(this.childCurrentIndex);
        if (currentEditText.getText().length() < 1) {
            return;
        }
        // 取消光标,失去焦点
        setCursorVisible(currentEditText,false);
        currentEditText.setBackground(this.childNormalStyle);
        // 下一个
        this.childCurrentIndex++;
        if (this.childCurrentIndex >= this.childNum) {
            // 完成输入,回调事件
            onCompleteListener.onComplete(stringBuilder.toString());
            return;
        }

        // 获取下一个光标,设置为可用，添加光标，请求焦点
        EditText nextEditText = (EditText) getChildAt(this.childCurrentIndex);
        nextEditText.setEnabled(true);
        setCursorVisible(nextEditText,true);
        nextEditText.requestFocus();
        nextEditText.setBackground(this.childFocusStyle);
        // 设置为不可用
        currentEditText.setEnabled(false);
    }

    /**
     * 设置游标是否可见
     * @param editText 输入框
     * @param enable 是否可见
     */
    private void setCursorVisible(EditText editText, boolean enable){
        if(!this.isChildCursorVisible){
            return;
        }
        editText.setCursorVisible(enable);
    }

    /**
     * 设置输入框是否可用
     * @param editText 输入框
     * @param enable 是否可用
     */
    private void setChildEnable(EditText editText, boolean enable){
        editText.setEnabled(enable);
        setCursorVisible(editText,enable);
        if(enable){
            editText.requestFocus();
            editText.setBackground(this.childFocusStyle);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.println("onTextChanged => s = "+ s + ", start = "+ start+ ", count = " + count + ", before = " + before);
        if(count == 1){
            this.stringBuilder.append(s);
        }else{
            int deleteIndex = this.stringBuilder.length() - 1;
            if(deleteIndex >= 0){
                this.stringBuilder.deleteCharAt(deleteIndex);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // 下一个焦点
        if (s.length() != 0) {
            nextFocus();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 回退上一个输入框
            previousFocus();
        }
        return false;
    }

    private void getKeyBoard(EditText editText){
        InputMethodManager inputManager = (InputMethodManager)editText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private OnCompleteListener onCompleteListener;

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public interface OnCompleteListener {
        void onComplete(String code);
    }
}
