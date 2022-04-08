package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model

import android.os.Parcelable
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.SatelliteInEarthOrbit
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.SatelliteInGeostationaryOrbit
import kotlinx.parcelize.Parcelize

//если интересно что за Tle - гугли TLE

@Parcelize
data class TleDomainModel(
    val name: String,
    val epoch: Double,
    val meanmo: Double,
    val eccn: Double,
    val incl: Double,
    val raan: Double,
    val argper: Double,
    val meanan: Double,
    val idSatellite: Int,
    val bstar: Double,
    val xincl: Double = Math.toRadians(incl),
    val xnodeo: Double = Math.toRadians(raan),
    val omegao: Double = Math.toRadians(argper),
    val xmo: Double = Math.toRadians(meanan),
    val xno: Double = meanmo * Math.PI * 2.0 / 1440,
    val isDeepspace: Boolean = meanmo < 6.4,
) : Parcelable {

    fun createSat(): Satellite {
        return when {
            this.isDeepspace -> SatelliteInGeostationaryOrbit(this)
            else -> SatelliteInEarthOrbit(this)
        }
    }
}
