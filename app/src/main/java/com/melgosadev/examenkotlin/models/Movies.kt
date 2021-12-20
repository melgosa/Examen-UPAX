package com.melgosadev.examenkotlin.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies")
data class Movies (
    var adult: Boolean,
    var backdrop_path: String,
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var original_language: String,
    var original_title: String,
    var overview: String,
    var popularity: Double,
    var poster_path: String,
    var release_date: String,
    var title: String,
    var video: Boolean,
    var vote_average: Double,
    var vote_count: Long
)