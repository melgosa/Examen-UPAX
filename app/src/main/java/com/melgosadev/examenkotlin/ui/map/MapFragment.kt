package com.melgosadev.examenkotlin.ui.map

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.melgosadev.examenkotlin.R
import com.melgosadev.examenkotlin.databinding.FragmentMapBinding
import com.melgosadev.examenkotlin.models.MarkerData

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentMapBinding? = null
    private lateinit var map: GoogleMap
    companion object{
        const val ANIMATION_DURATION = 4000
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if(internetAvailable(requireContext())){
            createMapFragment()
            mapViewModel.getAllLocations()
            //Se observa la variable de interes, en este caso un marker personalizado (De la data class
            //MarkerData
            mapViewModel.markerData.observe(viewLifecycleOwner, { markerData ->
                createMarker(markerData)
            })
            //Se observa la variable de interes, en este caso un mensaje de error al obtener las ubicaciones
            mapViewModel.errorMessage.observe(viewLifecycleOwner, { errorMessage ->
                showDialog(errorMessage)
            })
        }else{
            showDialog(getString(R.string.dialog_content_error_internet))
        }
        return root
    }

    private fun createMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        animateCamera()
    }

    /**
     * Crea un marker en el mapa, que al tocarlo muestra la fecha en que fue guardada dicha ubicación
     */
    private fun createMarker(markerData: MarkerData) {
        map.addMarker(MarkerOptions().position(markerData.latLng).title(markerData.fAlmacenaiento))
    }

    /**
     * Realiza un zom de la camara con un efecto
     */
    private fun animateCamera(){
        //Ubicación cercana  mi domicilio
        val nearFromMyPlace = LatLng(19.4, -99.1)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(nearFromMyPlace, 8f),
            ANIMATION_DURATION,
            null
        )
    }

    /**
     * Muestra un diálogo simple, con un boton de acción
     */
    private fun showDialog(errorInfo: String){
        let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.apply {
                setPositiveButton(R.string.accept) { dialog, _ ->
                    dialog.dismiss()
                }
                setTitle(R.string.dialog_title_error)
                setMessage(getString(R.string.dialog_message_error_al_obtener_ubicaciones) + errorInfo)
                create()
                show()
            }
        }
    }

    /**
     * Se verifica la conexión a internet
     */
    private fun internetAvailable(context: Context): Boolean {
        val cmg = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cmg.getNetworkCapabilities(cmg.activeNetwork)?.let { networkCapabilities ->
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        } else {
            return cmg.activeNetworkInfo?.isConnectedOrConnecting == true
        }

        return false
    }
}