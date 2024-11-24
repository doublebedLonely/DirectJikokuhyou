package com.example.directjikokuhyou.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

object BitmapUtils {
    fun resizeWidth(bitmap: Bitmap, newWidth: Int): Bitmap {
        val originalHeight = bitmap.height
        return Bitmap.createScaledBitmap(bitmap, newWidth, originalHeight, true)
    }


}