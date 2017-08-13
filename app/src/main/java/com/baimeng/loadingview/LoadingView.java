package com.baimeng.loadingview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/8/13.
 */

public class LoadingView extends View {

    private float mCurrentRotationAngle;
    //小圆颜色数组
    private int [] mColors ;
    private long ROTATION_ANIMATION_TIME = 2000;
    private double mRotationRadius;
    private float mCircleRadius;
    private Paint mPaint;
    private int mCenterX ;
    private int mCenterY ;
    private ValueAnimator animator;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mColors = new int[]{Color.BLACK,Color.YELLOW,Color.BLUE,Color.RED,Color.GRAY,Color.GREEN};
    }

    private boolean mInitParams = false ;

    private void initParams() {
        mRotationRadius = getMeasuredWidth() / 4 ;
        mCircleRadius = getMeasuredWidth() / 8 ;
        mPaint = new Paint ();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mCenterX = getMeasuredWidth() / 2 ;
        mCenterY = getMeasuredHeight() / 2 ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mInitParams){
            initParams();
            mInitParams = true ;
        }
        drawRotationAnimator(canvas);
    }

    /**
     * 属性动画做旋转
     * @param canvas
     */
    private void drawRotationAnimator(Canvas canvas) {
        //搞一个变量不断的去改变 使用属性动画
        //旋转360度
       if(animator == null){
           animator = ObjectAnimator.ofFloat(0f, 2 * ((float) Math.PI));
           animator.setDuration(ROTATION_ANIMATION_TIME);
           animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

               @Override
               public void onAnimationUpdate(ValueAnimator animation) {
                   mCurrentRotationAngle = (float) animation.getAnimatedValue();
                   invalidate();
               }
           });
           animator.setInterpolator(new LinearInterpolator());
           animator.setRepeatCount(-1);
           animator.start();
       }

        canvas.drawColor(Color.WHITE);

        //圆之间的间隔角度
        double percentAngle = Math.PI * 2 / mColors.length;
        for (int i = 0 ; i < mColors.length ; i++) {
            double currentAngle = percentAngle * i + mCurrentRotationAngle;

            int cx = (int)(mCenterX + mRotationRadius * Math.cos(currentAngle));
            int cy = (int)(mCenterY + mRotationRadius * Math.sin(currentAngle));
            canvas.drawCircle(cx,cy , mCircleRadius, mPaint);
        }
    }
    public void disappear(){
        //开始聚合动画
        animator.cancel();
        invalidate();

    }

    public abstract class LoadingState{
        public abstract void draw(Canvas canvas);
    }

    public class RotationState extends LoadingState{

        @Override
        public void draw(Canvas canvas) {

        }
    }

    public class MergeState extends LoadingState{

        @Override
        public void draw(Canvas canvas) {

        }
    }

    public class ExpendState extends LoadingState{

        @Override
        public void draw(Canvas canvas) {

        }
    }


}
