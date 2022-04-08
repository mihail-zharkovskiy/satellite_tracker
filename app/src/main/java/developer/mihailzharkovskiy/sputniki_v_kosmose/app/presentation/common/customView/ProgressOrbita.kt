package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.customView

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ProgressOrbita @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var progress: Float = 87f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }
    var visibleSatellites: Boolean = true

    fun starAnimation() = animatorStrokeSatellite.start()
    fun stopAnimation() = animatorStrokeSatellite.cancel()

    private inner class Coordinates(val x: Float, val y: Float)

    private var sputnikColor: Int = ContextCompat.getColor(context, R.color.red)
    private var starsColor: Int = ContextCompat.getColor(context, R.color.white)
    private var progressColor: Int = ContextCompat.getColor(context, R.color.white)
    private var transparentColor: Int = ContextCompat.getColor(context, R.color.prozrachniy)
    private var backgroundView: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.background_dark)
    private var strokeSatelliteColor: Int =
        ContextCompat.getColor(context, R.color.red_okruzhnost_sptnika_in_custom_view)

    /**если переносить на обчную окружность,то стартовый угол был бы = 275° (связоно с особенностями классов отыечаюших за рисования окружностей и тд)**/
    private val startAnglBackground = 185f
    private val sweepAnglBackground = 170f

    private val widthBackground = 7f
    private val widthProgress = 7f
    private val widthSatellite = 15f

    private val padding = widthSatellite * 2

    private val radius: Float by lazy { (if (height < width) height else width) - padding }
    private val centerX: Float by lazy { width / 2f }
    private val centerY = 0.0f

    private val leftPointArc: Float by lazy { centerX - radius }
    private val rightPointArc: Float by lazy { centerX + radius }
    private val topPointArc: Float by lazy { centerY + padding + padding } //убери последний padding чтобы передвинуть дугу
    private val bottomPointArc: Float by lazy { radius * 2 + padding }  //и здесь тогда ттоже нужно будет убрать

    private val maxNumberStars = 100
    private val maxRadiusStar = 5
    private val minRadiusStar = 1
    private val coordinatesStars: MutableList<Coordinates> by lazy { generateCoordinatesStars() }

    /**список со значениями радусов каждой звезды**/
    private val radiiOfStars: MutableList<Float> by lazy { generateDimensionsStars() }

    private var widthStrokeSatellite = 0f
    private val animatorStrokeSatellite = ValueAnimator.ofFloat(widthSatellite, widthSatellite * 2)

    private val pathBackgroundProgress = Path()
    private val pathProgress = Path()
    private val oval by lazy {
        RectF(
            leftPointArc,
            topPointArc,
            rightPointArc,
            bottomPointArc
        )
    }

    private val starPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = starsColor
    }
    private val satellitePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = sputnikColor
    }
    private val strokeSatellitePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = strokeSatelliteColor
    }
    private val backgroundArcPaint = Paint().apply {
        style = Paint.Style.STROKE // Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        color = progressColor
        strokeWidth = widthBackground
        strokeCap = Paint.Cap.ROUND
    }
    private val progressPaint = Paint()


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        this.background = backgroundView
        animStrokeSatellite()
    }

    override fun onDraw(canvas: Canvas) {
        drawStars(canvas)
        drawArcBackground(canvas)
        if (visibleSatellites) {
            drawArcProgress(canvas)
            drawSatellite(canvas)
            drawStrokeSatellite(canvas)
        }
    }

    /**@return - возращает угол в градусах
     * при помощи этого угла и радиуса можно узнать где на окружности нажодится точка
     * **/
    private fun convertProgressToDegrees(progress: Float): Double {
        val maxAngleArc = sweepAnglBackground
        return startAnglBackground + (maxAngleArc / 100.0) * progress
    }

    private fun calculateCoordinatesSatellitesOnArc(): Coordinates {
        /**крайний левый угол у полуокружности это 180 => мои улы будут идти от 180 до 360°**/
        val angle = Math.toRadians(convertProgressToDegrees(progress))
        val centerY = radius
        val centerX = width / 2f
        val x = centerX + radius * cos(angle).toFloat()
        val y = centerY + radius * sin(angle).toFloat()
        return Coordinates(x, y)
    }

    private fun animStrokeSatellite() {
        animatorStrokeSatellite.apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                widthStrokeSatellite = it.animatedValue as Float
                invalidate()
            }
        }
    }

    private fun generateCoordinatesStars(): MutableList<Coordinates> {
        val coordinates = mutableListOf<Coordinates>()
        var countStars = maxNumberStars
        while (countStars != 0) {
            val x = Random.nextInt(0, width).toFloat()
            val y = Random.nextInt(0, height).toFloat()
            coordinates.add(Coordinates(x, y))
            countStars -= 1
        }
        return coordinates
    }

    private fun generateDimensionsStars(): MutableList<Float> {
        val dimensions = mutableListOf<Float>()
        var countStars = maxNumberStars
        while (countStars != 0) {
            val radiusStars = Random.nextInt(minRadiusStar, maxRadiusStar).toFloat()
            dimensions.add(radiusStars)
            countStars -= 1
        }
        return dimensions
    }

    private fun drawStars(canvas: Canvas) {
        coordinatesStars.forEachIndexed { index, coordinate ->
            canvas.drawCircle(coordinate.x, coordinate.y, radiiOfStars[index], starPaint)
        }
    }

    private fun drawStrokeSatellite(canvas: Canvas) {
        val coordinate = calculateCoordinatesSatellitesOnArc()
        canvas.drawCircle(
            coordinate.x,
            coordinate.y + topPointArc,
            widthStrokeSatellite,
            strokeSatellitePaint
        )
    }

    private fun drawSatellite(canvas: Canvas) {
        val coordinate = calculateCoordinatesSatellitesOnArc()
        canvas.drawCircle(
            coordinate.x,
            coordinate.y + topPointArc,
            widthSatellite,
            satellitePaint
        )
    }

    private fun drawArcProgress(canvas: Canvas) {
        val coordinates = calculateCoordinatesSatellitesOnArc()
        val lengthArc = 155f
        progressPaint.apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            shader = LinearGradient(
                coordinates.x - lengthArc,
                height.toFloat(),
                coordinates.x - widthSatellite,
                height.toFloat(),
                transparentColor,
                sputnikColor,
                Shader.TileMode.CLAMP
            )
            strokeWidth = widthProgress
            strokeCap = Paint.Cap.ROUND
        }
        pathProgress.apply {
            rewind()
            addArc(
                oval,
                startAnglBackground + (sweepAnglBackground * progress) / 200f,
                (sweepAnglBackground * progress) / 200f
            )
        }
        canvas.drawPath(pathProgress, progressPaint)
    }

    private fun drawArcBackground(canvas: Canvas) {
        pathBackgroundProgress.apply { addArc(oval, startAnglBackground, sweepAnglBackground) }
        canvas.drawPath(pathBackgroundProgress, backgroundArcPaint)
    }
}
