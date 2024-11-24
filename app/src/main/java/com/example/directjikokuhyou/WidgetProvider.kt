package com.example.directjikokuhyou

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.RemoteViews
import com.example.directjikokuhyou.utils.loadTrainTimes
import com.example.directjikokuhyou.utils.getNextTwoDirectTrains
import com.example.directjikokuhyou.utils.getWidgetContainerMaxWidth
import com.example.directjikokuhyou.utils.resizeBitmapWidth
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 複数のウィジェットがある場合に対応
        for (appWidgetId in appWidgetIds) {
            // ウィジェットレイアウトを取得
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // ボタンのクリックイベントを設定
            val intent = Intent(context, WidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_image_container, pendingIntent)

            // 現在の時刻を取得
            val currentTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            // 取得した現在時刻をwidget_textに設定
            views.setTextViewText(R.id.widget_text, "更新時刻: $currentTime")

            // `widget_image_container`の最大幅を取得
            val containerMaxWidth = getWidgetContainerMaxWidth(context, appWidgetManager, appWidgetId)


            val trainTimeList = loadTrainTimes(context)
            val trainTimePair = getNextTwoDirectTrains(trainTimeList)

            // Bitmapを作成
            val bitmap = createBitmapWithCustomFont(
                context,
                trainTimePair.first?.time ?: "直通なし",
                R.font.pixelfont, // フォントリソース
                50f, // テキストサイズ
                textColor = android.graphics.Color.WHITE
            )

            // Bitmapを作成
            val bitmapExp = when (trainTimePair.first?.color){
                "b" -> createBitmapWithCustomFont(
                context,
                "        ",
                R.font.pixelfont, // フォントリソース
                50f, // テキストサイズ
                android.graphics.Color.CYAN
                )

                "r" -> createBitmapWithCustomFont(
                    context,
                    "    急行",
                    R.font.pixelfont, // フォントリソース
                    50f, // テキストサイズ
                    android.graphics.Color.RED
                )

                else -> createBitmapWithCustomFont(
                    context,
                    "        ",
                    R.font.pixelfont, // フォントリソース
                    50f, // テキストサイズ
                    android.graphics.Color.RED
                )
                }

            // Bitmapを作成
            val bitmapDest = createBitmapWithCustomFont(
                context,
                "新宿",
                R.font.pixelfont, // フォントリソース
                50f, // テキストサイズ
                android.graphics.Color.CYAN
            )

            // `widget_image`はそのまま設定
            views.setImageViewBitmap(R.id.widget_image, bitmap)

            // 各Bitmapの幅を取得
            val totalWidth = bitmap.width + bitmapExp.width + bitmapDest.width

            // 幅の合計が`containerMaxWidth`を超える場合にリサイズ
            if (totalWidth > containerMaxWidth) {
                val scaleFactor = containerMaxWidth.toFloat() / totalWidth
                val adjustedExpWidth = (bitmapExp.width * scaleFactor).toInt()
                val adjustedDestWidth = (bitmapDest.width * scaleFactor).toInt()

                // 幅のみリサイズ（高さは固定）
                val resizedBitmapExp = resizeBitmapWidth(bitmapExp, adjustedExpWidth)
                val resizedBitmapDest = resizeBitmapWidth(bitmapDest, adjustedDestWidth)

                println("Resized Exp Width: ${resizedBitmapExp.width}, Height: ${resizedBitmapExp.height}")
                println("Resized Dest Width: ${resizedBitmapDest.width}, Height: ${resizedBitmapDest.height}")

                // リサイズ後のBitmapを設定
                views.setImageViewBitmap(R.id.widget_image_exp, resizedBitmapExp)
                views.setImageViewBitmap(R.id.widget_image_dest, resizedBitmapDest)
                println("resized")
            } else {
                // 幅が問題なければそのまま設定
                views.setImageViewBitmap(R.id.widget_image_exp, bitmapExp)
                views.setImageViewBitmap(R.id.widget_image_dest, bitmapDest)
                println("not resized")
            }

            println("Bitmap Widths - widget_image: ${bitmap.width}, widget_image_exp: ${bitmapExp.width}, widget_image_dest: ${bitmapDest.width}")
            println("Total Width: $totalWidth, Container Max Width: $containerMaxWidth")


            // ウィジェットを更新
            appWidgetManager.updateAppWidget(appWidgetId, views)
            println("update widget")
        }
    }

    fun createBitmapWithCustomFont(
        context: Context,
        text: String,
        fontResId: Int,
        textSize: Float,
        textColor: Int
    ): Bitmap {
        val paint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textAlign = Paint.Align.LEFT
            this.textSize = textSize

            // フォントを設定
            typeface = Typeface.create(context.resources.getFont(fontResId), Typeface.NORMAL)
        }

        // テキストの幅と高さを計算
        val textWidth = paint.measureText(text).toInt()
        val textHeight = (paint.descent() - paint.ascent()).toInt()

        // Bitmapを生成
        val bitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 背景を黒で塗りつぶす
        canvas.drawColor(android.graphics.Color.parseColor("#101010"))
        // カスタムフォント文字を描画
        canvas.drawText(text, 0f, -paint.ascent(), paint)

        return bitmap
    }
}