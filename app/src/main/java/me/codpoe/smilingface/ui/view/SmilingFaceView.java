package me.codpoe.smilingface.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import me.codpoe.smilingface.R;

/**
 * Created by Codpoe on 2016/8/22.
 */
public class SmilingFaceView extends View {

    private Paint mPaint; // 画笔
    private int mColor; // 画笔颜色
    private int mAlpha; // 画笔透明度
    private int mPaintWidth; // 画笔宽度

    private int mWidth = 60; // 默认宽度
    private int mHeight = 60; // 默认高度
    private float mCenterX; // 中心 X
    private float mCenterY; // 中心 Y
    private float mRadius; // 半径
    private float[] mLPointPos = new float[2]; // 左点位置
    private float[] mRPointPos = new float[2]; // 右点位置

    private Path mPath;
    private Path dst1;
    private Path dst2;
    private Path dst3;
    private PathMeasure mMeasure;

    private enum State { // 三个状态：初始，刷新中，刷新完成
        NONE, REFRESHING, OK
    }
    private State mCurrentState = State.NONE; // 当前状态
    private Handler mHandler; // 用于状态切换

    private ValueAnimator mStartPAnim; // 起点动画
    private ValueAnimator mStopPAnim; // 终点动画
    private ValueAnimator mOkAnim; // 刷新完成的动画
    private AnimatorSet mAnimSet; // 刷新的组合动画
    private int mDuration; // 默认动画时长
    private float mStartPValue = 0.1f; // 起点动画的数值
    private float mStopPValue = 0.1f; // 终点动画的数值
    private float mOkValue = 0.1f; // 刷新完成动画的数值

    private ValueAnimator.AnimatorListener mListener; // 动画监听器
    private ValueAnimator.AnimatorUpdateListener mStartPUpdateListener; // 起点的动画数值监听器
    private ValueAnimator.AnimatorUpdateListener mStopPUpdateListener; // 终点的动画数值监听器
    private ValueAnimator.AnimatorUpdateListener mOkUpdateListener; // 刷新完成的动画监听器

    private boolean isOver; // 是否结束
    private boolean isPause = false; // 是否暂停

    public SmilingFaceView(Context context) {
        this(context, null);
    }

    public SmilingFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initPaint();
        initPath();
        initListener();
        initAnimator();
        initHandler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取宽度的模式与大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 获取高度的模式与大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // 设置宽高
        setMeasuredDimension(measureWidth(widthMode, width), measureHeight(heightMode, height));

        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        mRadius = Math.min(mCenterX, mCenterY) * 0.8f;
        // 计算左右两点的位置
        mLPointPos[0] = -mRadius * (float) (Math.cos(45));
        mLPointPos[1] = -mRadius * (float) (Math.sin(45));
        mRPointPos[0] = mRadius * (float) (Math.cos(45));
        mRPointPos[1] = -mRadius * (float) (Math.sin(45));
    }


