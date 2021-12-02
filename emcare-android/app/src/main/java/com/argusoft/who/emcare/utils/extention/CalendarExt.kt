@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.argusoft.who.emcare.utils.extention

import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * USAGE : Calender utils class for basic operations.
 * Created by R.S.
 */
const val DATE_FORMAT_DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss"
const val DATE_FORMAT_DD_MM_YYYY = "dd-MM-yyyy"
const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
const val DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

const val MILLIS: Long = 1000
const val SECONDS: Long = 60
const val SECONDS_IN_MILLIS: Long = MILLIS
const val MINUTE_IN_MILLIS: Long = SECONDS * MILLIS
const val HOUR_IN_MILLIS: Long = 60 * MINUTE_IN_MILLIS
const val DAY_IN_MILLIS: Long = 24 * HOUR_IN_MILLIS

const val MINUTE_IN_SECONDS: Long = SECONDS
const val HOUR_IN_SECONDS: Long = 60 * MINUTE_IN_SECONDS
const val DAY_IN_SECONDS: Long = 24 * HOUR_IN_SECONDS

//========================================== Basic Operations of Calendar ==========================================

/**
 * Return calendar's not time
 *
 * @return Calendar object
 */
inline fun Calendar.now(): Calendar {
    return Calendar.getInstance()
}

/**
 * Add number of minutes into Calendar object.
 *
 * @param noOfMinutes Number of minutes to be added in Calendar object.
 * @return Calendar object after adding that minutes.
 */
inline fun Calendar.plusMinutes(noOfMinutes: Int): Calendar {
    this.add(Calendar.MINUTE, noOfMinutes)
    return this
}

/**
 * Remove number of minutes into Calendar object.
 *
 * @param noOfMinutes Number of minutes to be remove in Calendar object.
 * @return Calendar object after removing that minutes.
 */
inline fun Calendar.removeMinutes(noOfMinutes: Int): Calendar {
    this.add(Calendar.MINUTE, -noOfMinutes)
    return this
}

/**
 * Add number of hours into Calendar object.
 *
 * @param noOfHours Number of hours to be added in Calendar object.
 * @return Calendar object after adding that hours.
 */
inline fun Calendar.plusHours(noOfHours: Int): Calendar {
    this.add(Calendar.HOUR_OF_DAY, noOfHours)
    return this
}

/**
 * Remove number of hours into Calendar object.
 *
 * @param noOfHours Number of hours to be remove in Calendar object.
 * @return Calendar object after removing that hours.
 */
inline fun Calendar.removeHours(noOfHours: Int): Calendar {
    this.add(Calendar.HOUR_OF_DAY, -noOfHours)
    return this
}

/**
 * Add number of day into Calendar object.
 *
 * @param noOfDay Number of day to be added in Calendar object.
 * @return Calendar object after adding that days.
 */
inline fun Calendar.plusDays(noOfDay: Int): Calendar {
    this.add(Calendar.DATE, noOfDay)
    return this
}

/**
 * Remove number of day into Calendar object.
 *
 * @param noOfDay Number of day to be remove in Calendar object.
 * @return Calendar object after removing that days.
 */
inline fun Calendar.removeDays(noOfDay: Int): Calendar {
    this.add(Calendar.DATE, -noOfDay)
    return this
}

/**
 * Add number of week into Calendar object.
 *
 * @param noOfWeek Number of week to be added in Calendar object.
 * @return Calendar object after adding that month.
 */
inline fun Calendar.plusWeeks(noOfWeek: Int): Calendar {
    plusDays(noOfWeek * 7)
    return this
}

/**
 * Remove number of week into Calendar object.
 *
 * @param noOfWeek Number of week to be remove in Calendar object.
 * @return Calendar object after removing that month.
 */
inline fun Calendar.removeWeeks(noOfWeek: Int): Calendar {
    removeDays(noOfWeek * 7)
    return this
}

