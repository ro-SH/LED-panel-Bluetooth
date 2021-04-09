package com.ledpanel.led_panel_control_app.ui.draw

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.ledpanel.led_panel_control_app.R

// Stroke width for the the paint.
private const val STROKE_WIDTH = 1f

// Drawing modes
const val ERASE = 0
const val DRAW = 1

/**
 *  View for drawing on a pixel matrix
 *  @param context
 *  @param rows Number of rows
 *  @param cols Number of cols
 *  @param drawColor Current color
 */
class DrawView(
    context: Context,
    private var rows: Int = 8,
    private var cols: Int = 32,
    private var drawColor: Int = Color.WHITE
) : View(context) {

    private val gridColor = Color.WHITE
    private val bgColor = ResourcesCompat.getColor(resources, R.color.gray_900, null)
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    // Pixel Size
    private var pixelWidth: Int? = null
    private var pixelHeight: Int? = null

    // Current drawing mode
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

    /**
     *  Set new draw mode
     *  @param mode
     */
    fun setDrawMode(mode: Int) {
        if (mode == ERASE || mode == DRAW)
            drawMode = mode
    }

    /**
     *  Set new draw color
     *  @param newColor
     */
    fun setDrawColor(newColor: Int) {
        drawColor = newColor
    }

    /**
     *  Fill the matrix with current color or background color
     *  @param fillColor 'true' if current color
     */
    fun fill(fillColor: Boolean = false) {
        when (fillColor) {
            true -> extraCanvas.drawColor(drawColor)
            else -> extraCanvas.drawColor(bgColor)
        }

        // Bold Grid
        for (i in 1..3)
            drawGrid()

        invalidate()
    }

    /**
     * Called whenever the view changes size.
     * Also called after view has been inflated and has a valid size.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)

        pixelWidth = (extraCanvas.width - cols + 1) / cols
        pixelHeight = (extraCanvas.height - rows + 1) / rows

        fill()
    }

    /**
     * Drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the bitmap that has the saved path.
        canvas?.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    /**
     *  Draw the grid
     */
    private fun drawGrid() {
        // Horizontal
        for (line in 1 until rows)
            extraCanvas.drawLine(
                0F,
                (extraCanvas.height / rows * line).toFloat(),
                extraCanvas.width.toFloat(),
                (extraCanvas.height / rows * line).toFloat(),
                paint
            )
        // Vertical
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
     *  Fill the pixel with current color
     *  @param currentX
     *  @param currentY
     *  @return Pair of coordinates of current pixel
     */
    fun fillPixel(currentX: Float, currentY: Float): Pair<Int, Int>? {

        if (extraCanvas.width % currentX == 0F || extraCanvas.height % currentY == 0F)
            return null

        val pixelX: Int = (currentX / (pixelWidth!! + 1)).toInt()
        val pixelY: Int = (currentY / (pixelHeight!! + 1)).toInt()

        val startX = when (pixelX) {
            cols -> (pixelX - 1) * (pixelWidth!! + 1)
            else -> pixelX * (pixelWidth!! + 1)
        }

        val startY = when (pixelY) {
            rows -> (pixelY - 1) * (pixelHeight!! + 1)
            else -> pixelY * (pixelHeight!! + 1)
        }

        val endX = when (pixelX) {
            cols -> extraCanvas.width
            cols - 1 -> extraCanvas.width
            else -> startX + pixelWidth!!
        }

        val endY = when (pixelY) {
            rows -> extraCanvas.height
            rows - 1 -> extraCanvas.height
            else -> startY + pixelHeight!!
        }
        val rect = Rect(startX, startY, endX, endY)

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

        drawGrid()

        invalidate()
        return Pair(pixelX, pixelY)
    }
}
