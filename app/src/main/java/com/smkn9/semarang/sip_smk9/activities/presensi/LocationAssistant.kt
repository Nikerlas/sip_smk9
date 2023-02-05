// https://github.com/klaasnotfound/LocationAssistant
/*
 *    Copyright 2017 Klaas Klasing (klaas [at] klaasnotfound.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.presensi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
/**
 * A helper class that monitors the available location info on behalf of a requesting activity or application.
 */
class LocationAssistant(
    // Parameters
    protected var context: Context,
    listener: Listener?,
    accuracy: Accuracy?,
    updateInterval: Long,
    allowMockLocations: Boolean
) : ConnectionCallbacks, OnConnectionFailedListener,
    LocationListener {
    /**
     * Delivers relevant events required to obtain (valid) location info.
     */
    interface Listener {
        /**
         * Called when the user needs to grant the app location permission at run time.
         * This is only necessary on newer Android systems (API level >= 23).
         * If you want to show some explanation up front, do that, then call [.requestLocationPermission].
         * Alternatively, you can call [.requestAndPossiblyExplainLocationPermission], which will request the
         * location permission right away and invoke [.onExplainLocationPermission] only if the user declines.
         * Both methods will bring up the system permission dialog.
         */
        fun onNeedLocationPermission()

        /**
         * Called when the user has declined the location permission and might need a better explanation as to why
         * your app really depends on it.
         * You can show some sort of dialog or info window here and then - if the user is willing - ask again for
         * permission with [.requestLocationPermission].
         */
        fun onExplainLocationPermission()

        /**
         * Called when the user has declined the location permission at least twice or has declined once and checked
         * "Don't ask again" (which will cause the system to permanently decline it).
         * You can show some sort of message that explains that the user will need to go to the app settings
         * to enable the permission. You may use the preconfigured OnClickListeners to send the user to the app
         * settings page.
         *
         * @param fromView   OnClickListener to use with a view (e.g. a button), jumps to the app settings
         * @param fromDialog OnClickListener to use with a dialog, jumps to the app settings
         */
        fun onLocationPermissionPermanentlyDeclined(
            fromView: View.OnClickListener?,
            fromDialog: DialogInterface.OnClickListener?
        )

        /**
         * Called when a change of the location provider settings is necessary.
         * You can optionally show some informative dialog and then request the settings change with
         * [.changeLocationSettings].
         */
        fun onNeedLocationSettingsChange()

        /**
         * In certain cases where the user has switched off location providers, changing the location settings from
         * within the app may not work. The LocationAssistant will attempt to detect these cases and offer a redirect to
         * the system location settings, where the user may manually enable on location providers before returning to
         * the app.
         * You can prompt the user with an appropriate message (in a view or a dialog) and use one of the provided
         * OnClickListeners to jump to the settings.
         *
         * @param fromView   OnClickListener to use with a view (e.g. a button), jumps to the location settings
         * @param fromDialog OnClickListener to use with a dialog, jumps to the location settings
         */
        fun onFallBackToSystemSettings(
            fromView: View.OnClickListener?,
            fromDialog: DialogInterface.OnClickListener?
        )

        /**
         * Called when a new and valid location is available.
         * If you chose to reject mock locations, this method will only be called when a real location is available.
         *
         * @param location the current user location
         */
        fun onNewLocationAvailable(location: Location?)

        /**
         * Called when the presence of mock locations was detected and [.allowMockLocations] is `false`.
         * You can use this callback to scold the user or do whatever. The user can usually disable mock locations by
         * either switching off a running mock location app (on newer Android systems) or by disabling mock location
         * apps altogether. The latter can be done in the phone's development settings. You may show an appropriate
         * message and then use one of the provided OnClickListeners to jump to those settings.
         *
         * @param fromView   OnClickListener to use with a view (e.g. a button), jumps to the development settings
         * @param fromDialog OnClickListener to use with a dialog, jumps to the development settings
         */
        fun onMockLocationsDetected(
            fromView: View.OnClickListener?,
            fromDialog: DialogInterface.OnClickListener?
        )

        /**
         * Called when an error has occurred.
         *
         * @param type    the type of error that occurred
         * @param message a plain-text message with optional details
         */
        fun onError(
            type: ErrorType?,
            message: String?
        )
    }

    /**
     * Possible values for the desired location accuracy.
     */
    enum class Accuracy {
        /**
         * Highest possible accuracy, typically within 30m
         */
        HIGH,

        /**
         * Medium accuracy, typically within a city block / roughly 100m
         */
        MEDIUM,

        /**
         * City-level accuracy, typically within 10km
         */
        LOW,

        /**
         * Variable accuracy, purely dependent on updates requested by other apps
         */
        PASSIVE
    }

    enum class ErrorType {
        /**
         * An error with the user's location settings
         */
        SETTINGS,

        /**
         * An error with the retrieval of location info
         */
        RETRIEVAL
    }

    private val REQUEST_CHECK_SETTINGS = 0
    private val REQUEST_LOCATION_PERMISSION = 1

    private var activity: Activity? = null
    private var listener: Listener?
    private var priority = 0
    private val updateInterval: Long
    private val allowMockLocations: Boolean
    private var verbose = false
    private var quiet = false

    // Internal state
    private var permissionGranted = false
    private var locationRequested = false
    private var locationStatusOk = false
    private var changeSettings = false
    private var updatesRequested = false

    /**
     * Returns the best valid location currently available.
     * Usually, this will be the last valid location that was received.
     *
     * @return the best valid location
     */
    var bestLocation: Location? = null
        protected set
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationStatus: Status? = null
    private var mockLocationsEnabled = false
    private var numTimesPermissionDeclined = 0

    // Mock location rejection
    private var lastMockLocation: Location? = null
    private var numGoodReadings = 0

    /**
     * Makes the LocationAssistant print info log messages.
     *
     * @param verbose whether or not the LocationAssistant should print verbose log messages.
     */
    fun setVerbose(verbose: Boolean) {
        this.verbose = verbose
    }

    /**
     * Mutes/unmutes all log output.
     * You may want to mute the LocationAssistant in production.
     *
     * @param quiet whether or not to disable all log output (including errors).
     */
    fun setQuiet(quiet: Boolean) {
        this.quiet = quiet
    }

    /**
     * Starts the LocationAssistant and makes it subscribe to valid location updates.
     * Call this method when your application or activity becomes awake.
     */
    fun start() {
        checkMockLocations()
        googleApiClient!!.connect()
    }

    /**
     * Updates the active Activity for which the LocationAssistant manages location updates.
     * When you want the LocationAssistant to start and stop with your overall application, but service different
     * activities, call this method at the end of your  implementation.
     *
     * @param activity the activity that wants to receive location updates
     * @param listener a listener that will receive location-related events
     */
    fun register(
        activity: Activity?,
        listener: Listener?
    ) {
        this.activity = activity
        this.listener = listener
        checkInitialLocation()
        acquireLocation()
    }

    /**
     * Stops the LocationAssistant and makes it unsubscribe from any location updates.
     * Call this method right before your application or activity goes to sleep.
     */
    fun stop() {
        if (googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
        permissionGranted = false
        locationRequested = false
        locationStatusOk = false
        updatesRequested = false
    }

    /**
     * Clears the active Activity and its listener.
     * Until you register a new activity and listener, the LocationAssistant will silently produce error messages.
     * When you want the LocationAssistant to start and stop with your overall application, but service different
     * activities, call this method at the beginning of your  implementation.
     */
    fun unregister() {
        activity = null
        listener = null
    }

    /**
     * In rare cases (e.g. after losing connectivity) you may want to reset the LocationAssistant and have it start
     * from scratch. Use this method to do so.
     */
    fun reset() {
        permissionGranted = false
        locationRequested = false
        locationStatusOk = false
        updatesRequested = false
        acquireLocation()
    }

    /**
     * The first time you call this method, it brings up a system dialog asking the user to give location permission to
     * the app. On subsequent calls, if the user has previously declined permission, this method invokes
     * [Listener.onExplainLocationPermission].
     */
    fun requestAndPossiblyExplainLocationPermission() {
        if (permissionGranted) return
        if (activity == null) {
            if (!quiet) Log.e(
                javaClass.simpleName,
                "Need location permission, but no activity is registered! " +
                        "Specify a valid activity when constructing " + javaClass.simpleName +
                        " or register it explicitly with register()."
            )
            return
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            && listener != null
        ) listener!!.onExplainLocationPermission() else requestLocationPermission()
    }

    /**
     * Brings up a system dialog asking the user to give location permission to the app.
     */
    fun requestLocationPermission() {
        if (activity == null) {
            if (!quiet) Log.e(
                javaClass.simpleName,
                "Need location permission, but no activity is registered! " +
                        "Specify a valid activity when constructing " + javaClass.simpleName +
                        " or register it explicitly with register()."
            )
            return
        }
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    /**
     * Call this method at the end of your [Activity.onRequestPermissionsResult] implementation to notify the
     * LocationAssistant of an update in permissions.
     *
     * @param requestCode  the request code returned to the activity (simply pass it on)
     * @param grantResults the results array returned to the activity (simply pass it on)
     * @return `true` if the location permission was granted, `false` otherwise
     */
    fun onPermissionsUpdated(requestCode: Int, grantResults: IntArray): Boolean {
        if (requestCode != REQUEST_LOCATION_PERMISSION) return false
        return if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            acquireLocation()
            true
        } else {
            numTimesPermissionDeclined++
            if (!quiet) Log.i(
                javaClass.simpleName,
                "Location permission request denied."
            )
            if (numTimesPermissionDeclined >= 2 && listener != null) listener!!.onLocationPermissionPermanentlyDeclined(
                onGoToAppSettingsFromView,
                onGoToAppSettingsFromDialog
            )
            false
        }
    }

    /**
     * Call this method at the end of your  implementation to notify the
     * LocationAssistant of a change in location provider settings.
     *
     * @param requestCode the request code returned to the activity (simply pass it on)
     * @param resultCode  the result code returned to the activity (simply pass it on)
     */
    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode != REQUEST_CHECK_SETTINGS) return
        if (resultCode == Activity.RESULT_OK) {
            changeSettings = false
            locationStatusOk = true
        }
        acquireLocation()
    }

    /**
     * Brings up an in-app system dialog that requests a change in location provider settings.
     * The settings change may involve switching on GPS and/or network providers and depends on the accuracy and
     * update interval that was requested when constructing the LocationAssistant.
     * Call this method only from within [Listener.onNeedLocationSettingsChange].
     */
    fun changeLocationSettings() {
        if (locationStatus == null) return
        if (activity == null) {
            if (!quiet) Log.e(
                javaClass.simpleName,
                "Need to resolve location status issues, but no activity is " +
                        "registered! Specify a valid activity when constructing " + javaClass.simpleName +
                        " or register it explicitly with register()."
            )
            return
        }
        try {
            locationStatus!!.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
        } catch (e: SendIntentException) {
            if (!quiet) Log.e(
                javaClass.simpleName,
                """
                    Error while attempting to resolve location status issues:
                    $e
                    """.trimIndent()
            )
            if (listener != null) listener!!.onError(
                ErrorType.SETTINGS,
                """
                    Could not resolve location settings issue:
                    ${e.message}
                    """.trimIndent()
            )
            changeSettings = false
            acquireLocation()
        }
    }

    protected fun acquireLocation() {
        if (!permissionGranted) checkLocationPermission()
        if (!permissionGranted) {
            if (numTimesPermissionDeclined >= 2) return
            if (listener != null) listener!!.onNeedLocationPermission() else if (!quiet) Log.e(
                javaClass.simpleName,
                "Need location permission, but no listener is registered! " +
                        "Specify a valid listener when constructing " + javaClass.simpleName +
                        " or register it explicitly with register()."
            )
            return
        }
        if (!locationRequested) {
            requestLocation()
            return
        }
        if (!locationStatusOk) {
            if (changeSettings) {
                if (listener != null) listener!!.onNeedLocationSettingsChange() else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need location settings change, but no listener is " +
                            "registered! Specify a valid listener when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            } else checkProviders()
            return
        }
        if (!updatesRequested) {
            requestLocationUpdates()
            // Check back in a few
            Handler().postDelayed({ acquireLocation() }, 10000)
            return
        }
        if (!checkLocationAvailability()) {
            // Something is wrong - probably the providers are disabled.
            checkProviders()
        }
    }

    protected fun checkInitialLocation() {
        if (!googleApiClient!!.isConnected || !permissionGranted || !locationRequested || !locationStatusOk) return
        try {
            val location =
                LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
            onLocationChanged(location)
        } catch (e: SecurityException) {
            if (!quiet) Log.e(
                javaClass.simpleName, """Error while requesting last location:
 $e"""
            )
            if (listener != null) listener!!.onError(
                ErrorType.RETRIEVAL,
                """
                    Could not retrieve initial location:
                    ${e.message}
                    """.trimIndent()
            )
        }
    }

    private fun checkMockLocations() {
        // Starting with API level >= 18 we can (partially) rely on .isFromMockProvider()
        // (http://developer.android.com/reference/android/location/Location.html#isFromMockProvider%28%29)
        // For API level < 18 we have to check the Settings.Secure flag
        if (Build.VERSION.SDK_INT < 18 &&
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION
            ) != "0"
        ) {
            mockLocationsEnabled = true
            if (listener != null) listener!!.onMockLocationsDetected(
                onGoToDevSettingsFromView,
                onGoToDevSettingsFromDialog
            )
        } else mockLocationsEnabled = false
    }

    private fun checkLocationPermission() {
        permissionGranted = Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocation() {
        if (!googleApiClient!!.isConnected || !permissionGranted) return
        locationRequest = LocationRequest.create()
        locationRequest?.setPriority(priority)
        locationRequest?.setInterval(updateInterval)
        locationRequest?.setFastestInterval(updateInterval)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)
        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            .setResultCallback(onLocationSettingsReceived)
    }

    private fun checkLocationAvailability(): Boolean {
        return if (!googleApiClient!!.isConnected || !permissionGranted) false else try {
            val la =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient)
            la != null && la.isLocationAvailable
        } catch (e: SecurityException) {
            if (!quiet) Log.e(
                javaClass.simpleName,
                "Error while checking location availability:\n $e"
            )
            if (listener != null) listener!!.onError(
                ErrorType.RETRIEVAL,
                """
                Could not check location availability:
                ${e.message}
                """.trimIndent()
            )
            false
        }
    }

    private fun checkProviders() {
        // Do it the old fashioned way
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (gps || network) return
        if (listener != null) listener!!.onFallBackToSystemSettings(
            onGoToLocationSettingsFromView,
            onGoToLocationSettingsFromDialog
        ) else if (!quiet) Log.e(
            javaClass.simpleName,
            "Location providers need to be enabled, but no listener is " +
                    "registered! Specify a valid listener when constructing " + javaClass.simpleName +
                    " or register it explicitly with register()."
        )
    }

    private fun requestLocationUpdates() {
        if (!googleApiClient!!.isConnected || !permissionGranted || !locationRequested) return
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                this
            )
            updatesRequested = true
        } catch (e: SecurityException) {
            if (!quiet) Log.e(
                javaClass.simpleName, """Error while requesting location updates:
 $e"""
            )
            if (listener != null) listener!!.onError(
                ErrorType.RETRIEVAL,
                """
                    Could not request location updates:
                    ${e.message}
                    """.trimIndent()
            )
        }
    }

    private val onGoToLocationSettingsFromDialog: DialogInterface.OnClickListener =
        object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if (activity != null) {
                    val intent =
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activity!!.startActivity(intent)
                } else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need to launch an intent, but no activity is registered! " +
                            "Specify a valid activity when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            }
        }
    private val onGoToLocationSettingsFromView: View.OnClickListener =
        object : View.OnClickListener {
            override fun onClick(v: View) {
                if (activity != null) {
                    val intent =
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    activity!!.startActivity(intent)
                } else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need to launch an intent, but no activity is registered! " +
                            "Specify a valid activity when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            }
        }
    private val onGoToDevSettingsFromDialog: DialogInterface.OnClickListener =
        object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if (activity != null) {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    activity!!.startActivity(intent)
                } else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need to launch an intent, but no activity is registered! " +
                            "Specify a valid activity when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            }
        }
    private val onGoToDevSettingsFromView: View.OnClickListener =
        object : View.OnClickListener {
            override fun onClick(v: View) {
                if (activity != null) {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    activity!!.startActivity(intent)
                } else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need to launch an intent, but no activity is registered! " +
                            "Specify a valid activity when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            }
        }
    private val onGoToAppSettingsFromDialog: DialogInterface.OnClickListener =
        object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if (activity != null) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri =
                        Uri.fromParts("package", activity!!.packageName, null)
                    intent.data = uri
                    activity!!.startActivity(intent)
                } else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need to launch an intent, but no activity is registered! " +
                            "Specify a valid activity when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            }
        }
    private val onGoToAppSettingsFromView: View.OnClickListener =
        object : View.OnClickListener {
            override fun onClick(v: View) {
                if (activity != null) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri =
                        Uri.fromParts("package", activity!!.packageName, null)
                    intent.data = uri
                    activity!!.startActivity(intent)
                } else if (!quiet) Log.e(
                    javaClass.simpleName,
                    "Need to launch an intent, but no activity is registered! " +
                            "Specify a valid activity when constructing " + javaClass.simpleName +
                            " or register it explicitly with register()."
                )
            }
        }

    private fun isLocationPlausible(location: Location?): Boolean {
        if (location == null) return false
        val isMock =
            mockLocationsEnabled || Build.VERSION.SDK_INT >= 18 && location.isFromMockProvider
        if (isMock) {
            lastMockLocation = location
            numGoodReadings = 0
        } else numGoodReadings =
            Math.min(numGoodReadings + 1, 1000000) // Prevent overflow

        // We only clear that incident record after a significant show of good behavior
        if (numGoodReadings >= 20) lastMockLocation = null

        // If there's nothing to compare against, we have to trust it
        if (lastMockLocation == null) return true

        // And finally, if it's more than 1km away from the last known mock, we'll trust it
        val d = location.distanceTo(lastMockLocation).toDouble()
        return d > 1000
    }

    override fun onConnected(bundle: Bundle?) {
        acquireLocation()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onLocationChanged(location: Location) {
        if (location == null) return
        val plausible = isLocationPlausible(location)
        if (verbose && !quiet) Log.i(
            javaClass.simpleName, location.toString() +
                    if (plausible) " -> plausible" else " -> not plausible"
        )
        if (!allowMockLocations && !plausible) {
            if (listener != null) listener!!.onMockLocationsDetected(
                onGoToDevSettingsFromView,
                onGoToDevSettingsFromDialog
            )
            return
        }
        bestLocation = location
        if (listener != null) listener!!.onNewLocationAvailable(location) else if (!quiet) Log.w(
            javaClass.simpleName,
            """New location is available, but no listener is registered!
Specify a valid listener when constructing ${javaClass.simpleName} or register it explicitly with register()."""
        )
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (!quiet) Log.e(
            javaClass.simpleName, """
     Error while trying to connect to Google API:
     ${connectionResult.errorMessage}
     """.trimIndent()
        )
        if (listener != null) listener!!.onError(
            ErrorType.RETRIEVAL,
            """
                Could not connect to Google API:
                ${connectionResult.errorMessage}
                """.trimIndent()
        )
    }

    var onLocationSettingsReceived =
        ResultCallback<LocationSettingsResult> { result ->
            locationRequested = true
            locationStatus = result.status
            when (locationStatus?.getStatusCode()) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    locationStatusOk = true
                    checkInitialLocation()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    locationStatusOk = false
                    changeSettings = true
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> locationStatusOk =
                    false
            }
            acquireLocation()
        }

    /**
     * Constructs a LocationAssistant instance that will listen for valid location updates.
     *
     * @param context            the context of the application or activity that wants to receive location updates
     * @param listener           a listener that will receive location-related events
     * @param accuracy           the desired accuracy of the loation updates
     * @param updateInterval     the interval (in milliseconds) at which the activity can process updates
     * @param allowMockLocations whether or not mock locations are acceptable
     */
    init {
        if (context is Activity) activity = context as Activity
        this.listener = listener
        priority = when (accuracy) {
            Accuracy.HIGH -> LocationRequest.PRIORITY_HIGH_ACCURACY
            Accuracy.MEDIUM -> LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            Accuracy.LOW -> LocationRequest.PRIORITY_LOW_POWER
            Accuracy.PASSIVE -> LocationRequest.PRIORITY_NO_POWER
            else -> LocationRequest.PRIORITY_NO_POWER
        }
        this.updateInterval = updateInterval
        this.allowMockLocations = allowMockLocations

        // Set up the Google API client
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }
}