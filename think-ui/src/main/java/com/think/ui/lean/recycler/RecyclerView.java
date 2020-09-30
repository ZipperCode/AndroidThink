package com.think.ui.lean.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.think.ui.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerView extends ViewGroup {
    /* 适配器 */
    private Adapter adapter;
    /* 显示的视图列表 */
    private List<View> viewList;
    /* 当前滑动的位置 */
    private int currentY;
    /* 总的行数 */
    private int rowCount;
    /* 第一行的位置 */
    private int firstRow;
    /* 滑动的位置 */
    private int scrollY;
    /* 是否需要重新布局 */
    private boolean needRelayout;
    /* view的宽度 */
    private int width;
    /* view的高度 */
    private int height;
    /* 保存每一个view的高度 */
    private int[] heights;
    /* 回收池 */
    private Recycler recycler;
    /* 最小滑动距离 */
    private int touchSlop;

    public RecyclerView(Context context) {
        super(context);
        init();
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.touchSlop = configuration.getScaledTouchSlop();
        this.viewList = new ArrayList<>();
        this.needRelayout = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(needRelayout || changed){
            needRelayout = false;
        }
        // 将显示的view列表清空
        viewList.clear();
        // 且将view移除
        removeAllViews();

        if(adapter != null){

            width = r - l;
            height = b - t;
            int left = 0,top = 0, right = 0,bottom = 0;
            for (int i = 0; i < rowCount && bottom < height; i++) {
                right = width;
                bottom = top + heights[i];
                // 根据view的大小去绘制view
                addView(makeAndStep(i,left,top,right,bottom));
            }
        }
    }

    private View makeAndStep(int row, int left, int top, int right, int bottom){
        View view = obtainView(row, right - left, bottom - top);
        view.layout(left,top,right,bottom);
        return view;
    }

    private View obtainView(int row, int width, int height){
        // 获取itemType
        int typeItem = adapter.getItemViewType(row);
        // 从回收池中获取一个view
        // 首次加载的情况肯定是null
        View recyclerView = recycler.get(typeItem);

        View view = null;
        if(recyclerView == null){
            view = adapter.onCreateViewHolder(row,recyclerView,this);
            if(view == null){
                throw  new RuntimeException("onCreateViewHolder can't return null");
            }
        }else{
            view = adapter.onBinderViewHolder(row,recyclerView,this);
        }
        // 设置TAG
        view.setTag(R.id.tag_type_view,typeItem);
        // 测量子view的宽高
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));

        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        if(adapter != null){
            this.rowCount = adapter.getCount();
            if(this.heights == null){
                this.heights = new int[rowCount];
            }
            for (int i = 0; i < heights.length; i++) {
                heights[i] = adapter.getHeight(i);
            }
        }
        int tempH = sumArray(heights,0,heights.length);
        height = Math.min(heightSize,tempH);
        setMeasuredDimension(widthSize,height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int sumArray(int array[], int firstIndex, int count){
        int sum = 0;
        count += firstIndex;
        for (int i = 0; i < count; i++) {
            sum += array[i];
        }
        return sum;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                currentY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int y2 = Math.abs(currentY - (int) ev.getRawY());
                if(y2 > touchSlop){
                    intercept = true;
                }
                break;
        }

        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                int y2 = (int) event.getRawY();
                int diff = currentY - y2;
                scrollBy(diff,0);
                break;
        }
        return super.onTouchEvent(event);
    }

    private int scrollBounds(int scrollY){
        if(scrollY > 0){
            scrollY = Math.min(scrollY,
                    sumArray(heights,firstRow,heights.length - firstRow)- height);
        }else{
            scrollY = Math.max(scrollY, -sumArray(heights,0,firstRow));
        }
        return scrollY;
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollY += y;

        scrollY = scrollBounds(scrollY);

        if(scrollY > 0){
            while (scrollY > heights[firstRow]){
                removeView(viewList.remove(0));
                scrollY -= heights[firstRow];
                firstRow ++;
            }

            while (getFullHeihgt() < height){
                int addLast = firstRow + viewList.size();

                View view = obtainView(addLast,width,heights[addLast]);
                viewList.add(view);
            }

        }else if(scrollY < 0){
            while (scrollY < 0){
                int firstAddRow = firstRow - 1;
                View view = obtainView(firstAddRow,width,heights[firstAddRow]);
                viewList.add(0,view);
                firstRow--;
                scrollY += heights[firstAddRow];
            }

            while (sumArray(heights,firstRow,viewList.size()) - scrollY - heights[firstRow + viewList.size() - 1] >= height){
                removeView(viewList.remove(viewList.size() - 1));
            }
        }else{

        }
        repositionViews();
    }

    private void repositionViews(){
        int left,top,right,bottom;
        top = - scrollY;
        int i = firstRow;
        for (View view: viewList){
            bottom = top + heights[i++];
            view.layout(0,top,width,bottom);
            top = bottom;
        }
    }

    private int getFullHeihgt(){

        return sumArray(heights, firstRow, viewList.size()) - scrollY;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int key = (int) view.getTag(R.id.tag_type_view);
        recycler.put(view,key);
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        this.heights = new int[adapter.getCount()];

        recycler = new Recycler(adapter.getViewTypeCount());
        scrollY = 0;
        firstRow = 0;
        needRelayout = true;
        requestLayout();

    }

    interface Adapter{
        View onCreateViewHolder(int position, View convertView, ViewGroup parent);

        View onBinderViewHolder(int position, View convertView, ViewGroup parent);

        int getItemViewType(int row);

        int getViewTypeCount();

        int getCount();

        int getHeight(int position);
    }
}
