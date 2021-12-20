package com.melgosadev.examenkotlin.domain

import com.melgosadev.examenkotlin.data.MoviesRepository
import javax.inject.Inject

class GetNowPlayingMoviesFromDBUseCase @Inject constructor(private val repository :MoviesRepository){

    suspend operator fun invoke() = repository.getNowPlayingMoviesFromDB()
}