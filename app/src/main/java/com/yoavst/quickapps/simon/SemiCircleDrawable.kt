package com.yoavst.quickapps.simon

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable

public class SemiCircleDrawable(color: Int = Color.BLUE, private val angle: SemiCircleDrawable.Direction = SemiCircleDrawable.Direction.LEFT) : Drawable() {

    private val paint: Paint
    private val rectF: RectF
    public var color: Int = 0
        private set(color: Int) {
            $color = color
            paint.setColor($color)
        }

    public enum class Direction {
        LEFT
        RIGHT
        TOP
        BOTTOM
    }

    init {
        paint = Paint()
        paint.setStyle(Paint.Style.FILL)
        rectF = RectF()
        this.color = color
    }


    override fun draw(canvas: Canvas) {
        canvas.save()

        val bounds = getBounds()

        if (angle == Direction.LEFT || angle == Direction.RIGHT) {
            canvas.scale(2F, 1F)
            if (angle == Direction.RIGHT) {
                canvas.translate((-(bounds.right / 2)).toFloat(), 0F)
            }
        } else {
            canvas.scale(1F, 2F)
            if (angle == Direction.BOTTOM) {
                canvas.translate(0F, (-(bounds.bottom / 2)).toFloat())
            }
        }


        rectF.set(bounds)

        if (angle == Direction.LEFT)
            canvas.drawArc(rectF, 90F, 180F, true, paint)
        else if (angle == Direction.TOP)
            canvas.drawArc(rectF, -180F, 180F, true, paint)
        else if (angle == Direction.RIGHT)
            canvas.drawArc(rectF, 270F, 180F, true, paint)
        else if (angle == Direction.BOTTOM)
            canvas.drawArc(rectF, 0F, 180F, true, paint)
    }

    override fun setAlpha(alpha: Int) {
        // Has no effect
    }

    override fun setColorFilter(cf: ColorFilter) {
        // Has no effect
    }

    override fun getOpacity(): Int {
        // Not Implemented
        return 0
    }

}
