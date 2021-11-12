package rs.dk150.cryptotracker.ui

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.HistoricalList
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Class for drawing graph on canvas
 */
class GraphDrawer(val context: Context?) {

    private var width: Float = 0.0f
    private var height: Float = 0.0f
    private var newHeight: Float = 0.0f
    private var minVal: Float = 0.0f
    private var oneFifth: Float = 0.0f
    private var xInc: Float = 0.0f
    private var yInc: Float = 0.0f

    fun draw(canvas: Canvas, values: List<HistoricalList.Value>, xPoints: Int) {
        context?.let {
            // blank white canvas to begin width
            canvas.drawColor(Color.WHITE)
            // make list contain only needed (time, value) pairs to draw
            var list = values.toMutableList()
                .filter { value -> (value.close != null && value.time != null) }
            var size = values.size
            if (list.size < 2) {
                return
            }
            if (xPoints < size) {
                list = list.subList(size - xPoints, size)
                size = list.size
            }
            // calculate width and height
            width = canvas.width.toFloat()
            height = canvas.height.toFloat()
            xInc = (width - 2) / (size - 1)
            yInc = (height - 70 - 2) / 5
            newHeight = getNewHeight(list)

            drawBorder(it, canvas)

            drawXAxis(it, canvas)

            drawGraph(it, list, size, canvas)

            drawGrid(it, canvas)

            drawYValues(it, canvas)

            drawXValues(it, canvas, list, size, xPoints)
        }
    }

    private fun getNewHeight(values: List<HistoricalList.Value>): Float {
        var max = Float.MIN_VALUE
        var min = Float.MAX_VALUE
        for (v in values) {
            v.close?.let { it ->
                if (it > max) {
                    max = it
                }
                if (it < min) {
                    min = it
                }
            }
        }
        oneFifth = 1f / 3 * (max - min)
        minVal = min - oneFifth
        val maxVal = max + oneFifth
        val newHeight = maxVal - minVal
        return if (newHeight == 0f) {
            height - 70 - 2 - 3
        } else {
            newHeight
        }
    }

    private fun drawBorder(
        context: Context,
        canvas: Canvas
    ) {
        val strokePaint = Paint()
        strokePaint.color = context.getColor(R.color.light_black)
        strokePaint.strokeWidth = 1f
        strokePaint.style = Paint.Style.STROKE
        canvas.drawRect(
            0f,
            0f,
            width - 1,
            height - 1,
            strokePaint
        )
    }

    private fun drawXAxis(context: Context, canvas: Canvas) {
        val strokePaint = Paint()
        strokePaint.color = context.getColor(R.color.light_black)
        strokePaint.strokeWidth = 1f
        strokePaint.style = Paint.Style.STROKE
        canvas.drawLine(
            1f,
            height - 70 - 1,
            width - 1,
            height - 70 - 1,
            strokePaint
        )
    }

    private fun drawGraph(
        context: Context,
        list: List<HistoricalList.Value>,
        size: Int,
        canvas: Canvas
    ) {
        // draw graph line & fill
        val strokePaint = Paint()
        strokePaint.color = context.getColor(R.color.orangeDark)
        strokePaint.strokeWidth = 3f
        val fillPaint = Paint()
        fillPaint.shader = LinearGradient(
            1f,
            1f,
            1f,
            height - 70,
            Color.parseColor("#FFfba555"),
            Color.WHITE,
            Shader.TileMode.CLAMP
        )
        fillPaint.style = Paint.Style.FILL
        strokePaint.style = Paint.Style.STROKE
        val strokePath = Path()
        val fillPath = Path()
        strokePath.fillType = Path.FillType.EVEN_ODD
        var i = 0
        var prevHigh = getY(list[i++].close!!)
        strokePath.moveTo(1f, prevHigh)
        fillPath.moveTo(1f, height - 70 - 1)
        fillPath.lineTo(1f, prevHigh)
        while (i < size) {
            val y = getY(list[i].close!!)
            if (y != prevHigh) {
                val xPrev = getX(i - 1)
                strokePath.lineTo(xPrev, prevHigh)
                fillPath.lineTo(xPrev, prevHigh)
                val x = getX(i)
                strokePath.lineTo(x, y)
                fillPath.lineTo(x, y)
                prevHigh = y
            } else if (i == size - 1) {
                val x = getX(i)
                strokePath.lineTo(x, prevHigh)
                fillPath.lineTo(x, prevHigh)
            }
            i++
        }
        fillPath.lineTo(getX(i - 1), height - 70 - 1)
        fillPath.close()
        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(strokePath, strokePaint)
    }

