package com.soulrelay.gallery.view.gallery;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Author:    SuS
 * Version    V1.0
 * Date:      17/2/14
 * Description: ViewPaper嵌套使用时，当里面的viewpaper未滑动到最后一个时，外面的viewpaper禁止滑动
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 17/2/14          SuS                 1.0               1.0
 * Why & What is modified:
 */
public class MyViewPager extends ViewPager {

    private OnNeedScrollListener mOnNeedScrollListener;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnNeedScrollListener(OnNeedScrollListener listener){
        this.mOnNeedScrollListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mOnNeedScrollListener != null && !mOnNeedScrollListener.needScroll()){
            return false;
        } else {
            try {
                return super.onTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(mOnNeedScrollListener != null && !mOnNeedScrollListener.needScroll()){
            return false;
        } else {
            try {
                return super.onInterceptTouchEvent(event);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public interface OnNeedScrollListener{
        boolean needScroll();
    }
}
