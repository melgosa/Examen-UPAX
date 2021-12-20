package com.melgosadev.examenkotlin.domain

import com.melgosadev.examenkotlin.data.MoviesRepository
import javax.inject.Inject

class GetNowPlayingMoviesUseCase @Inject constructor(private val repository : MoviesRepository){

    suspend operator fun invoke() = repository.getNowPlayingMovies()
}