/**
 * Add number of month into Calendar object.
 *
 * @param noOfMonth Number of month to be added in Calendar object.
 * @return Calendar object after adding that month.
 */
inline fun Calendar.plusMonths(noOfMonth: Int): Calendar {
    this.add(Calendar.MONTH, noOfMonth)
    return this
}

/**
 * Remove number of month into Calendar object.
 *
 * @param noOfMonth Number of month to be remove in Calendar object.
 * @return Calendar object after removing that month.
 */
inline fun Calendar.removeMonths(noOfMonth: Int): Calendar {
    this.add(Calendar.MONTH, -noOfMonth)
    return this
}

/**
 * Add number of year into Calendar object.
 *
 * @param noOfYear Number of year to be added in Calendar object.
 * @return Calendar object after adding that years.
 */
inline fun Calendar.plusYears(noOfYear: Int): Calendar {
    this.add(Calendar.YEAR, noOfYear)
    return this
}

/**
 * Remove number of year into Calendar object.
 *
 * @param noOfYear Number of year to be remove in Calendar object.
 * @return Calendar object after removing that year.
 */
inline fun Calendar.removeYears(noOfYear: Int): Calendar {
    this.add(Calendar.YEAR, -noOfYear)
    return this
}


/**
 * Returns Calendar object with 00:00:00:000 (HH:mm:ss:sss)
 *
 * @return Calendar object
 */
inline fun Calendar.getStartTimeOfDay(): Calendar {
    getStartTimeOfDay(this)
    return this
}

/**
 * Returns Calendar object with 0:00:00:000 (HH:mm:ss:sss)
 *
 * @param cal Calendar object
 * @return Calendar object
 */
inline fun Calendar.getStartTimeOfDay(cal: Calendar = Calendar.getInstance()): Calendar {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal
}

/**
 * Returns Calendar object with 23:59:59:999 (HH:mm:ss:sss)
 *
 * @return Calendar object
 */
inline fun Calendar.getEndTimeOfDay(): Calendar {
    getEndTimeOfDay(this)
    return this
}

/**
 * Returns Calendar object with 23:59:59:999 (HH:mm:ss:sss)
 *
 * @param cal Calendar object
 * @return Calendar object
 */
inline fun Calendar.getEndTimeOfDay(cal: Calendar = Calendar.getInstance()): Calendar {
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal
}

/**
 * Checks whether two Calendar object are same or not?
 *
 * @param cal2 Second calendar object which needs to be compare with current calendar.
 * @return Boolean
 */
inline fun Calendar.isDateEqual(cal2: Calendar): Boolean {
    val cal1Temp = getStartTimeOfDay(this)
    val cal2Temp = getStartTimeOfDay(cal2)

    return cal1Temp == cal2Temp
}

/**
 * Check whether date is Future date or not
 *
 * @return boolean
 */
inline fun Calendar.isInTheFuture(): Boolean {
    val cal = Calendar.getInstance()
    return this.lt(cal)
}

/**
 * Check whether date is Past date or not
 *
 * @return boolean
 */
inline fun Calendar.isInThePast(): Boolean {
    val cal = Calendar.getInstance()
    return this.gt(cal)
}

/**
 * Return Calendar object with first date of the Current Month.
 *
 * @return Calendar object with first date of the Current Month.
 */
inline fun Calendar.getStartOfMonth(): Calendar {
    getStartOfMonth(this)
    return this
}

/**
 * Return Calendar object with first date of the Current Month.
 *
 * @param cal Calendar object
 * @return Calendar object with first date of the Current Month.
 */
inline fun Calendar.getStartOfMonth(cal: Calendar = Calendar.getInstance()): Calendar {
    cal.set(Calendar.DATE, 1)
    getStartTimeOfDay(cal)
    return cal
}

/**
 * Return Calendar object with last date of the Current Month.
 *
 * @return Calendar object with last date of the Current Month.
 */
