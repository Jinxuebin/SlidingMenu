package com.jin.slidingmenu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jin.slidingmenu.paints.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 侧滑菜单的下部分list列表,该布局只放一个组件，以后可以扩展组件
 * Created by Jin on 2018/1/3.
 */

public class CustomBlowView extends LinearLayout{

    private Context c;

    private ListView listView;

    private List<Map<String, Object>> lists;
    private SimpleAdapter simpleAdapter;
    private int startX;
    private int startY;

    public CustomBlowView(Context context) {
        this(context, null);
        this.c = context;

    }

    public CustomBlowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBlowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initData();
        initView(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 如果该子View是横向事件，则不处理事件，交给上级的groupView来处理
     */
    private void initEvent() {

        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        LogUtils.e("ListView-----down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int)event.getX();
                        int y = (int)event.getY();
                        int dx = x - startX;
                        int dy = y - startY;

                        if (Math.abs(dx) > Math.abs(dy)) {
                            //横向运动 -- 把事件交给父ViewGroup处理
                            LogUtils.e("发生了横向运动");
                            return false;
                        } else {
                            LogUtils.e("发生了纵向运动");
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

    }

    private void initData() {
        lists = new ArrayList<>();
    }

    public void stopListViewEvent(boolean isStop) {
        listView.setEnabled(!isStop);
        listView.setClickable(!isStop);
        listView.setLongClickable(!isStop);
    }

    private void initView(Context c) {
        listView = new ListView(c);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        simpleAdapter = new SimpleAdapter(c, lists, R.layout.sliding_list, new String[]{"iv_icon", "tv_list_name"},
                new int[]{R.id.iv_icon, R.id.tv_list_name});
        listView.setAdapter(simpleAdapter);
        addView(listView);
    }

    public void addItem(Map<String, Object> maps) {
        lists.add(maps);
        simpleAdapter.notifyDataSetChanged();
    }

    public void removeAll() {
        lists.clear();
        simpleAdapter.notifyDataSetChanged();
    }

    public void setData(List<Map<String, Object>> lists) {
        this.lists = lists;
        simpleAdapter = new SimpleAdapter(c, lists, R.layout.sliding_list, new String[]{"iv_icon", "tv_list_name"},
                new int[]{R.id.iv_icon, R.id.tv_list_name});
        listView.setAdapter(simpleAdapter);
    }

    public void setData(ListDataInterface listDate) {
        setData(listDate.getData());
    }

    public void setDividerheight(int height) {
        listView.setDividerHeight(height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wm = MeasureSpec.getMode(widthMeasureSpec); // 1073741824 match_parent
        int hm = MeasureSpec.getMode(heightMeasureSpec); // 1073741824 match_parent
        int ws = MeasureSpec.getSize(widthMeasureSpec); // 801
        int hs = MeasureSpec.getSize(heightMeasureSpec); //1104

//        LogUtils.e("wm" + wm);
//        LogUtils.e("hm" + hm);
//        LogUtils.e("ws" + ws);
//        LogUtils.e("hs" + hs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.e("----------CustomBlowView-onTouchEvent------------");
        return false;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listItemListener) {
        listView.setOnItemClickListener(listItemListener);
    }

}
