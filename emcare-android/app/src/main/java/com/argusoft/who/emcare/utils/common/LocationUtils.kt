package com.argusoft.who.emcare.utils.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.Fragment
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.utils.extention.showSnackBar
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.ref.WeakReference

object LocationUtils : EasyPermissions.PermissionCallbacks {

    private const val REQUEST_PERMISSIONS_LOCATION_CODE = 666
    internal const val REQUEST_CHECK_SETTINGS = 101
    private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000L
    private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2L

    private var weakReferenceActivity: WeakReference<Activity?>? = null
    private var weakReferenceFragment: WeakReference<Fragment?>? = null
    private var weakReferenceFusedLocationClient: WeakReference<FusedLocationProviderClient>? = null
    private var weakReferenceSettingsClient: WeakReference<SettingsClient>? = null
    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null
    private var listener: LocationListener? = null
    private var isOneTimeFetch: Boolean = false

    private var resolutionForResult: ActivityResultLauncher<IntentSenderRequest>? = null
    private fun getActivity() = weakReferenceActivity?.get() ?: weakReferenceFragment?.get()?.activity

    fun fetchLocation(activity: Activity?, resolutionForResult: ActivityResultLauncher<IntentSenderRequest>, listener: LocationListener, isOneTimeFetch: Boolean = false): LocationUtils {
        weakReferenceActivity = WeakReference(activity)
        this.listener = listener
        this.isOneTimeFetch = isOneTimeFetch
        this.resolutionForResult = resolutionForResult
        requestLocation()
        return this
    }

    fun fetchLocation(fragment: Fragment?, resolutionForResult: ActivityResultLauncher<IntentSenderRequest>, listener: LocationListener, isOneTimeFetch: Boolean = false): LocationUtils {
        weakReferenceFragment = WeakReference(fragment)
        this.listener = listener
        this.isOneTimeFetch = isOneTimeFetch
        this.resolutionForResult = resolutionForResult
        requestLocation()
        return this
    }

    @AfterPermissionGranted(REQUEST_PERMISSIONS_LOCATION_CODE)
    fun requestLocation() {
        if (hasLocationPermission() == true) {
            startLocationRequest()
        } else {
            weakReferenceActivity?.get()?.let { activity ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    EasyPermissions.requestPermissions(
                        activity,
                        activity.getString(R.string.rationale_location),
                        REQUEST_PERMISSIONS_LOCATION_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                else EasyPermissions.requestPermissions(
                    activity,
                    activity.getString(R.string.rationale_location),
                    REQUEST_PERMISSIONS_LOCATION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            weakReferenceFragment?.get()?.let { fragment ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    EasyPermissions.requestPermissions(
                        fragment,
                        fragment.getString(R.string.rationale_location),
                        REQUEST_PERMISSIONS_LOCATION_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                else EasyPermissions.requestPermissions(
                    fragment,
                    fragment.getString(R.string.rationale_location),
                    REQUEST_PERMISSIONS_LOCATION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }

    /**
     * Location specific permission checkup
     */
    private fun hasLocationPermission(): Boolean? {
        return getActivity()?.let {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (hasForegroundPermissionAllow() == true) true
                else
                    hasBackgroundPermissionAllow()
            } else
                hasForegroundPermissionAllow()
        }
    }

    private fun hasForegroundPermissionAllow(): Boolean? {
        return getActivity()?.let {
            EasyPermissions.hasPermissions(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    @SuppressLint("InlinedApi")
    private fun hasBackgroundPermissionAllow(): Boolean? {
        return getActivity()?.let {
            EasyPermissions.hasPermissions(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun setupLocationCallback() {
        getActivity()?.let {
            weakReferenceFusedLocationClient = WeakReference(LocationServices.getFusedLocationProviderClient(it))
            weakReferenceSettingsClient = WeakReference(LocationServices.getSettingsClient(it))

            locationRequest = LocationRequest.create().apply {
                this.interval = UPDATE_INTERVAL_IN_MILLISECONDS
                this.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
                this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest!!)
            locationSettingsRequest = builder.build()

            /**
             * Creates a callback for receiving location events.
             */
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.lastLocation.let { currentLocation ->
                        listener?.onLocationChanged(currentLocation)
                        if (isOneTimeFetch) removeLocationUpdate()
                    }
                }
            }
        }
    }

    private fun startLocationRequest() {
        setupLocationCallback()
        getActivity()?.let {
            weakReferenceSettingsClient?.get()?.checkLocationSettings(locationSettingsRequest!!)?.addOnSuccessListener(it) {
                establishLocationUpdate()
            }?.addOnFailureListener(it) { e ->
                when ((e as? ResolvableApiException)?.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                            resolutionForResult?.launch(intentSenderRequest)
                        } catch (sie: IntentSender.SendIntentException) {
                            displayMessage("PendingIntent unable to execute request.")
                        }

                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        displayMessage("Location settings are inadequate, and cannot be fixed here. Fix in Settings.")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun establishLocationUpdate() {
        listener?.onStartLocationFetch()
        weakReferenceFusedLocationClient?.get()?.lastLocation?.addOnSuccessListener {
            it?.let {
                listener?.onLocationChanged(it)
                if (isOneTimeFetch) removeLocationUpdate()
            }
        }
        weakReferenceFusedLocationClient?.get()?.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.myLooper()!!
        )
    }

    private fun stopLocationRequest() {
        locationCallback?.let { weakReferenceFusedLocationClient?.get()?.removeLocationUpdates(it) }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        getActivity()?.let {
            if (EasyPermissions.somePermissionPermanentlyDenied(it, perms)) {
                AppSettingsDialog.Builder(getActivity()!!).build().show()
            }
            if (perms.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && hasForegroundPermissionAllow() == true) {
                requestLocation()
            }
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK ->
                    establishLocationUpdate()
                Activity.RESULT_CANCELED -> {
                    displayMessage("User choose not to make required location settings changes.")
                }
            }
        }
    }

    private fun displayMessage(message: String) {
        getActivity()?.let {
            it.showSnackBar(
                it.findViewById(android.R.id.content),
                message
            )
        }
    }

    fun removeLocationUpdate() {
        stopLocationRequest()
        weakReferenceFusedLocationClient?.clear()
        weakReferenceFusedLocationClient = null
        weakReferenceSettingsClient?.clear()
        weakReferenceSettingsClient = null
        weakReferenceActivity?.clear()
        weakReferenceActivity = null
        weakReferenceFragment?.clear()
        weakReferenceFragment = null
    }

    interface LocationListener {
        fun onStartLocationFetch()

        fun onLocationChanged(location: Location)
    }
}



