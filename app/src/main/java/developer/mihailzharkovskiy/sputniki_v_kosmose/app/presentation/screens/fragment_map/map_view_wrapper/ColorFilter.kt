package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.map_view_wrapper

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

/**
 * textview.background.colorFilter = myColorFilter(), (так можно делать фильтры на фото)
 * подробнее о реализации: https://medium.com/squaer-corner-blog/welcomr-to-the-color-matrix-64d112e3f43d
 * **/
internal fun MapViewWrapper.getColorFilter(colorForFilter: String = "#1D3245"): ColorMatrixColorFilter {
    val invertedMatrix = ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    val myColor = Color.parseColor(colorForFilter)
    val lr = (255.0f - Color.red(myColor)) / 255.0f
    val lg = (255.0f - Color.green(myColor)) / 255.0f
    val lb = (255.0f - Color.blue(myColor)) / 255.0f

    val myMatrix = ColorMatrix(
        floatArrayOf(
            lr, lg, lb, 0f, 0f,
            lr, lg, lb, 0f, 0f,
            lr, lg, lb, 0f, 0f,
            0f, 0f, 0f, 0f, 255f
        )
    )
    myMatrix.preConcat(invertedMatrix)
    val dr = Color.red(myColor)
    val dg = Color.green(myColor)
    val db = Color.blue(myColor)

    val scale = 1f - 0.3f
    val scaleMatrix = ColorMatrix(
        floatArrayOf(
            scale, 0f, 0f, 0f, dr * 0.9f,
            0f, scale, 0f, 0f, dg * 0.9f,
            0f, 0f, scale, 0f, db * 0.9f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    scaleMatrix.preConcat(myMatrix)
    return ColorMatrixColorFilter(scaleMatrix)
}
