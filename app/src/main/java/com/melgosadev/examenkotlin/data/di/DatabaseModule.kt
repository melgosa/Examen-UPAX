package com.melgosadev.examenkotlin.data.di

import android.content.Context
import androidx.room.Room
import com.melgosadev.examenkotlin.data.db.MoviesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app:Context) = Room.databaseBuilder(
        app,
        MoviesDatabase::class.java,
        "Movies.db"
    ).allowMainThreadQueries().build()

    @Singleton
    @Provides
    fun provideMoviesDao(db: MoviesDatabase) = db.moviesDao()
}