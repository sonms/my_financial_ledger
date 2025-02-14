package com.purang.financial_ledger.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter

class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color?): Int? {
        return color?.toArgb() // Color를 Int로 변환 (ARGB)
    }

    @TypeConverter
    fun toColor(value: Int?): Color? {
        return value?.let { Color(it) } // Int를 Color로 변환
    }
}