inline fun Calendar.getLastOfMonth(): Calendar {
    getLastOfMonth(this)
    return this
}

/**
 * Return Calendar object with last date of the Current Month.
 *
 * @param cal Calendar object
 * @return Calendar object with last date of the Current Month.
 */
inline fun Calendar.getLastOfMonth(cal: Calendar = Calendar.getInstance()): Calendar {
    cal.add(Calendar.MONTH, 1)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.add(Calendar.DATE, -1)
    getStartTimeOfDay(cal)
    return cal
}

/**
 * Return Calendar object with First week of the Calendar.
 *
 * @return Calendar object with First week of the Calendar.
 */
inline fun Calendar.getFirstWeekDay(): Calendar {
    getFirstWeekDay(this)
    return this
}

/**
 * Return Calendar object with First week of the Calendar.
 *
 * @param cal Calendar object
 * @return Calendar object with First week of the Calendar.
 */
inline fun Calendar.getFirstWeekDay(cal: Calendar = Calendar.getInstance()): Calendar {
    cal.add(Calendar.DATE, -cal.get(Calendar.DAY_OF_WEEK) + 1)
    getStartTimeOfDay(cal)
    return this
}

/**
 * Return Calendar object with Last week of the Calendar.
 *
 * @return Calendar object with Last week of the Calendar.
 */
inline fun Calendar.getLastWeekDay(): Calendar {
    getLastWeekDay(this)
    return this
}

/**
 * Return Calendar object with Last week of the Calendar.
 *
 * @param cal Calendar object
 * @return Calendar object with Last week of the Calendar.
 */
inline fun Calendar.getLastWeekDay(cal: Calendar = Calendar.getInstance()): Calendar {
    getFirstWeekDay(cal)
    cal.plusDays(7 - 1)
    getStartTimeOfDay(cal)
    return this
}

/**
 * Get number of days from the given month
 *
 * @return Int
 */
inline fun Calendar.getNumDaysInMonth(): Int {
    return getActualMaximum(Calendar.DAY_OF_MONTH)
}

/**
 * Check whether year is a leap year.
 *
 * @return Returns true only if the year is a leap year.
 */
inline fun Calendar.isLeapYear(): Boolean {
    return getActualMaximum(Calendar.DAY_OF_YEAR) > 365
}

/**
 * Check whether calendar is less than the other or not.
 *
 * @return Returns boolean.
 */
inline fun Calendar.lt(cal: Calendar): Boolean {
    return this.timeInMillis < cal.timeInMillis
}

/**
 * Check whether calendar is greater than the other or not.
 *
 * @return Returns boolean.
 */
inline fun Calendar.gt(cal: Calendar): Boolean {
    return this.timeInMillis > cal.timeInMillis
}

/**
 * Check whether calendar is less than or equal to the other or not.
 *
 * @return Returns boolean.
 */
inline fun Calendar.ltEqual(cal: Calendar): Boolean {
    return this.timeInMillis <= cal.timeInMillis
}

/**
 * Check whether calendar is greater than or equal to the other or not.
 *
 * @return Returns boolean.
 */
inline fun Calendar.gtEqual(cal: Calendar): Boolean {
    return this.timeInMillis >= cal.timeInMillis
}

/**
 * Check whether date is in between of two calendar dates.
 *
 * @param cal1 Min calendar object
 * @param cal2 Max calendar object
 * @return true if date is in between of passed two calendar dates.
 */
inline fun Calendar.isInBetween(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.timeInMillis < this.timeInMillis && this.timeInMillis < cal2.timeInMillis
}

/**
 * Check whether date is not in between of two calendar dates.
 *
 * @param cal1 Min calendar object
 * @param cal2 Max calendar object
 * @return true if date is not in between of passed two calendar dates.
 */
inline fun Calendar.isNotInBetween(cal1: Calendar, cal2: Calendar): Boolean {
    return !isInBetween(cal1, cal2)
}