//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mWidth = w;
//        mHeight = h;
//        mCenterX = w / 2;
//        mCenterY = h / 2;
//        mRadius = Math.min(mCenterX, mCenterY) * 0.8f;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mCenterX, mCenterY); // 移到画布中心

        // 添加Path
        RectF rectF1 = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        mPath.addArc(rectF1, 0, 359.99f);
        mPath.arcTo(rectF1, 359.99f, 359.99f, false);
        mPath.arcTo(rectF1, 359.98f, 180.02f, false);
        mMeasure.setPath(mPath, false);

        switch (mCurrentState) {
            case NONE:
                mMeasure.getSegment(0, mMeasure.getLength() * 0.2f, dst1, true);
                canvas.drawPath(dst1, mPaint);
                drawLPoint(canvas);
                drawRPoint(canvas);
                break;
            case REFRESHING:
                dst2.reset();
                mMeasure.getSegment(mMeasure.getLength() * mStartPValue, mMeasure.getLength() * mStopPValue, dst2, true);
                canvas.drawPath(dst2, mPaint);
                if (mStopPValue >= 0.25f && mStopPValue <= 0.65f) {
                    drawLPoint(canvas);
                }
                if (mStopPValue >= 0.35f && mStopPValue <= 0.75f) {
                    drawRPoint(canvas);
                }
                break;
            case OK:
                dst3.reset();
                mMeasure.getSegment(
                        mMeasure.getLength() * (-mOkValue + 0.1f),
                        mMeasure.getLength() * (mOkValue + 0.1f),
                        dst3,
                        true
                );
                canvas.drawPath(dst3, mPaint);
                if (mOkValue == 0.1f) {
                    drawLPoint(canvas);
                    drawRPoint(canvas);
                }
                break;
        }
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SmilingFaceView);
            mColor = array.getColor(R.styleable.SmilingFaceView_smiling_color, getResources().getColor(R.color.colorPrimary));
            mAlpha = array.getInt(R.styleable.SmilingFaceView_smiling_alpha, 255);
            mPaintWidth = array.getInt(R.styleable.SmilingFaceView_smiling_width, 10);
            mDuration = array.getInt(R.styleable.SmilingFaceView_smiling_duration, mDuration);
            array.recycle();
        }
    }

    /**
     * 初始化 Paint
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAlpha(mAlpha);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

    }

    /**
     * 初始化 Path
     */
    private void initPath() {
        mPath = new Path();
        dst1 = new Path();
        dst2 = new Path();
        dst3 = new Path();
        mMeasure = new PathMeasure();
    }

    /**
     * 初始化 Listener
     */
    private void initListener() {

        mStartPUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartPValue = (float) animation.getAnimatedValue();
                invalidate();

            }
        };

        mStopPUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStopPValue = (float) animation.getAnimatedValue() + 0.001f;
                if (!isPause && (int) (mStopPValue * 10) == 4) {
                    isPause = true;
                    mStartPAnim.pause();
                }
                if (isPause && (int) (mStopPValue * 10) == 6) {
                    isPause = false;
                    mStartPAnim.resume();
                }
                invalidate();
            }
        };

        mOkUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOkValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

    }

    /**
     * 初始化 Handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case REFRESHING:
                        if (!isOver) {
                            mAnimSet.start();
                        } else {
                            mCurrentState = State.OK;
                            mOkAnim.start();
                        }
                        break;
                    case OK:
                        mCurrentState = State.NONE;
                        break;
                }
            }
        };
    }

    /**
     * 初始化 Animator
     */
    private void initAnimator() {
        mStartPAnim = ValueAnimator.ofFloat(0.1f, 0.9f).setDuration(mDuration);
        mStopPAnim = ValueAnimator.ofFloat(0.1f, 0.9f).setDuration(mDuration);
        mAnimSet = new AnimatorSet();
        mAnimSet.play(mStartPAnim).with(mStopPAnim);

        mOkAnim = ValueAnimator.ofFloat(0, 0.1f).setDuration(mDuration / 5);

        mStartPAnim.addUpdateListener(mStartPUpdateListener);
        mStopPAnim.addUpdateListener(mStopPUpdateListener);
        mOkAnim.addUpdateListener(mOkUpdateListener);

        mAnimSet.addListener(mListener);
        mOkAnim.addListener(mListener);

    }

    /**
     * 根据测量模式返回宽度
     * @param mode
     * @param width
     * @return
     */
    private int measureWidth(int mode, int width) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                mWidth = width;
                break;
        }
        return mWidth;
    }

    /**
     * 根据测量模式返回高度
     * @param mode
     * @param height
     * @return
     */
    private int measureHeight(int mode, int height) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                mHeight = height;
                break;
        }
        return mHeight;
    }

    /**
     * 画点 1，即左眼
     * @param canvas
     */
    private void drawLPoint(Canvas canvas) {
        canvas.drawPoint(mLPointPos[0], mLPointPos[1], mPaint);
    }

    /**
     * 画点 2，即右眼
     * @param canvas
     */
    private void drawRPoint(Canvas canvas) {
        canvas.drawPoint(mRPointPos[0], mRPointPos[1], mPaint);
    }

    /**
     * 提供给外部的方法 ↓↓↓
     */

    /**
     * 开始刷新
     */
    public void start() {
        if (mCurrentState == State.NONE) {
            isOver = false;
            mCurrentState = State.REFRESHING;
            mAnimSet.start();
        }
    }

    /**
     * 结束刷新
     */
    public void stop() {
        if (mCurrentState == State.REFRESHING) {
            isOver = true;
        }
    }

    /**
     * 设置画笔颜色和透明度
     * @param color
     */
    public void setColorAndAlpha(int color, int alpha) {
        mColor = color;
        mAlpha = alpha;
        mPaint.setColor(mColor);
        mPaint.setAlpha(mAlpha);
    }

    /**
     * 设置画笔透明度
     * @param alpha
     */
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        mPaint.setAlpha(mAlpha);
    }

    /**
     * 设置画笔宽度
     * @param paintWidth
     */
    public void setPaintWidth(int paintWidth) {
        mPaintWidth = paintWidth;
        mPaint.setStrokeWidth(mPaintWidth);
    }

    /**
     * 设置动画时长
     * @param duration
     */
    public void setDuration(int duration) {
        mDuration = duration;
        mStartPAnim.setDuration(mDuration);
        mStopPAnim.setDuration(mDuration);
        mOkAnim.setDuration(mDuration / 5);
    }


}
