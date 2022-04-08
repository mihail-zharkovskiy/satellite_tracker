package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas

import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource

enum class SideOfWorld(val azimuth: IntRange) {
    NE(22..67),
    E(68..112),
    SE(113..157),
    S(158..202),
    SW(203..247),
    W(248..292),
    NW(293..337);
}

/**@return - возрашает название сотроны света в зависимости от переданного азимута**/
fun Number.azimuthToSideWorld(resource: Resource): String {
    val azimuth = this.toInt()
    return if (azimuth in 0..360) {
        when (azimuth) {
            in SideOfWorld.NE.azimuth -> resource.getString(R.string.ne)
            in SideOfWorld.E.azimuth -> resource.getString(R.string.e)
            in SideOfWorld.SE.azimuth -> resource.getString(R.string.se)
            in SideOfWorld.S.azimuth -> resource.getString(R.string.s)
            in SideOfWorld.SW.azimuth -> resource.getString(R.string.sw)
            in SideOfWorld.W.azimuth -> resource.getString(R.string.w)
            in SideOfWorld.NW.azimuth -> resource.getString(R.string.nw)
            else -> resource.getString(R.string.n)
        }
    } else "error"
}