/**
 * Sets this Calendar's current time from the given long value.
 *
 * @param millis the new time in UTC milliseconds from the epoch.
 * @see #setTime(Date)
 * @see #getTimeInMillis()
 */
inline fun Calendar.setToSpecificDate(millis: Long): Calendar {
    var time = millis
    if (time < 1000000000000L) {
        time *= 1000
    }
    this.timeInMillis = time
    return this
}

//========================================== Convert Operations of Calendar ==========================================

/**
 * Convert to Calendar time into ago String
 *
 * @return String
 */
inline fun Calendar.getTimeAgoText(): CharSequence? {
    var time = timeInMillis
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = System.currentTimeMillis()
    if (time > now || time <= 0) {
        return null
    }

    val diff = now - time
    return when {
        diff < MINUTE_IN_MILLIS -> "just now"
        diff < 2 * MINUTE_IN_MILLIS -> "a minute ago"
        diff < 50 * MINUTE_IN_MILLIS -> (diff / MINUTE_IN_MILLIS).toString() + " minutes ago"
        diff < 90 * MINUTE_IN_MILLIS -> "an hour ago"
        diff < 24 * HOUR_IN_MILLIS -> (diff / HOUR_IN_MILLIS).toString() + " hours ago"
        diff < 48 * HOUR_IN_MILLIS -> "yesterday"
        else -> (diff / DAY_IN_MILLIS).toString() + " days ago"
    }
}

/**
 * Get Day name (Sun/Mon/Tue/Wed/Thu/Fri/Sat) from the calendar object.
 *
 * @param isLong Get long text of day name
 * @return String
 */
inline fun Calendar.getDayName(isLong: Boolean = false): String {
    return getDisplayName(Calendar.DAY_OF_WEEK, if (isLong) Calendar.LONG else Calendar.SHORT, Locale.getDefault())!!
}

/**
 * Convert long value of time into desired String format
 *
 * @param dateFormat String date format in which long needs to be converted
 * @return String
 */
inline fun convertLongToString(dateFormat: String, long: Long): String {
    val cal: Calendar = Calendar.getInstance()
    cal.setToSpecificDate(long)
    return cal.convertDate(dateFormat)
}

/**
 * Convert Calendar object into given DateFormat
 *
 * @param dateFormat Need format to convert into.
 * @return String value with passed dateFormat.
 */
inline fun Calendar.convertDate(dateFormat: String, isUTC: Boolean = false): String {
    val sourceFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    if (isUTC) {
        sourceFormat.timeZone = TimeZone.getTimeZone("UTC")
    }
    return sourceFormat.format(this.time)
}

/**
 * Convert Calendar String into given DateFormat
 *
 * @param strSourceFormat Source format of the passed sourceDate.
 * @param strDestinationFormat Destination format of given function.
 * @param sourceDate String date which need to be converted.
 * @param isUTC Is UTC formatted date needed?
 * @return String value with passed dateFormat.
 */
