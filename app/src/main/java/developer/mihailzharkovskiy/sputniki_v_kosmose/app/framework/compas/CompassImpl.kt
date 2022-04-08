package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.Compass
import javax.inject.Inject

class CompassImpl @Inject constructor(
    private val resource: Resource,
    @ApplicationContext
    private val context: Context,
) : SensorEventListener, Compass {

    private val sensorManager: SensorManager =
        context.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val sensorCompass: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) //перепиши на новый сенсор
    private var sensorAccuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE
    private var azimuthListener: Compass.AzimuthListener? = null
    private val formatAzimuth by lazy { resource.getString(R.string.fps_compas) }

    init {
        testSensor()
    }

    private fun testSensor() {
        if (sensorCompass == null) {
            azimuthListener?.onOrientationChanged(CompassEvent.Error(resource.getString(R.string.compass_no_sensor)))
        }
    }

    private fun emitAzimuth(azimuth: Int) {
        azimuthListener?.onOrientationChanged(
            CompassEvent.Data(
                azimuth = azimuth,
                azimuthString = String.format(
                    formatAzimuth,
                    azimuth,
                    azimuth.azimuthToSideWorld(resource))
            )
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        when {
            sensorAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE -> return
            event.sensor == sensorCompass -> emitAzimuth(event.values[0].toInt())
        }
    }

    override fun onAccuracyChanged(compass: Sensor, accuracy: Int) {
        sensorAccuracy = accuracy
    }

    override fun startListening(listener: Compass.AzimuthListener) {
        if (sensorCompass != null && azimuthListener != listener) {
            azimuthListener = listener
            sensorManager.registerListener(this,
                sensorCompass,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else testSensor()
    }

    override fun stopListening(listener: Compass.AzimuthListener) {
        sensorManager.unregisterListener(this)
        azimuthListener = null
    }
}
