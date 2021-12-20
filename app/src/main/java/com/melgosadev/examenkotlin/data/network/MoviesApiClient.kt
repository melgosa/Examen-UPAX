package com.melgosadev.examenkotlin.data.network

import com.melgosadev.examenkotlin.models.NowPlayingMovies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "1865f43a0549ca50d341dd9ab8b29f49"
const val LANGUAGE = "es-ES"

interface MoviesApiClient {
    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = LANGUAGE,
        @Query("page") page: String = "1"
    ): Response<NowPlayingMovies>
}