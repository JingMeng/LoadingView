package com.baimeng.loadingview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by Administrator on 2017/8/13.
 */

public class LoadingView extends View {

    private float mCurrentRotationAngle;
    //小圆颜色数组
    private int [] mColors ;
    //旋转动画执行一周时长
    private long ROTATION_ANIMATION_TIME = 2000;
    //小圆绕中心点旋转半径
    private double mRotationRadius;
    //小圆半径
    private float mCircleRadius;
    private Paint mPaint;
    //中心点X轴坐标
    private int mCenterX ;
    //中心点Y轴坐标
    private int mCenterY ;
    private LoadingState loadState;
    //屏幕对角线
    private double minRadiu ;
    //描边宽度
    private float mPaintWidth ;

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
        mCircleRadius = getMeasuredWidth() / 40 ;
        mPaint = new Paint ();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mCenterX = getMeasuredWidth() / 2 ;
        mCenterY = getMeasuredHeight() / 2 ;
        //求对角线的长度
        minRadiu = Math.sqrt( mCenterX*mCenterX + mCenterY*mCenterY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!mInitParams){
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
        if(loadState == null)
            loadState = new RotationState();
        loadState.draw(canvas);

    }
    public void disappear(){
        //开始聚合动画
        ((RotationState)loadState).cancel();
        invalidate();

    }

    public abstract class LoadingState{
        public abstract void draw(Canvas canvas);
    }

    public class RotationState extends LoadingState{
        //搞一个变量不断的去改变 使用属性动画
        //旋转360度
        ValueAnimator animator ;
        public RotationState() {
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

        @Override
        public void draw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            //圆之间的间隔角度
            double percentAngle = Math.PI * 2 / mColors.length;
            for (int i = 0 ; i < mColors.length ; i++) {
                mPaint.setColor(mColors[i]);
                double currentAngle = percentAngle * i + mCurrentRotationAngle;
                int cx = (int)(mCenterX + mRotationRadius * Math.cos(currentAngle));
                int cy = (int)(mCenterY + mRotationRadius * Math.sin(currentAngle));
                canvas.drawCircle(cx,cy , mCircleRadius, mPaint);
            }
        }

        public void cancel(){
            animator.cancel();
            loadState = new MergeState();
        }
    }

    public class MergeState extends LoadingState{

        ValueAnimator animator ;

        public MergeState() {
            animator = ObjectAnimator.ofFloat((float) mRotationRadius, 0);
            animator.setDuration(ROTATION_ANIMATION_TIME/2);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRotationRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new AnticipateInterpolator(3f));
            animator.setRepeatCount(0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loadState = new ExpendState() ;
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setColor(Color.WHITE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            //圆之间的间隔角度
            double percentAngle = Math.PI * 2 / mColors.length;
            for (int i = 0 ; i < mColors.length ; i++) {
                mPaint.setColor(mColors[i]);
                double currentAngle = percentAngle * i + mCurrentRotationAngle;
                int cx = (int)(mCenterX + mRotationRadius * Math.cos(currentAngle));
                int cy = (int)(mCenterY + mRotationRadius * Math.sin(currentAngle));
                canvas.drawCircle(cx,cy , mCircleRadius, mPaint);
            }
        }
    }

    public class ExpendState extends LoadingState{
        ValueAnimator animator ;
        public ExpendState() {
            animator = ObjectAnimator.ofFloat(0f,(float) minRadiu );
            animator.setDuration(ROTATION_ANIMATION_TIME);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mPaintWidth = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new LinearInterpolator());
            animator.setRepeatCount(0);
            animator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            mPaint.setStrokeWidth((float) (minRadiu-mPaintWidth));
            canvas.drawCircle(mCenterX,mCenterY,mPaintWidth,mPaint);
        }
    }


}
