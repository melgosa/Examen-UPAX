package com.melgosadev.examenkotlin.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.melgosadev.examenkotlin.data.db.dao.MoviesDao
import com.melgosadev.examenkotlin.models.Movies

@Database(entities = [Movies::class], version = 1)
abstract class MoviesDatabase: RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
}