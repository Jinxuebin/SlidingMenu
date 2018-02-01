package com.jin.slidingmenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jin.slidingmenu.paints.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  侧滑菜单栏
 * Created by Jin on 2018/1/2.
 */

public class SlidingMenu extends LinearLayout {

    public static final String LIST_ICON = "iv_icon";
    public static final String LIST_TITLE = "tv_list_name";

    private CustomAboveView customAboveView;
    private CustomBlowView customBlowView;

    private Context c;

    //触摸点坐标
    private int startX;
    private int startY;

    /**
     * 滑动距离
     */
    private int slideDistance = 0;

    /**
     * 触发滑动的最小距离180px
     */
    private int minSlideDistance = 180;

    /**
     * 是否开启了SlidingMenu菜单
     */
    private boolean isOpenSlidingMenu = false;

    /**
     * 动画持续时间
     */
    private long animatorDuration = 500;

    /**
     * 动画关闭器
     */
    private ValueAnimator closeSlidingMenuAnimator;

    /**
     * 动画开启器
     */
    private ValueAnimator openSlidingMenuAnimator;

    private DarkView darkView;
    private SlidingMenuListener slidingMenuListener;

    OnClickListener darkViewOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            LogUtils.e("你点击了我");
            if (isOpenSlidingMenu()) {
                closeSlidingMenu(0);
            }
        }
    };

    SlidingMenu that = this;


    public SlidingMenu(Context context) {
        this(context,null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initView(context);
    }

    private void initView(Context c) {
        this.c = c;

        //上面的部分
        customAboveView = new CustomAboveView(c);
        //下面的部分
        customBlowView = new CustomBlowView(c);

        //添加这两个View
        addView(customAboveView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(customBlowView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setBackgroundColor(Color.parseColor("#ffffff"));
    }


    public void setNickName(String nickName) {
        customAboveView.setNickName(nickName);
    }

    public void setmPhotoBitmap(Bitmap photo) {
        customAboveView.setmPhotoBitmap(photo);
    }

    public void setmPhotoBitmap(@DrawableRes int photo) {
        customAboveView.setmPhotoBitmap(photo);
    }

    public void setAboveBackgroudImg(Bitmap aboveBg) {
        customAboveView.setBackgroundBitmap(aboveBg);
    }

    public void setAboveBackgroundImg(@DrawableRes int aboveBg) {
        customAboveView.setBgImgId(aboveBg);
    }

    public void setListDividerHeight(int height) {
        customBlowView.setDividerHeight(height);
    }

    /**
     * 填充list列表
     * @param lists list列表
     */
    public void setListData(List<Map<String,Object>> lists) {
        customBlowView.setData(lists);
    }

    public void setListData(ListDataInterface data) {
        customBlowView.setData(data);
    }

    public CustomAboveView getCustomAboveView() {
        return customAboveView;
    }

    public CustomBlowView getCustomBlowView() {
        return customBlowView;
    }

    public void setMinSlideDistance(int minSlideDistance) {
        this.minSlideDistance = minSlideDistance;
    }

    public int getMinSlideDistance() {
        return minSlideDistance;
    }

    public boolean isOpenSlidingMenu() {
        return isOpenSlidingMenu;
    }

    public void setOpenSlidingMenu(boolean openSlidingMenu) {
        isOpenSlidingMenu = openSlidingMenu;
    }

    public void setAnimatorDuration(long animatorDuration) {
        this.animatorDuration = animatorDuration;
    }

    public long getAnimatorDuration() {
        return animatorDuration;
    }

    /**
     * 动态给BlowView添加一个item
     * @param obj item的图标
     * @param itmeName item的名称
     */
    public void addItem(Object obj, String itmeName) {
        Map<String, Object> map = new HashMap<>();
        map.put("iv_icon",obj);
        map.put("tv_list_name", itmeName);
        customBlowView.addItem(map);
    }

    /**
     * 返回true，拦截事件，事件将不会传递到子view中
     * 返回false，事件将会传递到子view。子view如果没有拦截事件，又会将事件给了ViewGroup来处理。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("slidingMenu---dispatchTouchEvent---down");
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                return false;
            case  MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int dx = x - startX;
                int dy = y - startY;
                if (Math.abs(dx) > Math.abs(dy)) {
                    //横向运动
                    //拦截事件，子view将不会处理该事件
                    LogUtils.d("slidingmeu----dispatchTouchEvent----横向运动");
                    return true;
                }
                return false;

            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case  MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int dx = x - startX;
                slideDistance += dx;
                //横向运动
                slidingMenuOnMove(dx,slideDistance);
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.e("slideDistance is %d",slideDistance);
                slidingMenuOnUp(slideDistance);
                slideDistance = 0;
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
        }
        return false;
    }

    public void openSlidingMenu(int dx) {
        if (dx >= 0 && dx <= getWidth()) {
            openSlidingMenuAnimator = ValueAnimator.ofInt(dx, getWidth());
            if (openSlidingMenuAnimator.isRunning()) return;
            openSlidingMenuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) openSlidingMenuAnimator.getAnimatedValue();
                    setLayoutParams(-getWidth() + value, getTop(), value, getBottom());
                    float k = Math.abs((float)value / getWidth());
                    changeDarkView(k);
                    if (value >= getWidth()) {
                        setOpenSlidingMenu(true);
                        LogUtils.d("slidingMenu已经开启:%b" ,isOpenSlidingMenu());
                        openSlidingMenuAnimator.removeUpdateListener(this);
                        drakViewOnClick(true);
                        invalidate();
                        if (slidingMenuListener != null) {
                            slidingMenuListener.opened();
                        }
                    }
                }
            });
            openSlidingMenuAnimator.setDuration((int)(animatorDuration * (getWidth() - Math.abs(dx)) /(float)getWidth()));
            openSlidingMenuAnimator.setInterpolator(new AccelerateInterpolator());
            openSlidingMenuAnimator.start();
        }
    }

    public void closeSlidingMenu(int dx) {
        if (dx <= 0 && dx >= - getWidth()) {
            closeSlidingMenuAnimator = ValueAnimator.ofInt(dx, -getWidth());
            if (closeSlidingMenuAnimator.isRunning()) return;
            closeSlidingMenuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) closeSlidingMenuAnimator.getAnimatedValue();
                    setLayoutParams(value, getTop(), getWidth() + value, getBottom());
                    float k = Math.abs((float)value / getWidth());
                    changeDarkView(1 - k);
                    if (value <= -getWidth()) {
                        setOpenSlidingMenu(false);
                        LogUtils.d("slidingMenu已经关闭：%b",isOpenSlidingMenu());
                        closeSlidingMenuAnimator.removeUpdateListener(this);
                        drakViewOnClick(false);
                        if (slidingMenuListener != null) {
                            slidingMenuListener.closeed();
                        }
                    }
                }
            });
            closeSlidingMenuAnimator.setDuration((int)(animatorDuration * (getWidth() - Math.abs(dx)) / (float) getWidth()));
            closeSlidingMenuAnimator.setInterpolator(new AccelerateInterpolator());
            closeSlidingMenuAnimator.start();
        }
    }

    public void bindDrakView(DarkView v) {
        this.darkView = v;
        darkView.setInterceptEventListener(new DarkView.onInterceptEventListener() {
            @Override
            public void onClick(MotionEvent event) {
                closeSlidingMenu(0);
            }

            @Override
            public void onMoveEvent(MotionEvent event, float dx, float slideDistance) {
                LogUtils.e("slideDistance: %f",slideDistance);
                slidingMenuOnMove(dx,slideDistance);
            }

            @Override
            public void onUpEvent(MotionEvent event, float slideDistance) {
                slidingMenuOnUp(slideDistance);
            }
        });
    }

    private void slidingMenuOnMove(float dx, float slideDistance) {

        if (isOpenSlidingMenu() && slideDistance >= - getWidth() && slideDistance < 0 || !isOpenSlidingMenu() && slideDistance >=0 && slideDistance <= getWidth()) {
            float k = slideDistance / getWidth();
            changeDarkView(k);
        }
        if (getLeft() + (int)dx >= 0 ) {
            dx = -getLeft();
            openSlidingMenu(getWidth());
            return;
        }
        if (getLeft() + (int) dx <= -getWidth()) {
            dx = -getWidth() - getLeft();
            closeSlidingMenu(-getWidth());
            return;
        }
        setLayoutParams(getLeft() + (int)dx,getTop(),getLeft() + getWidth() + (int)dx,getBottom());

    }

    private void slidingMenuOnUp(float slideDistance) {
        if (slideDistance < 0 && slideDistance > - getMinSlideDistance()) {
            //平缓打开
            openSlidingMenu(getWidth() + (int)slideDistance);
        } else if (slideDistance <= - getMinSlideDistance()) {
            //向左关闭
            if (isOpenSlidingMenu()) {
                closeSlidingMenu((int)slideDistance);
            } else {
                openSlidingMenu(getRight());
            }
        } else if (slideDistance >= 0 && slideDistance < getMinSlideDistance()) {

            if (isOpenSlidingMenu()) {
                if (getWidth() == getRight()) {
                    return;
                }
                openSlidingMenu(getWidth() - (int)slideDistance);
            }else {
                closeSlidingMenu((int)slideDistance);
            }
        } else if (slideDistance >= getMinSlideDistance()) {
            //activity调用open方法打开slidingMenu
            if (isOpenSlidingMenu()) {
                openSlidingMenu(getRight());
            } else {
                closeSlidingMenu(getLeft());
            }
        }
    }

    /**
     * 拦截事件
     * @param clickable
     */
    private void drakViewOnClick(Boolean clickable){
        if (this.darkView == null)
            return;
        if (clickable) {
            darkView.setAllowReceiverMove(true);
        } else {
            darkView.setAllowReceiverMove(false);
        }

    }

    private void changeDarkView(float alpha) {

        if (alpha < 0 && alpha > -1) {
            alpha = 1 + alpha;
        }

        if (this.darkView != null) {
            alpha = 170 * alpha;
            String s = Integer.toHexString((int)alpha);
            if (s.length() < 2) {
                s = 0 + s;
            }
            darkView.setBackgroundColor(Color.parseColor("#" + s + "000000"));
        }
    }

    public void setPhotoOnClickListener(OnClickListener listener) {
        if (customAboveView != null) {
            customAboveView.setPhotoOnClickListener(listener);
        }
    }

    public void setNickNameOnClickListener(OnClickListener listener) {
        if (customAboveView != null) {
            customAboveView.setNickNameOnClickListener(listener);
        }
    }

    public void removePhotoOnClickListener() {
        if (customAboveView != null) {
            customAboveView.removePhotoOnClickListener();
        }
    }

    public void removeNickNameOnClickListener() {
        if (customAboveView != null) {
            customAboveView.removeNickNameOnClickListener();
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        if (customBlowView != null) {
            customBlowView.setOnItemClickListener(listener);
        }
    }

    public void setSlidingMenuListener(SlidingMenuListener listener) {
        slidingMenuListener = listener;
    }

    interface SlidingMenuListener{

        void opened();

        void closeed();
    }

    private void setLayoutParams(int l, int t, int r, int b) {
        if(getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams)getLayoutParams();
            layoutParams.leftMargin = l;
            layoutParams.width = r - l;
            setLayoutParams(layoutParams);

        } else if (getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams)getLayoutParams();
            layoutParams.leftMargin = l;
            layoutParams.width = r - l;
            setLayoutParams(layoutParams);
        } else {
            throw  new RuntimeException("请将父布局设置为RelativeLayout或者LinearLayout");
        }

    }
}
