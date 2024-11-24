package com.example.directjikokuhyou.utils

import android.content.Context
import com.example.directjikokuhyou.utils.DayUtils.isWeekendOrHoliday
import com.google.gson.Gson
import java.io.InputStreamReader
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 新しいラッパークラスを作成
data class TrainTimesResponse(val times: List<TrainTime>)

// TrainTime クラス（変更なし）
data class TrainTime(val time: String, val color: String, val direct: String)


fun loadTrainTimes(context: Context): List<TrainTime> {
    val isHoliday = isWeekendOrHoliday()
    val fileName = if (isHoliday) "times_holiday.json" else "times_weekday.json"

    val inputStream = context.assets.open(fileName)
    val reader = InputStreamReader(inputStream)
    val json = reader.readText()
    reader.close()
    val response = Gson().fromJson(json.replace("\"Direct\":", "\"direct\":"), TrainTimesResponse::class.java)
    return response.times
}

fun getNextDirectTrain(times: List<TrainTime>): TrainTime? {
    val now = LocalTime.now() // 現在時刻を取得
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    println(times)

    // Directが "y" の電車をフィルタリング
    val filteredTimes = times.filter { it.direct == "y" }

    println("Filtered Times: $filteredTimes")  // フィルタリングされた電車をデバッグ用に表示

    // 現在時刻より後の電車を選び、最も早い電車を選択
    return filteredTimes
        .map { train ->
            val trainTime = LocalTime.parse(train.time, formatter)
            train to trainTime // 時刻と一緒にタプルとして返す
        }
        .filter { it.second.isAfter(now) } // 現在時刻より後の電車
        .minByOrNull { it.second } // 最も早く来る電車を取得
        ?.first // 取得した電車情報を返す
}

fun getNextTwoDirectTrains(times: List<TrainTime>): Pair<TrainTime?, TrainTime?> {
    val now = LocalTime.now() // 現在時刻を取得
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    println(times)

    // Directが "y" の電車をフィルタリング
    val filteredTimes = times.filter { it.direct == "y" }

    println("Filtered Times: $filteredTimes")  // フィルタリングされた電車をデバッグ用に表示

    // 現在時刻より後の電車を選び、リストとして保存
    val upcomingTrains = filteredTimes
        .map { train ->
            val trainTime = LocalTime.parse(train.time, formatter)
            train to trainTime // 時刻と一緒にタプルとして返す
        }
        .filter { it.second.isAfter(now) } // 現在時刻より後の電車
        .sortedBy { it.second } // 時刻順にソート
        .map { it.first } // 電車情報のみを取得

    // 最初の電車と次の電車を取得（存在しない場合はnull）
    val nextTrain = upcomingTrains.getOrNull(0)
    val nextNextTrain = upcomingTrains.getOrNull(1)

    return nextTrain to nextNextTrain
}
