package com.example.newsapp.util

import androidx.room.TypeConverter
import com.example.newsapp.data.entity.Source

class Converters {
    @TypeConverter
    fun fromSource(source: Source) = source.name
    @TypeConverter
    fun toSource(name: String) = Source(name, name)
}