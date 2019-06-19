package com.gystry.afloat;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @author <a href="rongwenyang@everimaging.com">Rwy</a>
 * @version v1.0
 * @create 2019/6/19 10:16
 * @update 2019/6/19 10:16
 * @since v1.0
 */
public abstract class RWFloatView {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private Context mContext;
    private boolean mIscreate = false;
    private boolean mCanmove = false;
    private boolean mIsShow = false;
    private View mDecView;


    public Context getContext() {
        return mContext;
    }

    public void setCanmove(boolean canmove) {
        this.mCanmove = canmove;
    }

    public RWFloatView(Context context) {
        this.mContext = context;

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mParams.format = PixelFormat.RGB_888;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.width = setWidth();
        mParams.height = setHeight();
    }

    protected abstract int setWidth();

    protected abstract int setHeight();

    protected abstract @LayoutRes int setContent();

    public void setAnimation(@AnimRes int anim) {
        mParams.windowAnimations = anim;
    }


    public void show() {
        if (mIsShow) {
            if (mDecView != null) {
                mDecView.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (!mIscreate) {
            create();
        }
        mWindowManager.addView(mDecView, mParams);
        mIsShow = true;
        onStart();
    }

    public void dismiss() {
        if (mDecView == null || !mIsShow) {
            return;
        }
        onStop();
        try {
            onStop();
            mWindowManager.removeViewImmediate(mDecView);
        } finally {
            mDecView = null;
            mIsShow = false;
            mIscreate = false;
        }
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return mDecView.findViewById(id);
    }

    private void create() {
        mDecView = LayoutInflater.from(mContext).inflate(setContent(), null);
        onCreate(mDecView,mParams);
        mIscreate = true;
        if (mCanmove) {
            mDecView.setOnTouchListener(new FloatViewTouch());
        }
    }

    protected void onCreate(View view,WindowManager.LayoutParams params) {
    }

    protected void onStart() {
    }

    protected void onStop() {
    }

    private class FloatViewTouch implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mParams.x = mParams.x + movedX;
                    mParams.y = mParams.y + movedY;

                    // 更新悬浮窗控件布局
                    mWindowManager.updateViewLayout(v, mParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
