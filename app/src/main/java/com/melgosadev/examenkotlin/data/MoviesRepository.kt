package com.melgosadev.examenkotlin.data

import com.melgosadev.examenkotlin.data.db.dao.MoviesDao
import com.melgosadev.examenkotlin.data.network.MoviesService
import com.melgosadev.examenkotlin.models.Movies
import com.melgosadev.examenkotlin.models.NowPlayingMovies
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val api : MoviesService,
    private val moviesDao: MoviesDao
    ){

    suspend fun getNowPlayingMovies(): NowPlayingMovies? {
        return api.getNowPlayingMovies()
    }

    fun getNowPlayingMoviesFromDB(): NowPlayingMovies {
        return NowPlayingMovies(null, 1, moviesDao.getNowPlayingMovies(), 0, 0)
    }

    fun addMovie(movie: Movies) {
        moviesDao.addMovie(movie)
    }

    fun deleteMovies() {
        moviesDao.deleteNowPlayingMovies()
    }
}