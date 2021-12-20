package com.melgosadev.examenkotlin.ui.movies

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melgosadev.examenkotlin.data.MoviesRepository
import com.melgosadev.examenkotlin.domain.GetNowPlayingMoviesFromDBUseCase
import com.melgosadev.examenkotlin.domain.GetNowPlayingMoviesUseCase
import com.melgosadev.examenkotlin.models.NowPlayingMovies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getNowPlayingMoviesFromDBUseCase: GetNowPlayingMoviesFromDBUseCase,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {
    val nowPlayingMovies = MutableLiveData<NowPlayingMovies>()

    /**
     * Se consume el servicio web de Películas de Now Playing y cuando cambia la variable de interés
     * (nowPlayingMovies) se notifica a la UI para mostrar las películas
     */
    fun onCreate(context: Context){
        if(internetAvailable(context)){
            viewModelScope.launch {
                val result = getNowPlayingMoviesUseCase()

                if(result != null){
                    nowPlayingMovies.value = result!!
                        moviesRepository.deleteMovies()
                        nowPlayingMovies.let {
                            for (movie in result.results){
                                moviesRepository.addMovie(movie)
                            }
                        }
                }
            }
        }else{
            viewModelScope.launch {
                val result = getNowPlayingMoviesFromDBUseCase()
                nowPlayingMovies.value = result!!
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