package com.example.directjikokuhyou

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.example.directjikokuhyou.ui.theme.Orange
import com.example.directjikokuhyou.utils.BitmapGenerator
import com.example.directjikokuhyou.utils.loadTrainTimes
import com.example.directjikokuhyou.utils.getNextTwoDirectTrains
import com.example.directjikokuhyou.utils.getWidgetContainerMaxWidth
import com.example.directjikokuhyou.utils.BitmapUtils
import com.example.directjikokuhyou.utils.TrainTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val bitmapGenerator = BitmapGenerator(context) // BitmapGeneratorを初期化

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

            // 現在の時刻を取得し、widget_textに設定
            val currentTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            views.setTextViewText(R.id.widget_text, "更新時刻: $currentTime")

            // `widget_image_container`の最大幅を取得
            val containerMaxWidth = getWidgetContainerMaxWidth(context, appWidgetManager, appWidgetId)

            val trainTimeList = loadTrainTimes(context)
            val trainTimePair = getNextTwoDirectTrains(trainTimeList)

            // 共通処理でBitmapを設定
            val bitmaps = listOf(
                setupBitmapForTrain(trainTimePair.first, bitmapGenerator),
                setupBitmapForTrain(trainTimePair.second, bitmapGenerator)
            )

            // `widget_image`はそのまま設定
            views.setImageViewBitmap(R.id.widget_image, bitmaps[0].first)
            views.setImageViewBitmap(R.id.widget_image_2, bitmaps[1].first)

            // 各Bitmapの幅を取得
            val totalWidth1 = bitmaps[0].first.width + bitmaps[0].second.width + bitmaps[0].third.width
            val totalWidth2 = bitmaps[1].first.width + bitmaps[1].second.width + bitmaps[1].third.width

            val stationSymbolBitmap = bitmapGenerator.generateStationSymbol(
                text = "OH\n01",
                circleColor = Color.BLUE,
                textColor = Color.WHITE,
                textSize = 24f,
                textHeight = bitmaps[0].first.height // 時刻Bitmapの高さを基準にする
            )

// シンボルマークをウィジェットに設定
            views.setImageViewBitmap(R.id.widget_station_symbol, stationSymbolBitmap)
            views.setImageViewBitmap(R.id.widget_station_symbol2, stationSymbolBitmap)


            // 幅の合計が`containerMaxWidth`を超える場合にリサイズ
            if (totalWidth1 > containerMaxWidth) {
                val scaleFactor = containerMaxWidth.toFloat() / totalWidth1
                val adjustedExpWidth = (bitmaps[0].second.width * scaleFactor).toInt()
                val adjustedDestWidth = (bitmaps[0].third.width * scaleFactor).toInt()

                // 幅のみリサイズ（高さは固定）
                val resizedBitmapExp = BitmapUtils.resizeWidth(bitmaps[0].second, adjustedExpWidth)
                val resizedBitmapDest = BitmapUtils.resizeWidth(bitmaps[0].third, adjustedDestWidth)

                // リサイズ後のBitmapを設定
                views.setImageViewBitmap(R.id.widget_image_exp, resizedBitmapExp)
                views.setImageViewBitmap(R.id.widget_image_dest, resizedBitmapDest)
            } else {
                // 幅が問題なければそのまま設定
                views.setImageViewBitmap(R.id.widget_image_exp, bitmaps[0].second)
                views.setImageViewBitmap(R.id.widget_image_dest, bitmaps[0].third)
            }

            // 幅の合計が`containerMaxWidth`を超える場合にリサイズ
            if (totalWidth2 > containerMaxWidth) {
                val scaleFactor = containerMaxWidth.toFloat() / totalWidth2
                val adjustedExpWidth = (bitmaps[1].second.width * scaleFactor).toInt()
                val adjustedDestWidth = (bitmaps[1].third.width * scaleFactor).toInt()

                // 幅のみリサイズ（高さは固定）
                val resizedBitmapExp = BitmapUtils.resizeWidth(bitmaps[1].second, adjustedExpWidth)
                val resizedBitmapDest = BitmapUtils.resizeWidth(bitmaps[1].third, adjustedDestWidth)

                // リサイズ後のBitmapを設定
                views.setImageViewBitmap(R.id.widget_image_exp_2, resizedBitmapExp)
                views.setImageViewBitmap(R.id.widget_image_dest_2, resizedBitmapDest)
            } else {
                // 幅が問題なければそのまま設定
                views.setImageViewBitmap(R.id.widget_image_exp_2, bitmaps[1].second)
                views.setImageViewBitmap(R.id.widget_image_dest_2, bitmaps[1].third)
            }

            // ウィジェットを更新
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setupBitmapForTrain(
        train: TrainTime?,
        bitmapGenerator: BitmapGenerator
    ): Triple<Bitmap, Bitmap, Bitmap> {
        val bitmapImage = bitmapGenerator.generate(
            text = train?.time ?: "",
            fontResId = R.font.pixelfont,
            textSize = 50f,
            textColor = Color.WHITE
        )

        val bitmapExp = when (train?.color) {
            "b" -> bitmapGenerator.generate(
                "  ",
                R.font.pixelfont,
                50f,
                Color.CYAN
            )
            "r" -> bitmapGenerator.generate(
                " 急行",
                R.font.pixelfont,
                50f,
                Color.RED
            )
            else -> bitmapGenerator.generate(
                "",
                R.font.pixelfont,
                50f,
                Color.RED
            )
        }

        val bitmapDest = bitmapGenerator.generate(
            text = if (train?.time != null) "新宿" else "",
            R.font.pixelfont,
            50f,
            Orange.toArgb()
        )

        return Triple(bitmapImage, bitmapExp, bitmapDest)
    }
}