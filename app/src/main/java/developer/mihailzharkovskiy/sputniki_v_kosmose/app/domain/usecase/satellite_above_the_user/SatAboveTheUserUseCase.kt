package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.di.module.dispatchers.DefaultDispatcher
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.SatelliteRepository
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.*
import javax.inject.Inject

class SatAboveTheUserUseCase @Inject constructor(
    private val repository: SatelliteRepository,
    private val userLocationSource: UserLocationSource,
    @DefaultDispatcher private val dispatcherDefault: CoroutineDispatcher,
) {
    /**на примере hoursAhead = 3 т.е пролеты спутников буду расчитывать на ближайшие три часа**/
    private val hoursAhead = 1

    /**минимальная возвышение на которое спутник поднимается над горизонтом,при котормо мы добавляем его в список для отслеживания **/
    private val minElevation: Double = 16.0

    private var userLocation = userLocationSource.getUserLocation()

    private var calculateProgressJob: Job? = null

    private val _satAboveTheUser =
        MutableSharedFlow<DataState<List<SatAboveTheUserDomainModel>>>(replay = 0)
    val satAboveTheUser: SharedFlow<DataState<List<SatAboveTheUserDomainModel>>> =
        _satAboveTheUser.asSharedFlow()


    suspend fun calculateData() {
        _satAboveTheUser.emit(DataState.loading())
        userLocationSource.getUserLocation()
        userLocation = userLocationSource.getUserLocation()
        withContext(dispatcherDefault) {
            val satellites = repository.getSelectedSatellitesForCalculation()
            val satellitesAboveTheUser =
                calculationSatellitesAboveTheUserData(satellites, userLocation, Date().time)
            calculateProgress(satellitesAboveTheUser)
        }
    }

    private suspend fun calculationSatellitesAboveTheUserData(
        satellites: List<Satellite>,
        userLocation: Coordinates,
        nowTime: Long,
    ): List<SatAboveTheUserDomainModel> {
        return withContext(dispatcherDefault) {
            val allPasses = mutableListOf<SatAboveTheUserDomainModel>()
            satellites.forEach { satellite ->
                allPasses.addAll(getSatAboveTheUser(satellite,
                    userLocation,
                    nowTime))
            }
            allPasses.filterSatAboveTheUser(nowTime, hoursAhead, minElevation)
        }
    }

    private suspend fun calculateProgress(satellites: List<SatAboveTheUserDomainModel>) {
        calculateProgressJob?.cancelAndJoin()
        calculateProgressJob = coroutineScope {
            launch(dispatcherDefault) {
                while (isActive) {
                    val timeNow = System.currentTimeMillis()
                    satellites.forEach { satellite ->
                        if (!satellite.isDeepSpace) {
                            if (timeNow > satellite.startTime) {
                                val deltaNow = timeNow.minus(satellite.startTime).toFloat()
                                val deltaTotal =
                                    satellite.endTime.minus(satellite.startTime).toFloat()
                                satellite.progress = ((deltaNow / deltaTotal) * 100).toInt()
                            }
                        }
                    }
                    val result = satellites.asSequence()
                        .filter { it.progress < 100 }
                        .sortedBy { it.isDeepSpace }
                        .toList()
                    if (satellites.isEmpty()) _satAboveTheUser.emit(DataState.empty())
                    else _satAboveTheUser.emit(DataState.succes(result))

                    delay(2000)
                }
            }
        }
    }

    private fun getSatAboveTheUser(
        satellite: Satellite,
        userLocation: Coordinates,
        nowDate: Long,
    ): List<SatAboveTheUserDomainModel> {
        val passes = mutableListOf<SatAboveTheUserDomainModel>()
        val endDate = nowDate + hoursAhead * 60L * 60L * 1000L
        var nowTime = nowDate
        var statrDate: Long
        val quarterOrbitMin = (satellite.orbitalPeriod / 4.0).toInt()
        var shouldRewind = true
        var count = 0
        if (satellite.willBeSeen(userLocation)) {
            if (satellite.tle.isDeepspace) {
                passes.add(getGeoPass(satellite, userLocation, nowTime))
            } else {
                do {
                    if (count > 0) shouldRewind = false
                    val pass = getLeoPass(satellite, userLocation, nowTime, shouldRewind)
                    statrDate = pass.startTime
                    passes.add(pass)
                    nowTime = pass.endTime + (quarterOrbitMin * 3) * 60L * 1000L
                    count++
                } while (statrDate < endDate)
            }
        }
        return passes
    }

    private fun List<SatAboveTheUserDomainModel>.filterSatAboveTheUser(
        time: Long,
        hoursAhead: Int,
        minElev: Double,
    ): List<SatAboveTheUserDomainModel> {
        val timeFuture = time + (hoursAhead * 60L * 60L * 1000L)
        return this.filter { it.endTime > time }
            .filter { it.startTime < timeFuture }
            .filter { it.maxElevation > minElev }
            .sortedBy { it.startTime }
    }

    private fun getGeoPass(
        userPos: Satellite,
        userLocation: Coordinates,
        time: Long,
    ): SatAboveTheUserDomainModel {
        val satPos = userPos.getPosition(userLocation, time)
        val aos = time - 24 * 60L * 60L * 1000L
        val los = time + 24 * 60L * 60L * 1000L
        val tca = (aos + los) / 2
        val az = Math.toDegrees(satPos.azimuth)
        val elev = Math.toDegrees(satPos.elevation)
        val alt = satPos.altitude
        return SatAboveTheUserDomainModel(aos, az, los, az, tca, az, alt, elev, userPos)
    }

    private fun getLeoPass(
        sat: Satellite,
        userPos: Coordinates,
        time: Long,
        rewind: Boolean,
    ): SatAboveTheUserDomainModel {
        val quarterOrbitMin = (sat.orbitalPeriod / 4.0).toInt()
        var calendarTimeMillis = time
        var elevation: Double
        var maxElevation = 0.0
        var alt = 0.0
        var tcaAz = 0.0

        if (rewind) calendarTimeMillis += -quarterOrbitMin * 60L * 1000L

        var satPos = sat.getPosition(userPos, calendarTimeMillis)
        if (satPos.elevation > 0.0) {
            do {
                calendarTimeMillis += 30 * 1000L
                satPos = sat.getPosition(userPos, calendarTimeMillis)
            } while (satPos.elevation > 0.0)
            calendarTimeMillis += quarterOrbitMin * 3 * 60L * 1000L
        }

        do {
            calendarTimeMillis += 60L * 1000L
            satPos = sat.getPosition(userPos, calendarTimeMillis)
            elevation = satPos.elevation
            if (elevation > maxElevation) {
                maxElevation = elevation
                alt = satPos.altitude
                tcaAz = Math.toDegrees(satPos.azimuth)
            }
        } while (satPos.elevation < 0.0)

        calendarTimeMillis += -60L * 1000L
        do {
            calendarTimeMillis += 3L * 1000L
            satPos = sat.getPosition(userPos, calendarTimeMillis)
            elevation = satPos.elevation
            if (elevation > maxElevation) {
                maxElevation = elevation
                alt = satPos.altitude
                tcaAz = Math.toDegrees(satPos.azimuth)
            }
        } while (satPos.elevation < 0.0)

        val aos = satPos.time
        val aosAz = Math.toDegrees(satPos.azimuth)

        do {
            calendarTimeMillis += 30L * 1000L
            satPos = sat.getPosition(userPos, calendarTimeMillis)
            elevation = satPos.elevation
            if (elevation > maxElevation) {
                maxElevation = elevation
                alt = satPos.altitude
                tcaAz = Math.toDegrees(satPos.azimuth)
            }
        } while (satPos.elevation > 0.0)

        calendarTimeMillis += -30L * 1000L
        do {
            calendarTimeMillis += 3L * 1000L
            satPos = sat.getPosition(userPos, calendarTimeMillis)
            elevation = satPos.elevation
            if (elevation > maxElevation) {
                maxElevation = elevation
                alt = satPos.altitude
                tcaAz = Math.toDegrees(satPos.azimuth)
            }
        } while (satPos.elevation > 0.0)

        val los = satPos.time
        val losAz = Math.toDegrees(satPos.azimuth)
        val tca = (aos + los) / 2
        val elev = Math.toDegrees(maxElevation)
        return SatAboveTheUserDomainModel(aos, aosAz, los, losAz, tca, tcaAz, alt, elev, sat)
    }
}


