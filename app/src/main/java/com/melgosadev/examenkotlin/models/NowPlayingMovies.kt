package com.melgosadev.examenkotlin.models

data class NowPlayingMovies(
    val dates: Dates?,
    val page: Long,
    val results: List<Movies>,
    val total_pages: Long,
    val total_results: Long
)

data class Dates (
    val maximum: String,
    val minimum: String
)



