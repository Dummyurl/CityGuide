package sk.dmsoft.cityguide.Commons.Services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import org.java_websocket.client.WebSocketClient
import android.content.ContentValues.TAG
import android.util.Log
import android.content.Context.LOCATION_SERVICE
import android.content.ContentValues.TAG
import android.content.Context
import android.location.LocationListener


interface LocationUpdateCallback{
    fun updateLocation(position: Location)
}

class LocationService : Service(), LocationListener{
    val TAG = "Location manager"
    var mLocationManager: LocationManager? = null
    val LOCATION_INTERVAL: Long = 10
    val LOCATION_DISTANCE = 1f


    override fun onLocationChanged(p0: Location?) {
        if (p0 == null)
            return
        Log.e(TAG, "location changed: ${p0.latitude}-${p0.longitude}")
        serviceCallbacks?.updateLocation(p0)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        Log.e(TAG, "$p0 + $p1 ")
    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {
        Log.e(TAG, "Location disabled")
    }

    private val binder = LocalBinder()
    // Registered callbacks
    private var serviceCallbacks: LocationUpdateCallback? = null


    // Class used for the client Binder.
    inner class LocalBinder : Binder() {
        internal// Return this instance of MyService so clients can call public methods
        val service: LocationService
            get() = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
        initializeLocationManager()
        try {
            mLocationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    this)
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }

        try {
            mLocationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    this)
        } catch (ex: java.lang.SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "gps provider does not exist " + ex.message)
        }

    }

    private fun initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager")
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun setCallbacks(callbacks: LocationUpdateCallback) {
        serviceCallbacks = callbacks
    }
}