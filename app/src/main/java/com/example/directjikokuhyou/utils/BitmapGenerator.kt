package com.example.directjikokuhyou.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

class BitmapGenerator(private val context: Context) {

    fun generate(
        text: String,
        fontResId: Int,
        textSize: Float,
        textColor: Int,
        backgroundColor: Int = android.graphics.Color.parseColor("#101010"),
        defaultWidth: Int = 50, // デフォルトの幅
        defaultHeight: Int = 50 // デフォルトの高さ
    ): Bitmap {
        val paint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textAlign = Paint.Align.LEFT
            this.textSize = textSize
            typeface = Typeface.create(context.resources.getFont(fontResId), Typeface.NORMAL)
        }

        val textWidth = paint.measureText(text).toInt().coerceAtLeast(defaultWidth)
        val textHeight = (paint.descent() - paint.ascent()).toInt().coerceAtLeast(defaultHeight)

        val bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(backgroundColor)
        canvas.drawText(text, 0f, -paint.ascent(), paint)

        return bitmap
    }

    fun generateStationSymbol(
        text: String,
        circleColor: Int,
        textColor: Int,
        textSize: Float,
        textHeight: Int // 文字のBitmap高さを基準に調整
    ): Bitmap {
        val paint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        // シンボルマークのサイズを文字高さの90%に調整
        val diameter = (textHeight * 0.8).toInt()
        val radius = diameter / 2

        // Bitmapを生成
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 背景の丸を描画
        paint.color = circleColor
        canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), paint)

        // テキストを描画
        paint.color = textColor
        paint.textSize = textSize * 0.8f // テキストサイズも少し縮小
        val lines = text.split("\n")

        // テキストの総高さを計算
        val lineHeight = paint.textSize
        val totalTextHeight = lines.size * lineHeight + (lines.size - 1) * 4 // 行間を4ピクセル程度空ける

        // テキストの基準点を計算
        val centerY = radius.toFloat()
        val startY = centerY - totalTextHeight / 2 + lineHeight / 2

        // 各行を描画
        for ((index, line) in lines.withIndex()) {
            val y = startY + index * (lineHeight + 4) // 行間を加味
            canvas.drawText(line, radius.toFloat(), y, paint)
        }

        return bitmap
    }


}