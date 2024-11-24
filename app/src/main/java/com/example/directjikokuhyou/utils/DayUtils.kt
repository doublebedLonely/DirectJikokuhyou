package com.example.directjikokuhyou.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DayUtils {
    fun isWeekendOrHoliday(date: LocalDate = LocalDate.now()): Boolean {
        if (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) {
            return true
        }

        val formatter = DateTimeFormatter.ofPattern("MM-dd")
        val holidays = listOf("01-01", "02-11", "04-29", "05-03", "05-04", "05-05", "07-17", "08-11", "09-18", "10-09", "11-03", "11-23")
        return holidays.contains(date.format(formatter))
    }
}