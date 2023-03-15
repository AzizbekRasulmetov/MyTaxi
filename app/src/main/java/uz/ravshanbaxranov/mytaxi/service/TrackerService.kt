package uz.ravshanbaxranov.mytaxi.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.mapbox.geojson.Point
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.mytaxi.data.model.Track
import uz.ravshanbaxranov.mytaxi.domain.use_case.TrackUseCases
import uz.ravshanbaxranov.mytaxi.util.Constants.ACTION_SERVICE_START
import uz.ravshanbaxranov.mytaxi.util.Constants.ACTION_SERVICE_STOP
import uz.ravshanbaxranov.mytaxi.util.Constants.LOCATION_UPDATE_INTERVAL
import uz.ravshanbaxranov.mytaxi.util.Constants.LOCATION_UPDATE_MAXIMUM_INTERVAL
import uz.ravshanbaxranov.mytaxi.util.Constants.LOCATION_UPDATE_MINIMUM_INTERVAL
import uz.ravshanbaxranov.mytaxi.util.Constants.NOTIFICATION_CHANNEL_ID
import uz.ravshanbaxranov.mytaxi.util.Constants.NOTIFICATION_CHANNEL_NAME
import uz.ravshanbaxranov.mytaxi.util.Constants.NOTIFICATION_ID
import javax.inject.Inject

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    @Inject
    lateinit var notification: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var trackUseCases: TrackUseCases

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        var isServiceRunning = false
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            lifecycleScope.launch(Dispatchers.IO) {
                result.lastLocation.let {
                    updateNotification()

                    Point.fromLngLat(it?.longitude ?: 0.0, it?.latitude ?: 0.0)
                    trackUseCases.addTrack(
                        Track(
                            latitude = it?.longitude ?: 0.0,
                            longitude = it?.latitude ?: 0.0
                        )
                    )
                }
            }

        }

    }

    private fun updateNotification() {

        notification.apply {
            setContentTitle("MyTaxi")
            setContentText("Online")
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())

    }


    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                }
                ACTION_SERVICE_STOP -> {
                    stopForegroundService()
                }
                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopForegroundService() {
        isServiceRunning = false
        removeLocationUpdates()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    private fun startForegroundService() {
        createNotificationManagerChannel()
        startForeground(NOTIFICATION_ID, notification.build())
        startUpdateLocation()
        isServiceRunning = true
    }


    @SuppressLint("MissingPermission")
    private fun startUpdateLocation() {

        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(LOCATION_UPDATE_MINIMUM_INTERVAL)
                .setMaxUpdateDelayMillis(LOCATION_UPDATE_MAXIMUM_INTERVAL)
                .build()


        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }


    private fun createNotificationManagerChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


}