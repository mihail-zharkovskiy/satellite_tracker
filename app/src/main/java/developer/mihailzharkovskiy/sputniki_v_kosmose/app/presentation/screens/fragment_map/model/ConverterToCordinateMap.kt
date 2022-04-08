package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model

import kotlin.math.max
import kotlin.math.min

/**ЭТИ ФУНКЦИИ НУЖНЫ.НУЖНО ДУМАТЬ КУДА ИХ ПРИБРАТЬ**/

fun convertLatitudeForMap(latitude: Double): Double {
    return convertCoordinate(latitude, -85.05, 85.05)
}

fun convertLongitudeForMap(longitude: Double): Double {
    var result = longitude
    while (result < -180.0) result += 360.0
    while (result > 180.0) result -= 360.0
    return convertCoordinate(result, -180.0, 180.0)
}

private fun convertCoordinate(currentValue: Double, minValue: Double, maxValue: Double): Double {
    return min(max(currentValue, minValue), maxValue)
}
