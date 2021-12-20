package com.melgosadev.examenkotlin

import android.app.Application
import androidx.room.Room
import com.melgosadev.examenkotlin.data.db.MoviesDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExamenKotlinApplication: Application() {

    companion object{
        lateinit var database: MoviesDatabase
    }

    override fun onCreate() {
        super.onCreate()
        ExamenKotlinApplication.database = Room.databaseBuilder(this, MoviesDatabase::class.java, "Movies.db").allowMainThreadQueries().build()
    }
}