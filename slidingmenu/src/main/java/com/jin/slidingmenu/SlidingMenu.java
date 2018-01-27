package com.jin.slidingmenu;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.jin.slidingmenu.paints.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  侧滑菜单栏
 * Created by Jin on 2018/1/2.
 */

public class SlidingMenu extends LinearLayout {

    private CustomAboveView customAboveView;
    private CustomBlowView customBlowView;

    /**
     * ViewGroup的宽度
     */
    private int viewWidth = 801;

    /**
     * ViewGroup的高度
     */
    private int viewHeight = 1704;

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
    private boolean isOpenSlidingMenu = true;

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
        //上面的部分
        customAboveView = new CustomAboveView(c);
        //下面的部分
        customBlowView = new CustomBlowView(c);


        //添加这两个View
        addView(customAboveView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(customBlowView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public void setNickName(String nickName) {
        customAboveView.setNickName(nickName);
    }

    public void setmPhotoBitmap(Bitmap photo) {
        customAboveView.setmPhotoBitmap(photo);
    }

    public void setmPhotoBitmap(@IdRes int photo) {
        customAboveView.setmPhotoBitmap(photo);
    }

    public void setAboveBackgroudImg(Bitmap aboveBg) {
        customAboveView.setBackgroudBitmap(aboveBg);
    }

    public void setAboveBackgroundImg(@IdRes int aboveBg) {
        customAboveView.setBgImgId(aboveBg);
    }

    public void setListDividerHeight(int height) {
        customBlowView.setDividerheight(height);
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

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewWidth() {
        return viewWidth;
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        viewWidth = getWidth();
        int wm = MeasureSpec.getMode(widthMeasureSpec); // 1073741824 match_parent
        int hm = MeasureSpec.getMode(heightMeasureSpec); // 1073741824 match_parent
        int ws = MeasureSpec.getSize(widthMeasureSpec); // 801
        int hs = MeasureSpec.getSize(heightMeasureSpec); //1104

//        LogUtils.e("wm" + wm); // 1073741824 - 确定的801
//        LogUtils.e("hm" + hm); // 1073741824 - match_parent
//        LogUtils.e("ws" + ws); // 801
//        LogUtils.e("hs" + hs); // 1704
//        setMeasuredDimension(wm == MeasureSpec.EXACTLY ? ws : getViewWidth(), hm == MeasureSpec.EXACTLY ? hs: getViewHeight());

    }

    /**
     * 返回true，拦截事件，事件将不会传递到子view中
     * 返回false，事件将会传递到子view。子view如果没有拦截事件，又会将事件给了ViewGroup来处理。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.e("slidingMenu---dispatchTouchEvent---down");
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                return false;
            case  MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                int dx = x - startX;
                int dy = y - startY;
                if (Math.abs(dx) >= Math.abs(dy)) {
                    //横向运动
                    //拦截事件，子view将不会处理该事件
                    LogUtils.e("slidingmeu----dispatchTouchEvent----横向运动");
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
                LogUtils.e("sliding---move------------");
                int x = (int) event.getRawX();
                int dx = x - startX;
                slideDistance += dx;
                LogUtils.d("dx:%d",dx);
                LogUtils.d("slidDistance: %d",slideDistance);

                //横向运动
                if (isOpenSlidingMenu() && slideDistance >= -getViewWidth() && slideDistance < 0 || !isOpenSlidingMenu() && slideDistance >=0 && slideDistance <= getViewWidth()) {
                    layout(getLeft() + dx,getTop(),getLeft() + getViewWidth() + dx,getBottom());
                }
                LogUtils.d("getLeft():%d,gerRight:%d",getLeft(),getRight());

                //即时重新更新起点坐标，防止dx，dy出现误差而过大。
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                LogUtils.e("slideDistance is %d",slideDistance);
                if (slideDistance < 0 && slideDistance > - getMinSlideDistance()) {
                    //平缓打开
                    openSlidingMenu(getViewWidth() + slideDistance);
                } else if (slideDistance <= - getMinSlideDistance()) {
                    //向左关闭
                    if (isOpenSlidingMenu()) {
                        closeSlidingMenu(slideDistance);
                    }
                } else if (slideDistance >= 0 && slideDistance < getMinSlideDistance()) {
                    //平滑关闭
                    //逻辑需要在外部的activity中完成
                    closeSlidingMenu(getLeft());
                } else if (slideDistance >= getMinSlideDistance()) {
                    //向右打开
                    if (!isOpenSlidingMenu()) {
                        openSlidingMenu(slideDistance);
                    } else {
                        //可能出现了差错，slidedistance不准确。
                        closeSlidingMenu(getLeft());
                    }
                }
                slideDistance = 0;
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
        }
        return false;
    }

    public void openSlidingMenu(int dx) {
        LogUtils.d("开始开启slidingmenu.dx:%d",dx);
        if (dx >= 0 && dx <= getViewWidth()) {
            openSlidingMenuAnimator = ValueAnimator.ofInt(dx, getViewWidth());
            //防止重复运行
            if (openSlidingMenuAnimator.isRunning()) return;
            openSlidingMenuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) openSlidingMenuAnimator.getAnimatedValue();
                    layout(-getViewWidth() + value, getTop(), value, getBottom());
                    if (value >= getViewWidth()) {
                        setOpenSlidingMenu(true);
                        LogUtils.d("slidingMenu已经开启:%b" ,isOpenSlidingMenu());
                        openSlidingMenuAnimator.removeUpdateListener(this);
                    }
                }
            });
            openSlidingMenuAnimator.setDuration((int)(animatorDuration * (getViewWidth() - Math.abs(dx)) /(float)getViewWidth()));
            openSlidingMenuAnimator.setInterpolator(new AccelerateInterpolator());
            openSlidingMenuAnimator.start();
        }
    }

    public void closeSlidingMenu(int dx) {
        if (dx <= 0 && dx >= - getViewWidth()) {
            closeSlidingMenuAnimator = ValueAnimator.ofInt(dx, -getViewWidth());
            // 防止SlidingMenuAnimator重复运行
            if (closeSlidingMenuAnimator.isRunning()) return;
            closeSlidingMenuAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) closeSlidingMenuAnimator.getAnimatedValue();
                    layout(value, getTop(), getViewWidth() + value, getBottom());
                    if (value <= -getViewWidth()) {
                        setOpenSlidingMenu(false);
                        LogUtils.d("lisingMenu已经关闭：%b",isOpenSlidingMenu());
                        //移除监听器，优化代码
                        closeSlidingMenuAnimator.removeUpdateListener(this);
                    }
                }
            });
            int i = (int)(animatorDuration * (getViewWidth() - Math.abs(dx)) / (float) getViewWidth());
            LogUtils.e("duration:%d",i);
            closeSlidingMenuAnimator.setDuration(i);
            closeSlidingMenuAnimator.setInterpolator(new AccelerateInterpolator());
            closeSlidingMenuAnimator.start();
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
}
