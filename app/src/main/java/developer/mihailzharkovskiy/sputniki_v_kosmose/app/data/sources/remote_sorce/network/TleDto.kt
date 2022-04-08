package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TleDto(
    @SerializedName("OBJECT_NAME") val objectName: String,
    @SerializedName("OBJECT_ID") val objectID: String,
    @SerializedName("EPOCH") val epoch: String,
    @SerializedName("MEAN_MOTION") val meanMotion: Double,
    @SerializedName("ECCENTRICITY") val eccentricity: Double,
    @SerializedName("INCLINATION") val inclination: Double,
    @SerializedName("RA_OF_ASC_NODE") val raOfAscNode: Double,
    @SerializedName("ARG_OF_PERICENTER") val argOfPericenter: Double,
    @SerializedName("MEAN_ANOMALY") val meanAnomaly: Double,
    @SerializedName("EPHEMERIS_TYPE") val ephemerisType: Long,
    @SerializedName("CLASSIFICATION_TYPE") val classificationType: String,
    @SerializedName("NORAD_CAT_ID") val noradCatID: Long,
    @SerializedName("ELEMENT_SET_NO") val elementSetNo: Long,
    @SerializedName("REV_AT_EPOCH") val revAtEpoch: Long,
    @SerializedName("BSTAR") val bstar: Double,
    @SerializedName("MEAN_MOTION_DOT") val meanMotionDot: Double,
    @SerializedName("MEAN_MOTION_DDOT") val meanMotionDdot: String,
)