package com.ledpanel.led_panel_control_app.ui.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.ledpanel.led_panel_control_app.R
import kotlin.math.abs

// Stroke width for the the paint.
private const val STROKE_WIDTH = 1f

const val ERASE = 0
const val DRAW = 1

class DrawView(context: Context,
               private var rows: Int = 8,
               private var cols: Int = 32,
               private var drawColor: Int = Color.WHITE
) : View(context) {

    private var path = Path()

    private val gridColor = Color.WHITE
    private val bgColor = ResourcesCompat.getColor(resources, R.color.gray_900, null)
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var pixelWidth: Int? = null
    private var pixelHeight: Int? = null

    private var currentX = 0F
    private var currentY = 0F

    private var drawMode = DRAW

    // Set up the paint with which to draw.
    private var paint = Paint().apply {
        color = gridColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    fun setDrawMode(mode: Int) {
        if (mode == ERASE || mode == DRAW)
            drawMode = mode
    }

    fun setDrawColor(newColor: Int) {
        drawColor = newColor
    }

    fun fill(fillColor: Boolean = false) {
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        when (fillColor) {
            true -> extraCanvas.drawColor(drawColor)
            else -> extraCanvas.drawColor(bgColor)
        }
        drawGrid()

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        fill()
        pixelWidth = (extraCanvas.width - cols + 1) / cols
        pixelHeight = (extraCanvas.height - rows + 1) / rows
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the bitmap that has the saved path.
        canvas?.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    private fun drawGrid() {
        for (line in 1 until rows)
            extraCanvas.drawLine(
                    0F,
                    (extraCanvas.height / rows * line).toFloat(),
                    extraCanvas.width.toFloat(),
                    (extraCanvas.height / rows * line).toFloat(),
                    paint
            )

        for (line in 1 until cols)
            extraCanvas.drawLine(
                    (extraCanvas.width / cols * line).toFloat(),
                    0F,
                    (extraCanvas.width / cols * line).toFloat(),
                    extraCanvas.height.toFloat(),
                    paint
            )
    }

    /**
     * No need to call and implement MyCanvasView#performClick, because MyCanvasView custom view
     * does not handle click actions.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentX = event.x
        currentY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> fillPixel()
            MotionEvent.ACTION_MOVE -> fillPixel()
        }
        return true
    }

    private fun fillPixel() {
        if (extraCanvas.width % currentX == 0F || extraCanvas.height % currentY == 0F)
            return

        val pixelX: Int = (currentX / (pixelWidth!! + 1)).toInt()
        val pixelY: Int = (currentY / (pixelHeight!! + 1)).toInt()

        val startX = pixelX * (pixelWidth!! + 1)
        val startY = pixelY * (pixelHeight!! + 1)
        val rect = Rect(
                startX,
                startY,
                startX + pixelWidth!!,
                startY + pixelHeight!!)

        when (drawMode) {
            DRAW -> {
                extraCanvas.drawRect(
                        rect,
                        Paint().apply {
                            color = drawColor
                        }
                )
            }

            ERASE -> {
                extraCanvas.drawRect(
                        rect,
                        Paint().apply {
                            color = bgColor
                        }
                )
            }
        }

        invalidate()
    }
}