package com.jin.slidingmenu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jin.slidingmenu.paints.LogUtils;

/**
 * 该View是一个最上层的view，用于显示屏幕亮度渐变
 *
 * 该view要响应点击事件和横向滑动时间，把其他时间分发给底层的view
 */

public class DarkView extends View {

    private float startX;
    private float startY;

    private onInterceptEventListener interceptEventListener;
    private int orginX;
    private int orginY;
    private float slideDistance = 0;

    public DarkView(Context context) {
        super(context);
    }

    public DarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * true 允许接收move时间，拦截事件
     * false 不处理该事件，交由其他View处理该时间
     */
    private boolean allowReceiverMove;

    /**
     * groupView调用该方法
     * @param event event
     * @return true 消耗掉该事件,并且不会向下层重叠的兄弟View传递事件
     *          false 让GroupView处理该事件，该事件会传递到兄弟View中
     *          super.dispatchTouchEvent(event)：调用父类的方法，父类的方法会调用该view的OnTouch方法和
     *          onTouchEvent方法。
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return handleDarkViewEvent(event) || allowReceiverMove;
    }

    public void setAllowReceiverMove(boolean allowReceiverMove) {
        this.allowReceiverMove = allowReceiverMove;
    }

    public boolean isAllowReceiverMove() {
        return allowReceiverMove;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.e("DarkView----------onTouchEvent");
        return super.onTouchEvent(event);
    }

    public boolean handleDarkViewEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("handleHorizontalEvent-down");
                startX = event.getRawX();
                startY = event.getRawY();
                orginX = (int) event.getRawX();
                orginY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.e("handleHorizontalEvent-move");
                float dx = event.getRawX() - startX;
                float dy = event.getRawY() - startY;
                slideDistance += dx;
                if (interceptEventListener != null) {
                    interceptEventListener.onMoveEvent(event, dx, slideDistance);
                }
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.e("handleHorizontalEvent-up");
                if (orginX == (int)event.getRawX() && orginY == (int)event.getRawY() && interceptEventListener != null) {
                    interceptEventListener.onClick(event);
                }else {
                    if (interceptEventListener != null) {
                        interceptEventListener.onUpEvent(event, slideDistance);
                    }
                }
                slideDistance = 0;
                startX = event.getRawX();
                startY = event.getRawY();
                break;
        }
        return false;
    }

    public void setInterceptEventListener(onInterceptEventListener interceptEventListener) {
        this.interceptEventListener = interceptEventListener;
    }

    public void removeInterceptEventListener() {
        this.interceptEventListener = null;
    }

    public interface onInterceptEventListener{
        void onClick(MotionEvent event);
        void onMoveEvent(MotionEvent event, float dx, float slideDistance);
        void onUpEvent(MotionEvent event, float slideDistance);
    }
}
