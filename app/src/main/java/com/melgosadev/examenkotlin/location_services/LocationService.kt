package com.melgosadev.examenkotlin.location_services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.melgosadev.examenkotlin.R
import java.util.*

class LocationService: Service(){
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val db = Firebase.firestore

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
        const val NOTIFICATION_CHANNEL_ID = "examen_kotlin_notification_location"
        const val PERIOD: Long = 1000 * 60 * 5 // cada 5 minutos
        const val COLLECTION_PATH = "locations"
        const val DATA_LONGITUDE = "longitude"
        const val DATA_LATITUDE = "latitude"
        const val DATA_TIMESTAMP = "timestamp"
    }

    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    if(location?.latitude != null)
                        mLocation = location
                    else
                        getLastLocation()
                }
            })
        scheduleRecordLocation()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false

    }

    /**
     * Se muestra una notificación permanente que le indica al usuario que su geolocalización esta
     * siendo monitoreada
     */
    private fun showNotification(){
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.noti_title_examen_kotlin))
                .setContentText(getString(R.string.noti_content_info))
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    /**
     * Se programa cada 5 minutos el guardado de la geolocalización en Firestore
     */
    private fun scheduleRecordLocation(){
        val timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                addLocationToFirestore()
            }
        }
        timer.schedule(task, 0L, PERIOD)
    }

    /**
     * Se añade la geolocalización obtenida a Firestore, y en caso de éxito se muestra una
     * notificación al usuario de manera silenciosa
     */
    private fun addLocationToFirestore() {
        if(mLocation != null){
            val data = hashMapOf(
                DATA_LATITUDE to mLocation!!.latitude.toString(),
                DATA_LONGITUDE to mLocation!!.longitude.toString(),
                DATA_TIMESTAMP to FieldValue.serverTimestamp()
            )

            db.collection(COLLECTION_PATH)
                .add(data)
                .addOnSuccessListener {
                    showLocationRegisterNotification()
                }
                .addOnFailureListener {
                }
        }
    }

    /**
     * Se obtiene la última ubicación conocida del dispositivo
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                mLocation = location
            }
    }

    /**
     * Muestra una notificación silenciosa al usuario para informarle que su geolocalización ha sido
     * guardada con éxito
     */
    private fun showLocationRegisterNotification(){
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_map_24)
            .setContentTitle(getString(R.string.noti_title_ubi_guardada))
            .setContentText(getString(R.string.noti_content_info_ubi))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.noti_content_info_ubi)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)){
            notify(2, builder.build())
        }
    }
}