package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention.toDp
import kotlin.random.Random

class ViewStarrySky @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private inner class Coordinate(val x: Float, val y: Float)

    private var cornerRadius = 0.toDp()

    fun renderCornerRadius(radius: Int) {
        cornerRadius = radius.toDp()
    }

    private val colorBackground = ContextCompat.getColor(context, R.color.heavy_clouds)
    private val colorStars = ContextCompat.getColor(context, R.color.snow)

    private val maxCountStars by lazy { ((width * height) * /*0.1*/0.08 / 100).toInt() } //0.1 сколтко в процентном соотношении хотим чтобы звезды занмали прлощади
    private val maxRadiusStar = 3
    private val minRadiusStar = 1
    private val spisokKordinatzvezd: MutableList<Coordinate> by lazy { generateCoordinatesStars() }
    private val spisokWithRadiusStars: MutableList<Float> by lazy { generateDimensionsStars() }

    private val backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = colorBackground
    }

    private val starPaint = Paint().apply {
        style = Paint.Style.FILL
        color = colorStars
    }

    private val backgroundRect by lazy { RectF(0f, 0f, width.toFloat(), height.toFloat()) }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawStars(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)
    }

    private fun drawStars(canvas: Canvas) {
        spisokKordinatzvezd.forEachIndexed { index, valie ->
            canvas.drawCircle(valie.x, valie.y, spisokWithRadiusStars[index].toDp() / 2, starPaint)
        }
    }

    private fun generateCoordinatesStars(): MutableList<Coordinate> {
        val coordinates = mutableListOf<Coordinate>()
        var countStars = maxCountStars
        while (countStars != 0) {
            val x = Random.nextInt(0, width).toFloat()
            val y = Random.nextInt(0, height).toFloat()
            coordinates.add(Coordinate(x, y))
            countStars -= 1
        }
        return coordinates
    }

    private fun generateDimensionsStars(): MutableList<Float> {
        val spisokRazmerov = mutableListOf<Float>()
        var countStars = maxCountStars
        while (countStars != 0) {
            val radiusStars = Random.nextInt(minRadiusStar, maxRadiusStar).toFloat()
            spisokRazmerov.add(radiusStars)
            countStars -= 1
        }
        return spisokRazmerov
    }
}