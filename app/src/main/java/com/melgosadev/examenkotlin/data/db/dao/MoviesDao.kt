package com.melgosadev.examenkotlin.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.melgosadev.examenkotlin.models.Movies

@Dao
interface MoviesDao {
    @Query("select * from Movies")
    fun getNowPlayingMovies(): MutableList<Movies>

    @Insert
    fun addMovie(movies: Movies)

    @Query("delete from Movies")
    fun deleteNowPlayingMovies()
}