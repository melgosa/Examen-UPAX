package com.melgosadev.examenkotlin.data

import com.melgosadev.examenkotlin.ExamenKotlinApplication
import com.melgosadev.examenkotlin.data.network.MoviesService
import com.melgosadev.examenkotlin.models.NowPlayingMovies
import javax.inject.Inject

class MoviesRepository @Inject constructor(private val api : MoviesService){


    private val db = ExamenKotlinApplication.database.moviesDao()

    suspend fun getNowPlayingMovies(): NowPlayingMovies? {
        return api.getNowPlayingMovies()
    }

    suspend fun getNowPlayingMoviesFromDB(): NowPlayingMovies {
        return NowPlayingMovies(null, 1, db.getNowPlayingMovies(), 0, 0)
    }
}