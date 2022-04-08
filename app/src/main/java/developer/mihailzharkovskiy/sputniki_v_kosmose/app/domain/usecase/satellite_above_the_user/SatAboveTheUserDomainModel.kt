package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user

import android.os.Parcelable
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import kotlinx.parcelize.Parcelize

@Parcelize
data class SatAboveTheUserDomainModel(
    /** время когда когда спутник появляется над горизонтом**/
    val startTime: Long,
    /** направление от куда спутник появляется над горизонтом**/
    val startAzimuth: Double,
    /** время кода спутник уходит за горизонт**/
    val endTime: Long,
    /** направление в котором спутник уходит за горизонтом**/
    val endAzimuth: Double,
    /** время кода спутник максимально высоко над горизонтом **/
    val centerTime: Long,
    /** направление в котором спутник спутник будет находиться максимально высоко над горизонтом **/
    val centerAzimuth: Double,
    /** высота орбиты спутника**/
    val altitude: Double,
    /** в общем, это то, на сколько градусов нужно задрать голову, чтобы увидеть спутник**/
    val maxElevation: Double,
    val satellite: Satellite,
    var progress: Int = 0,

    val satId: Int = satellite.tle.idSatellite,
    val name: String = satellite.tle.name,
    val isDeepSpace: Boolean = satellite.tle.isDeepSpace,
) : Parcelable {

}