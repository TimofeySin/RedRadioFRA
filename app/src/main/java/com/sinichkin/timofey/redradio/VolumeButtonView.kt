package com.sinichkin.timofey.redradio

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class VolumeButtonView(context: Context?, attrs: AttributeSet?) : View(context, attrs)   {
    interface OnSliderMovedListener {

        fun onSliderMoved(pos: Double,firstPos: Double,startRotation: Float)
    }

    private var mCircleCenterX = 0
    private var mCircleCenterY = 0
    private var mCircleRadius = 0
    private var mAngle = 0.0
    private var mListener: OnSliderMovedListener? = null
    private var firstX = 0f
    private var firstY = 0f
    private var firstRot = 0f
    private var secondView:View? = null

    fun setSecondView(view:View?){
        secondView = view
    }

    fun setOnSliderMovedListener(listener: OnSliderMovedListener) {
        mListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        this.performClick()
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
               parent.requestDisallowInterceptTouchEvent(true)
                 firstX = ev.x
                 firstY = ev.y
                if (secondView!=null) {
                    firstRot = secondView!!.rotation
                }
            }
            MotionEvent.ACTION_MOVE -> {
                    val x = ev.x
                    val y = ev.y
                    updateSliderState(x, y,firstX,firstY,firstRot)
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    // fun  performClick(): Boolean {return super.performClick()}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // use smaller dimension for calculations (depends on parent size)
        val smallerDim = if (w > h) h else w

        // find circle's rectangle points
        val largestCenteredSquareLeft = (w - smallerDim) / 2
        val largestCenteredSquareTop = (h - smallerDim) / 2
        val largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim
        val largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim

        // save circle coordinates and radius in fields
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2
        mCircleRadius = smallerDim / 2 //- mBorderThickness / 2 - mPadding

        // works well for now, should we call something else here?

        super.onSizeChanged(w, h, oldw, oldh)
    }

    private  fun updateSliderState(touchX: Float, touchY: Float, firstTouchX: Float, firstTouchY: Float,startRotation: Float) {
            mAngle  = calculateAngle(touchX, touchY)
        val mAngle2 = calculateAngle(firstTouchX, firstTouchY)

        // notify slider moved listener of the new position which should be in [0..1] range
        this.mListener!!.onSliderMoved(mAngle,mAngle2,startRotation)
    }

    private fun calculateAngle(x: Float, y: Float): Double {
        val distanceX: Int = (x - mCircleCenterX).toInt()
        val distanceY: Int = (mCircleCenterY - y).toInt()
        val c = sqrt(
            distanceX.toDouble().pow(2.0) + distanceY.toDouble().pow(2.0)
        )
        var localmAngle = acos(distanceX / c)
        if (distanceY < 0) {
            localmAngle = -localmAngle
        }
        return localmAngle/ (2 * Math.PI)
    }
    
    
    // you get the angle from the slider listener
//    fun convertAngleToClock(angle: Float): Float {
//        return ((3f / 4f - angle) % 1)*360f
//    }

    // use the returned value in the `setPosition(float)` method
    fun convertClockToPosition(clock: Float): Float {
        return (1f - (3f / 4f + clock) % 1)*360f
    }

    fun roundAngle(a: Float, oldAngle: Float): Float {
        var angle = a
        while (angle >= 360f) angle -= 360f

        while (angle < 0f) angle += 360f

        if (a <= 0f || a >= 180f) {
            angle = if (oldAngle <= 90) {
                0f
            } else {
                180f
            }
        }
        return angle
    }


}
