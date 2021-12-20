package com.melgosadev.examenkotlin.data.network

import com.melgosadev.examenkotlin.models.NowPlayingMovies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MoviesService @Inject constructor(private val api: MoviesApiClient){

    suspend fun getNowPlayingMovies(): NowPlayingMovies?{
        return withContext(Dispatchers.IO){
            val response = api.getNowPlayingMovies()
            response.body()
        }
    }
}