    private fun getX(i: Int): Float = i * xInc + 1

    private fun getY(y: Float): Float {
        // yVal = yValue - minVal
        // yVal : newHeight = yPointInverse : height
        // yPointInverse = yVal * height / newHeight
        // yPoint = height - yPointInverse
        val h = (height - 70 - 2 - 3)
        return if (h == newHeight) {
            h / 5 + 1 + 1.5f
        } else {
            val yVal = y - minVal
            val yPointInverse = yVal * h
            val yPointInverseScaled = yPointInverse / newHeight
            val yPoint = h - yPointInverseScaled
            yPoint + 1 + 1.5f
            //h - (y - minVal) * h / newHeight + 1 + 1.5f
        }
    }

    private fun drawGrid(
        context: Context,
        canvas: Canvas,
    ) {
        val strokePaint = Paint()
        strokePaint.color = context.getColor(R.color.light_black_opaque)
        strokePaint.strokeWidth = 1f
        for (j in 1..4) {
            canvas.drawLine(1f, yInc * j, width - 1, yInc * j, strokePaint)
        }
        var sum = yInc
        do {
            canvas.drawLine(sum, 1f, sum, height - 70 - 1, strokePaint)
            sum += yInc
        } while (sum < width - 1)
    }

    private fun drawYValues(
        context: Context,
        canvas: Canvas
    ): TextPaint {
        val textPaint = TextPaint()
        textPaint.color = context.getColor(R.color.orangeDark)
        textPaint.isAntiAlias = true
        textPaint.textSize = 11 * context.resources.displayMetrics.density
        textPaint.isFakeBoldText = true
        val backgroundPaint = Paint()
        backgroundPaint.color = Color.parseColor("#FFffdfcc")
        backgroundPaint.style = Paint.Style.FILL
        val range = if (height - 70 - 2 - 3 == newHeight) {
            1..1
        } else {
            1..5
        }
        for (j in range) {
            val text = (minVal + (5 - j) * oneFifth).toString()
            val x = 8f
            val y = yInc * j - 6f
            canvas.drawRect(getTextBackgroundSize(x, y, text, textPaint), backgroundPaint)
            canvas.drawText(text, x, y, textPaint)
        }
        return textPaint
    }

    private fun getTextBackgroundSize(x: Float, y: Float, text: String, paint: TextPaint): Rect {
        val fontMetrics = paint.fontMetrics
        val halfTextLength = paint.measureText(text)
        return Rect(
            x.toInt() - 3,
            (y + fontMetrics.top).toInt(),
            (x + halfTextLength + 3).toInt(),
            (y + fontMetrics.bottom).toInt()
        )
    }

    private fun drawXValues(
        context: Context,
        canvas: Canvas,
        list: List<HistoricalList.Value>,
        size: Int,
        xPoints: Int
    ) {
        val strokePaint = Paint()
        strokePaint.color = context.getColor(R.color.light_black)
        strokePaint.strokeWidth = 1f
        strokePaint.style = Paint.Style.STROKE
        val textPaint = TextPaint()
        textPaint.color = context.getColor(R.color.orangeDark)
        textPaint.isAntiAlias = true
        textPaint.textSize = 11 * context.resources.displayMetrics.density
        textPaint.isFakeBoldText = true
        xInc = (width - 2) / 4
        for (j in 1..3) {
            val x = j * xInc
            canvas.drawLine(x, height - 70, x, height - 70 + 30, strokePaint)
            val instant = Instant.ofEpochSecond((list[size / 4 * j]).time!!)
            // parse the time zone
            val zoneId = ZoneId.systemDefault()
            // define a formatter using the given pattern and a Locale
            val formatter = DateTimeFormatter.ofPattern("MMM d  HH:mm", Locale.ENGLISH)
            // then make the moment in time consider the zone and return the formatted String
            val date = instant.atZone(zoneId).format(formatter)
            val text = when (xPoints) {
                24, 60, 180, 1440 -> {
                    date.split(" ")[3]
                }
                72, 168 -> {
                    date
                }
                else -> {
                    val s = date.split(" ")
                    "${s[0]} ${s[1]}"
                }
            }
            canvas.drawText(text, x - textPaint.measureText(text) / 2, height - 10, textPaint)
        }
    }
}