package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import developer.mihailzharkovskiy.sputniki_v_kosmose.R

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var progress: Int = 1
        set(value) {
            field = value.coerceIn(1, 100)
            invalidate()
        }

    private val progressBkgColor: Int = ContextCompat.getColor(context, R.color.heavy_clouds)
    private val progressColor: Int = ContextCompat.getColor(context, R.color.snow)
    private val cornerRadius: Float =
        resources.getDimensionPixelSize(R.dimen.corner_radius).toFloat()
    private val padding = 5f

    private val paintBackground = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = progressBkgColor
    }
    private val paintProgrss = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = progressColor
    }
    private val backgroundRect by lazy { RectF(0f, 0f, width.toFloat(), height.toFloat()) }
    private val progressRect by lazy { RectF(padding, padding, 0f, height - padding) }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawProgress(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paintBackground)
    }

    private fun drawProgress(canvas: Canvas) {
        val progressEndX = padding + ((progress * width) / 100) - padding

        canvas.drawRoundRect(
            progressRect.apply { right = progressEndX },
            cornerRadius,
            cornerRadius,
            paintProgrss
        )
    }
}
