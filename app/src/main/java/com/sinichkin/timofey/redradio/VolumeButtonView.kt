package com.sinichkin.timofey.redradio

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt


class VolumeButtonView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    interface OnSliderMovedListener {

        fun onSliderMoved(pos: Double, firstPos: Double, startRotation: Float, clock: Int, quarter:Int)
    }

    private var mCircleCenterX = 0
    private var mCircleCenterY = 0
    private var mCircleRadius = 0
    private var mAngle = 0.0
    private var mListener: OnSliderMovedListener? = null
    private var firstX = 0f
    private var firstY = 0f
    private var firstRot = 0f
    private var secondView: View? = null

    fun setSecondView(view: View?) {
        secondView = view
    }

    fun setOnSliderMovedListener(listener: OnSliderMovedListener) {
        mListener = listener
    }

    private var mVelocityTracker: VelocityTracker? = null
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        this.performClick()
//        val index: Int = ev!!.actionIndex
//        val action: Int = ev.actionMasked
//        val pointerId: Int = ev.getPointerId(index)


        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                firstX = ev.x
                firstY = ev.y
                if (secondView != null) {
                    firstRot = secondView!!.rotation
                }

/////////////////////////////////


                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain()
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker!!.clear()
                }
                // Add a user's movement to the tracker.
                mVelocityTracker!!.addMovement(ev)


            }
            MotionEvent.ACTION_MOVE -> {
                val x = ev.x
                val y = ev.y


                mVelocityTracker!!.addMovement(ev)
                mVelocityTracker!!.computeCurrentVelocity(1000)
                firstX = mVelocityTracker!!.xVelocity
                firstY = mVelocityTracker!!.yVelocity

                Log.d(
                    "DDDD",
                    "X:" + mVelocityTracker!!.xVelocity + "| Y:" + mVelocityTracker!!.yVelocity
                )

                updateSliderState(x, y, firstX, firstY, firstRot)
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                // mVelocityTracker!!.recycle()
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

    private fun getLenghtSegment(x1:Float,y1:Float,x2:Float,y2:Float):Float {

        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

    }





    private fun updateSliderState(
        touchX: Float,
        touchY: Float,
        firstTouchX: Float,
        firstTouchY: Float,
        startRotation: Float
    ) {

        val Xhalf = if (mCircleCenterX < touchX) {
            1
        } else {
            2
        }//540
        val Yhalf = if (mCircleCenterY > touchY) {
            1
        } else {
            2
        }//194
        //41
        //32
        var quarter = 0
        if (Xhalf == 1 && Yhalf == 1) {
            quarter = 1
        }
        if (Xhalf == 1 && Yhalf == 2) {
            quarter = 2
        }
        if (Xhalf == 2 && Yhalf == 2) {
            quarter = 3
        }
        if (Xhalf == 2 && Yhalf == 1) {
            quarter = 4
        }


        Log.d(
            "DRT",
            "touchX:$touchX|touchY:$touchY   |Xhalf:$Xhalf|Yhalf:$Yhalf     |quarter:$quarter"
        )

        var speedX = firstTouchX
        if ((speedX > 0f && speedX < 15f) || (speedX < 0f && speedX > -15f)) {
            speedX = 0f
        }
        var speedY = firstTouchY
        if ((speedY > 0f && speedY < 15f) || (speedY < 0f && speedY > -15f)) {
            speedY = 0f
        }


        var clock = 0   //1 -противчасов и 2 по часам
        var directionX = 0
        var directionY = 0
        when (quarter) {
            1 -> {
                if (speedX < 0f) {
                    directionX = 1
//left
                } else {
                    directionX = 2
//right
                }
                if (speedY > 0f) {
                    directionY = 1
//bottom
                } else {
                    directionY = 2
//top
                }
                if (directionX == 1 && directionY == 1) {
                    clock = 0
                } else if (directionX == 1 && directionY == 2) {
                    clock = 1
                } else if (directionX == 2 && directionY == 1) {
                    clock = 2
                } else if (directionX == 2 && directionY == 2) {
                    clock = 0
                } else if (directionX == 0 && directionY == 1) {
                    clock = 2
                } else if (directionX == 0 && directionY == 2) {
                    clock = 1
                } else if (directionX == 1 && directionY == 0) {
                    clock = 1
                } else if (directionX == 2 && directionY == 0) {
                    clock = 2
                }

            }
            4 -> {
                if (speedX < 0f) {
                    directionX = 1
//left
                } else {
                    directionX = 2
//right
                }
                if (speedY > 0f) {
                    directionY = 1
//bottom
                } else {
                    directionY = 2
//top
                }
                if (directionX == 1 && directionY == 1) {
                    clock = 1
                } else if (directionX == 1 && directionY == 2) {
                    clock = 0
                } else if (directionX == 2 && directionY == 1) {
                    clock = 0
                } else if (directionX == 2 && directionY == 2) {
                    clock = 2
                } else if (directionX == 0 && directionY == 1) {
                    clock = 1
                } else if (directionX == 0 && directionY == 2) {
                    clock = 2
                } else if (directionX == 1 && directionY == 0) {
                    clock = 1
                } else if (directionX == 2 && directionY == 0) {
                    clock = 2
                }


            }
            3 -> {
                if (speedX < 0f) {
                    directionX = 1
//left
                } else {
                    directionX = 2
//right
                }
                if (speedY > 0f) {
                    directionY = 1
//bottom
                } else {
                    directionY = 2
//top
                }
                if (directionX == 1 && directionY == 1) {
                    clock = 0
                } else if (directionX == 1 && directionY == 2) {
                    clock = 2
                } else if (directionX == 2 && directionY == 1) {
                    clock = 1
                } else if (directionX == 2 && directionY == 2) {
                    clock = 0
                } else if (directionX == 0 && directionY == 1) {
                    clock = 1
                } else if (directionX == 0 && directionY == 2) {
                    clock = 2
                } else if (directionX == 1 && directionY == 0) {
                    clock = 2
                } else if (directionX == 2 && directionY == 0) {
                    clock = 1
                }

            }
            2 -> {
                if (speedX < 0f) {
                    directionX = 1
//left
                } else {
                    directionX = 2
//right
                }
                if (speedY > 0f) {
                    directionY = 1
//bottom
                } else {
                    directionY = 2
//top
                }

                if (directionX == 1 && directionY == 1) {
                    clock = 2
                } else if (directionX == 1 && directionY == 2) {
                    clock = 0
                } else if (directionX == 2 && directionY == 1) {
                    clock = 0
                } else if (directionX == 2 && directionY == 2) {
                    clock = 1
                } else if (directionX == 0 && directionY == 1) {
                    clock = 2
                } else if (directionX == 0 && directionY == 2) {
                    clock = 1
                } else if (directionX == 1 && directionY == 0) {
                    clock = 2
                } else if (directionX == 2 && directionY == 0) {
                    clock = 1
                }
            }
        }
        if(speedX==0f && speedY==0f){clock=0}



        Log.d(
            "SRT",
            "clock:$clock | directionX:$directionX | directionY:$directionY | speedX:$speedX| speedY:$speedY"
        )
///////////////////////
        ///////////////////////
val cLength =getLenghtSegment(mCircleCenterX.toFloat(),touchX,mCircleCenterY.toFloat(),touchY)
val bLength =getLenghtSegment(mCircleCenterX.toFloat(),touchX+speedX*0.016f,mCircleCenterY.toFloat(),touchY+speedY*0.016f)
//val aLength =getLenghtSegment(touchX,touchX+speedX,touchY,touchY+speedY)
        val aLength =        getLenghtSegment(touchX,touchX+speedX*0.016f,touchY,touchY+speedY*0.016f)

        val cosalfa =      ( bLength.pow(2)+cLength.pow(2)-aLength.pow(2))/(2*bLength*cLength)
val alfa = acos(cosalfa)


Log.d("ALFFF","aLength:$aLength|bLength:$bLength|cLength:$cLength|alfa:$alfa|mCircleCenterX:$mCircleCenterX|mCircleCenterY:$mCircleCenterY|speedX:$speedX|speedY:$speedY|touchX$touchX|touchY:$touchY")



////////////////////////////
        ///////////////////////









        mAngle = calculateAngle(touchX, touchY)
        val mAngle2 = calculateAngle(firstTouchX, firstTouchY)

        // notify slider moved listener of the new position which should be in [0..1] range
        this.mListener!!.onSliderMoved(alfa.toDouble(), mAngle2, startRotation, clock,quarter)
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
        return localmAngle / (2 * Math.PI)
    }

    fun convertClockToPosition(clock: Float): Float {
        var angle = clock * -360f
//        while (angle < 0f) {
//            angle += 180f
//        }

        return angle
    }

    fun roundAngle(a: Float, oldAngle: Float, clock: Int,quarter:Int): Float {
        var angle = a

        if (clock == 0) {
            return oldAngle
        }




//        if (oldAngle > 170f) {
//            if (clock == 2 && a in 180f..360f) {
//                return 180f
//            }
//        } else if (oldAngle < 10f) {
//            if (clock == 1 && a in 180f..360f) {
//                return 0f
//            }
//        }

//DRT
//            if (oldAngle in 0f..90f && (a <= 0f || a >= 180f)) {
//                angle = 0f
//            } else if (oldAngle in 90f..180f && (a <= 0f || a >= 180f)){//178/ 313
//                angle = 180f
//            }
//            else if (oldAngle == 0f && a !in 180f..360f){
//                angle = 0fЯ котлин
//            }else if (oldAngle == 180f && a !in 160f..180f){
//                angle = 180f
//            }

        Log.d("RND", "a:$a | oldAngle: $oldAngle | angle:$angle| clock:$clock")
        return angle
    }


}
