package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.mapper

import androidx.annotation.Keep
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.TleEntity
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.TleDto

@Keep
fun TleDto.mapToEntity(): TleEntity {
    //    try {
    val name = this.objectName
    val year = this.epoch.substring(0, 4)
    val month = this.epoch.substring(5, 7)
    val dayOfMonth = this.epoch.substring(8, 10)
    val day = getDayOfYear(year.toInt(), month.toInt(), dayOfMonth.toInt())
    val hour = this.epoch.substring(11, 13).toInt() * 3600000000 // microseconds in one hour
    val min = this.epoch.substring(14, 16).toInt() * 60000000 // microseconds in one minute
    val sec = this.epoch.substring(17, 19).toInt() * 1000000 // microseconds in one second
    val microsec = this.epoch.substring(20, 26).toInt()
    val frac = ((hour + min + sec + microsec) / 86400000000.0).toString()
    val epoch = "${year.substring(2)}$day${frac.substring(1)}".toDouble()
    val meanmo = this.meanMotion
    val eccn = this.eccentricity
    val incl = this.inclination
    val raan = this.raOfAscNode
    val argper = this.argOfPericenter
    val meanan = this.meanAnomaly
    val catnum = this.noradCatID.toInt()
    val bstar = this.bstar
    return TleEntity(name, epoch, meanmo, eccn, incl, raan, argper, meanan, catnum, bstar)
//    } catch (exception: Exception) {
//        return null
//    }
}

@Keep
private fun getDayOfYear(year: Int, month: Int, dayOfMonth: Int): String {
    if (month == 1) return "0$dayOfMonth"
    val daysArray = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var dayOfYear = dayOfMonth
    // If leap year increment Feb days
    if (((year / 4 == 0) && (year / 100 != 0)) || (year / 400 == 0)) daysArray[1]++
    for (i in 0 until month - 1) {
        dayOfYear += daysArray[i]
    }
    /**ноль этот нужен обязательно если номер дня меньше ста**/
    return if (dayOfYear < 100) "0$dayOfYear" else dayOfYear.toString()
}