package com.melgosadev.examenkotlin.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.melgosadev.examenkotlin.models.MarkerData
import java.text.SimpleDateFormat
import java.util.*

class MapViewModel : ViewModel() {

    private val db = Firebase.firestore
    //Variable de interes para ser observada y vigilar los cambios (Un marker personalizado)
    val markerData = MutableLiveData<MarkerData>()
    val errorMessage = MutableLiveData<String>()

    companion object{
        const val PARAM_LATITUDE = "latitude"
        const val PARAM_LONGITUDE = "longitude"
        const val PARAM_TIMESTAMP = "timestamp"
    }

    /**
    * Obtiene los documentos de FirebaseStore donde se almacenan todas las ubicaciones guardadas y
    * los convierte en Markers personalizados para mostrar al usuario
    * */
    fun getAllLocations(){
        db.collection("locations")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //Se crea el objeto que guarda las coordenadas de latitud y longitud
                    val latLon =  LatLng(
                        document.data[PARAM_LATITUDE].toString().toDouble(),
                        document.data[PARAM_LONGITUDE].toString().toDouble()
                    )
                    //Se crea el objeto marker que contien lo necesario para mostrar el marker
                    //personalidado
                    val marker = MarkerData(
                        latLon,
                        getDateFromTimestamp(document.data[PARAM_TIMESTAMP].toString())
                    )
                    markerData.value = marker
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.toString()
            }
    }

    /**
     * Convierte la información contenida en el parámetro proporcionado a una fecha formateada legible
     * y entendible par el usuario
     */
    private fun getDateFromTimestamp(text: String): String{
        //Ejemplo del valor devuelto por firebasestore
        //Timestamp(seconds=1639935303, nanoseconds=905000000)
        //Se obtiene el valor que solo correspondea los segundos
        //Se formatea la la fecha a partir de un timestamp (Long)
        return try {
            val timeStamp = text.substring(18,28).toLong()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val netDate = Date(timeStamp * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}