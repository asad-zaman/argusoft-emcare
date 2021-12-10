package com.argusoft.who.emcare.utils.extention

/**
 * USAGE : To get device related information.
 * Created by
 */

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import java.io.UnsupportedEncodingException
import java.util.*
import java.net.SocketException

import java.net.NetworkInterface

import java.util.Collections


fun Int.dpToPx(): Int = (this * getDisplayMetrics().density).toInt()

fun Int.pxToDp(): Int = (this / getDisplayMetrics().density).toInt()

fun getWidthPixels() = getDisplayMetrics().widthPixels

fun getHeightPixels() = getDisplayMetrics().heightPixels

fun getDisplayMetrics(): DisplayMetrics = Resources.getSystem().displayMetrics

fun getDeviceName(): String = Build.BRAND + " " + Build.MODEL

fun getDeviceModel(): String = Build.MODEL

fun getDeviceOS(): String {
    val builder = StringBuilder()
    builder.append("android : ").append(Build.VERSION.RELEASE)

    val fields = Build.VERSION_CODES::class.java.fields
    for (field in fields) {
        val fieldName = field.name
        var fieldValue = -1

        try {
            fieldValue = field.getInt(Any())
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        if (fieldValue == Build.VERSION.SDK_INT) {
            builder.append(" : ").append(fieldName).append(" : ")
            builder.append("sdk=").append(fieldValue)
        }
    }
    return builder.toString()
}

@SuppressLint("HardwareIds", "MissingPermission")
fun Context.getDeviceUUID(): UUID? {
    var uuid: UUID? = null

    val prefs = this.getSharedPreferences("device_id.xml", 0)
    val id = prefs.getString("device_id", null)

    if (id != null) {
        // Use the ids previously computed and stored in the prefs file
        uuid = UUID.fromString(id)

    } else {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number which we store
        // to a prefs file
        try {
            if ("9774d56d682e549c" != androidId) {
                uuid = UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8")))
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    val deviceId = (this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
                    uuid =
                        if (deviceId != null) {
                            UUID.nameUUIDFromBytes(deviceId.toByteArray(charset("utf8")))
                        } else {
                            UUID.randomUUID()
                        }
                } else {
                    throw RuntimeException("Require Read Phone State Permission")
                }
            }
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
        // Write the value out to the prefs file
        prefs.edit().putString("device_id", uuid!!.toString()).apply()
    }
    return uuid
}

fun Context.getIMEI(): String {
    val telephonyManager: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        telephonyManager.deviceId
    } else {
        telephonyManager.deviceId
    }
}

fun getMacAddress(): String {
    try {
        val all: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (nif in all) {
            if (!nif.name.equals("wlan0", ignoreCase = true)) continue
            val macBytes = nif.hardwareAddress ?: return ""
            val res1 = StringBuilder()
            for (b in macBytes) {
                res1.append(String.format("%02X:", b))
            }
            if (res1.length > 0) {
                res1.deleteCharAt(res1.length - 1)
            }
            return res1.toString()
        }
    } catch (ex: Exception) {
        return "02:00:00:00:00:00"
    }
    return "02:00:00:00:00:00"
}