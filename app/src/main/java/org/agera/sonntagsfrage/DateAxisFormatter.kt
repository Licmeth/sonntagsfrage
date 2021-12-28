package org.agera.sonntagsfrage

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateAxisFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return LocalDate.ofEpochDay(value.toLong()).format(DateTimeFormatter.ISO_DATE)
    }
}