package com.example.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.view_libaray.R


class StepCountView : View {
    //背景环颜色
    private var mBackgroundArcColor: Int = Color.GRAY
    //进度环颜色
    private var mStepArcColor: Int = Color.BLUE
    //绘制环背景的画笔
    private val mBackgroundPaint = Paint()
    //外环画笔
    private val mProgressPaint = Paint()
    //文字画笔
    private val mTextPaint = Paint()
    //背景环宽度
    private var mBackgroundWidth: Float = 10f
    //进度环宽度
    private var mProgressWidth: Float = 15f
    //文字字体大小
    private var mNumberTextSize: Float = 40f
    //名次字体大小
    private var mRankTextSize: Float = 18f
    //圆环最大进度
    private val mMaxProgress: Int = 1000
    //圆环当前进度
    private var mCurrentProgress: Int = 0
    //进度环移动的速度
    private var mRate: Int = 100
    //步数和排名之间的间距
    private var mRankNumberSpc: Float = 80f

    constructor(context: Context, @Nullable attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepCountView)
        try {
            mBackgroundArcColor = typedArray.getColor(R.styleable.StepCountView_background_round_color, mBackgroundArcColor)
            mStepArcColor = typedArray.getColor(R.styleable.StepCountView_progress_bar_color, mStepArcColor)
            mBackgroundWidth = typedArray.getDimension(R.styleable.StepCountView_background_width, mBackgroundWidth)
            mProgressWidth = typedArray.getDimension(R.styleable.StepCountView_progress_width, mProgressWidth)
            mNumberTextSize = typedArray.getDimension(R.styleable.StepCountView_center_text_size, mNumberTextSize)
            mRankTextSize = typedArray.getDimension(R.styleable.StepCountView_rank_text_size, mNumberTextSize)
            mRankNumberSpc = typedArray.getDimension(R.styleable.StepCountView_rank_center_spc, mRankNumberSpc)
            mCurrentProgress = typedArray.getInt(R.styleable.StepCountView_current_progress, mCurrentProgress)
            mRate = typedArray.getInt(R.styleable.StepCountView_rate, mRate)
        } finally {
            typedArray.recycle()
        }

        initPaint()
    }

    private fun initPaint() {
        //背景画笔的初始化
        mBackgroundPaint.isAntiAlias = true//抗锯齿
        mBackgroundPaint.style = Paint.Style.STROKE
        mBackgroundPaint.color = mBackgroundArcColor
        mBackgroundPaint.strokeCap = Paint.Cap.ROUND
        mBackgroundPaint.strokeWidth = mBackgroundWidth

        //步数进度条
        mProgressPaint.isAntiAlias = true//抗锯齿
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.color = mStepArcColor
        mProgressPaint.strokeCap = Paint.Cap.ROUND
        mProgressPaint.strokeWidth = mProgressWidth

        //文字画笔的初始化
        mTextPaint.isAntiAlias = true
        mProgressPaint.style = Paint.Style.STROKE
        mTextPaint.color = Color.BLACK
        mTextPaint.textSize = mNumberTextSize
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        var mGradientColorArray = intArrayOf(ContextCompat.getColor(context, R.color.color_4fd8), ContextCompat.getColor(context, R.color.color_4fd8), ContextCompat.getColor(context, R.color.color_5e9f), ContextCompat.getColor(context, R.color.color_4fd8))
        var colors = IntArray(mGradientColorArray.size)
        System.arraycopy(mGradientColorArray, 0, colors, 0, mGradientColorArray.size)
        var positions = FloatArray(mGradientColorArray.size)
        var v = 360f / 270
        positions[0] = 0.0f
        positions[1] = 0.33f * v
        positions[2] = 0.67f * v
        positions[3] = 1.0f
        mProgressPaint.shader = SweepGradient(width / 2 - mProgressWidth / 2, width / 2 - mProgressWidth / 2, mGradientColorArray, positions)

        //绘制背景圆环
        val backRect = RectF(0 + mBackgroundWidth / 2, 0 + mBackgroundWidth / 2, width - mBackgroundWidth / 2, width - mBackgroundWidth / 2)
        canvas.drawArc(backRect, 135f, 270f, false, mBackgroundPaint)
        val progressRect = RectF(0 + mProgressWidth / 2, 0 + mProgressWidth / 2, width - mProgressWidth / 2, width - mProgressWidth / 2)

        if (mCurrentProgress < mMaxProgress) {
            canvas.drawArc(progressRect, 135f, mCurrentProgress / mMaxProgress * 270f, false, mProgressPaint)
        } else {
            canvas.drawArc(progressRect, 135f, 270f, false, mProgressPaint)
        }

        /**
         * currentStep Int类型当前
         */
        fun setCurrentStep(currentStep: Int) {
            this.mCurrentProgress = currentStep

            //强制重绘
            postInvalidate()
        }


    }

    /**
     * 动态设置步数
     */
    fun dynamicCountStep(currentStep: Int) {
        val valueAnimator = ObjectAnimator.ofInt(0, currentStep)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val currentStep = animation.animatedValue as Int
            dynamicCountStep(currentStep)
        }
        valueAnimator.start()
    }
}