inline fun convertDateToUTCFormat(
    strSourceFormat: String,
    strDestinationFormat: String,
    sourceDate: String,
    isUTC: Boolean = false
): String {
    if (sourceDate.isEmpty()) {
        return ""
    }
    val fromFormatter = SimpleDateFormat(strSourceFormat, Locale.US)
    if (isUTC) {
        fromFormatter.timeZone = TimeZone.getTimeZone("UTC")
    }
    val toFormatter = SimpleDateFormat(strDestinationFormat, Locale.US)
    toFormatter.timeZone = TimeZone.getDefault()

    val date: Date?
    try {
        if (sourceDate.startsWith("-")) {
            val calTemp = Calendar.getInstance()
            calTemp.timeInMillis = sourceDate.toLong()
            if (isUTC) {
                calTemp.timeZone = TimeZone.getTimeZone("UTC")
            }
            date = calTemp.time
        } else if (TextUtils.isDigitsOnly(sourceDate)) {
            val calTemp = Calendar.getInstance()
            calTemp.timeInMillis = sourceDate.toLong()
            if (isUTC) {
                calTemp.timeZone = TimeZone.getTimeZone("UTC")
            }
            date = calTemp.time
        } else {
            date = fromFormatter.parse(sourceDate)
        }
        return toFormatter.format(date!!)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

inline fun Calendar.getMillis(): Long = this.timeInMillis
inline fun Calendar.getSeconds(): Int = get(Calendar.SECOND)
inline fun Calendar.getMinutes(): Int = get(Calendar.MINUTE)
inline fun Calendar.getHourOfDay(): Int = get(Calendar.HOUR_OF_DAY)
inline fun Calendar.getHour(): Int = get(Calendar.HOUR)
inline fun Calendar.getDay(): Int = get(Calendar.DATE)
inline fun Calendar.getMonth(): Int = (get(Calendar.MONTH) + 1)
inline fun Calendar.getYear(): Int = get(Calendar.YEAR)

inline fun Calendar.today(): Calendar = Calendar.getInstance()
inline fun Calendar.yesterday(): Calendar = removeDays(1)
inline fun Calendar.tomorrow(): Calendar = plusDays(1)

inline fun Calendar.nextDay(): Calendar = plusDays(1)
inline fun Calendar.nextWeek(): Calendar = plusWeeks(1)
inline fun Calendar.nextMonth(): Calendar = plusMonths(1)
inline fun Calendar.nextYear(): Calendar = plusYears(1)

inline fun Calendar.lastDay(): Calendar = removeDays(1)
inline fun Calendar.lastWeek(): Calendar = removeWeeks(1)
inline fun Calendar.lastMonth(): Calendar = removeMonths(1)
inline fun Calendar.lastYear(): Calendar = removeYears(1)

inline fun Calendar.isMonday() = Calendar.DAY_OF_WEEK == Calendar.MONDAY
inline fun Calendar.isTuesday() = Calendar.DAY_OF_WEEK == Calendar.TUESDAY
inline fun Calendar.isWednesday() = Calendar.DAY_OF_WEEK == Calendar.WEDNESDAY
inline fun Calendar.isThursday() = Calendar.DAY_OF_WEEK == Calendar.THURSDAY
inline fun Calendar.isFriday() = Calendar.DAY_OF_WEEK == Calendar.FRIDAY
inline fun Calendar.isSaturday() = Calendar.DAY_OF_WEEK == Calendar.SATURDAY
inline fun Calendar.isSunday() = Calendar.DAY_OF_WEEK == Calendar.SUNDAY

inline fun Calendar.toString() = convertDate(DATE_FORMAT_DD_MM_YYYY_HH_MM_SS)

//========================================
inline fun Calendar.add(millis: Long): Calendar {
    var l = millis
    if (l.toString().length <= 10) {
        l *= 1000
    }
    timeInMillis += l
    return this
}

inline fun Calendar.remove(millis: Long): Calendar {
    var l = millis
    if (l.toString().length <= 10) {
        l *= 1000
    }
    timeInMillis -= l
    return this
}

inline fun Int.days(): Long {
    return (this * DAY_IN_SECONDS)
}

inline fun Int.hours(): Long {
    return (this * HOUR_IN_SECONDS)
}

inline fun Int.minutes(): Long {
    return (this * MINUTE_IN_SECONDS)
}

inline fun Calendar.toLongString(isUTC: Boolean = false): String {
    return convertDate(DATE_FORMAT_DD_MM_YYYY_HH_MM_SS, isUTC)
}

inline fun Calendar.toShortString(isUTC: Boolean = false): String {
    return convertDate(DATE_FORMAT_DD_MM_YYYY, isUTC)
}

inline operator fun Calendar.plus(i: Long): Calendar {
    return this.add(i)
}

inline operator fun Calendar.minus(i: Long): Calendar {
    return this.remove(i)
}