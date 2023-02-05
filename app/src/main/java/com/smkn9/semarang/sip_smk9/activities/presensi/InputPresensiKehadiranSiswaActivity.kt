@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.presensi

import `in`.mayanknagwanshi.imagepicker.ImageSelectActivity
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.inforoeste.mocklocationdetector.MockLocationDetector
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.Tanggal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_input_presensi_kehadiran_siswa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.util.*

//class InputPresensiKehadiranSiswaActivity : AppCompatActivity(),LocationAssistant.Listener {
class InputPresensiKehadiranSiswaActivity : AppCompatActivity(){
//    private var assistant: LocationAssistant? = null
    lateinit var jenisPresensi: String
    var latAcuan: Double? = null
    var lngAcuan: Double? = null
    var jarak: Int? = null
    var locationText = ""
    var locationLatitude: Double? = null
    var locationLongitude: Double? = null
    lateinit var service: ServiceClient
    lateinit var sp: SharedPreferences
    lateinit var urlPresensiSiswa: String
    lateinit var nis: String
    lateinit var pesanA: String
    lateinit var nama: String


    // location last updated time
    private var mLastUpdateTime: String? = null

    // location updates interval - 10sec
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

    private val REQUEST_CHECK_SETTINGS = 100

    // bunch of location related apis
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null

    lateinit var androidId:String
    lateinit var lokasiPresensi:String
    lateinit var noWa:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_presensi_kehadiran_siswa)

        urlPresensiSiswa = Siswa.getLinkJurnalKelas(parent_input_hadir_pulang)
        nama = Siswa.getNamaSiswa(parent_input_hadir_pulang)
        nis = Siswa.getNIS(parent_input_hadir_pulang)
        jenisPresensi = intent.getStringExtra(Constant.BUNDLE_DETAIL).toString()
        latAcuan = intent.getStringExtra("lat")?.toDouble()
        lngAcuan = intent.getStringExtra("lng")?.toDouble()
        jarak = intent.getIntExtra("jarak", 0)
        lokasiPresensi = intent.getStringExtra(Constant.SISWA_LOKASI_PRESENSI).toString()
        noWa = intent.getStringExtra("wa").toString()
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        initUi()
        init()


        service = ServiceNetwork.getService(parent_input_hadir_pulang)


        btn_ambil_foto.setOnClickListener {
            val intent = Intent(this, ImageSelectActivity::class.java)
            intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true) //default is true

            intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true) //default is true

            intent.putExtra(ImageSelectActivity.FLAG_GALLERY, false) //default is true

            intent.putExtra(ImageSelectActivity.FLAG_CROP, false) //default is false

            startActivityForResult(intent, 1213)


        }

        btn_lokasi.setOnClickListener {
            startLocationButtonClick()
        }

        btn_kirim.setOnClickListener {
            if(mCurrentLocation!=null){
                val isMock = MockLocationDetector.isLocationFromMockProvider(this, mCurrentLocation)
                if(isMock){
                    toast("Maaf sistem GPS Palsu terdeteksi di HP ini")
                    return@setOnClickListener
                }else{
                    locationLatitude = mCurrentLocation?.latitude
                    locationLongitude = mCurrentLocation?.longitude
                }
            }else{
                toast("Lokasi belum didapatkan tunggu beberapa detik lagi")
                return@setOnClickListener
            }

            val jarakHitung =
                getDistance(latAcuan, lngAcuan, locationLatitude, locationLongitude)

            if (jarakHitung > jarak!!) {
                toast("Maaf Anda berada di luar lokasi Presensi")
                return@setOnClickListener
            }

            inputPresensiHadirPulang()

//            startActivity(intentFor<MainActivity>(
//                Constant.BUNDLE_DETAIL to jenisPresensi,
//                Constant.SISWA_LOKASI_PRESENSI to lokasiPresensi,
//                "wa" to noWa
//
//            ))
//            finish()

        }
    }

//    override fun onResume() {
//        assistant = LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 5000, false)
//        assistant?.setVerbose(true)
//        assistant!!.start()
//        super.onResume()
//    }

    override fun onPause() {
        // pausing location updates
        stopLocationUpdates()
//        assistant!!.stop()
//        finish()
        super.onPause()
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun initUi() {
        tv_nis_presensi.text = nis
        tv_mode_presensi.text = jenisPresensi
    }

    private fun init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                mCurrentLocation = locationResult.lastLocation
                mLastUpdateTime =
                        DateFormat.getTimeInstance().format(Date())
//                updateLocationUI()
            }
        }
//        mRequestingLocationUpdates = false
        mLocationRequest = LocationRequest()
        mLocationRequest?.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest?.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1213 && resultCode == RESULT_OK){
            val filePath = data?.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH)
            val selectedImage = BitmapFactory.decodeFile(filePath)
            iv_logo_sekolah_input_presensi.visibility = View.GONE
            iv_presensi_foto_guru.visibility = View.VISIBLE
            iv_presensi_foto_guru.setImageBitmap(selectedImage)

            btn_lokasi.visibility = View.VISIBLE
            btn_ambil_foto.visibility = View.GONE
        }

    }

    //ini awal dari detect fake gps

//    override fun onActivityResult(
//            requestCode: Int,
//            resultCode: Int,
//            data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        assistant!!.onActivityResult(requestCode, resultCode)
//    }
//
//    override fun onNeedLocationPermission() {
////        tvLocation.setText("Need\nPermission")
////        tvLocation.setOnClickListener(View.OnClickListener { assistant!!.requestLocationPermission() })
////        assistant!!.requestAndPossiblyExplainLocationPermission()
//    }
//
//    override fun onExplainLocationPermission() {
////        AlertDialog.Builder(this)
////            .setMessage(R.string.permissionExplanation)
////            .setPositiveButton(R.string.ok,
////                DialogInterface.OnClickListener { dialog, which ->
////                    dialog.dismiss()
////                    assistant!!.requestLocationPermission()
////                })
////            .setNegativeButton(R.string.cancel,
////                DialogInterface.OnClickListener { dialog, which ->
////                    dialog.dismiss()
////                    tvLocation.setOnClickListener(View.OnClickListener { assistant!!.requestLocationPermission() })
////                })
////            .show()
//    }
//
//    override fun onLocationPermissionPermanentlyDeclined(
//            fromView: View.OnClickListener?,
//            fromDialog: DialogInterface.OnClickListener?
//    ) {
////        AlertDialog.Builder(this)
////            .setMessage(R.string.permissionPermanentlyDeclined)
////            .setPositiveButton(R.string.ok, fromDialog)
////            .show()
//    }
//
//    override fun onNeedLocationSettingsChange() {
////        AlertDialog.Builder(this)
////            .setMessage(R.string.switchOnLocationShort)
////            .setPositiveButton(R.string.ok,
////                DialogInterface.OnClickListener { dialog, which ->
////                    dialog.dismiss()
////                    assistant!!.changeLocationSettings()
////                })
////            .show()
//    }
//
//    override fun onFallBackToSystemSettings(
//            fromView: View.OnClickListener?,
//            fromDialog: DialogInterface.OnClickListener?
//    ) {
////        AlertDialog.Builder(this)
////            .setMessage(R.string.switchOnLocationLong)
////            .setPositiveButton(R.string.ok, fromDialog)
////            .show()
//    }
//
//    override fun onNewLocationAvailable(location: Location?) {
////        if (location == null) return
////        tvLocation.setOnClickListener(null)
////        tvLocation.setText(
////            """
////                ${location.longitude}
////                ${location.latitude}
////                """.trimIndent()
////        )
////        tvLocation.setAlpha(1.0f)
////        tvLocation.animate().alpha(0.5f).setDuration(400)
//    }
//
//    override fun onMockLocationsDetected(
//            fromView: View.OnClickListener?,
//            fromDialog: DialogInterface.OnClickListener?
//    ) {
////        tvLocation.setText(getString(R.string.mockLocationMessage))
////        tvLocation.setOnClickListener(fromView)
//
//        alert {
//            title = "Konfirmasi"
//            message = "Mohon matikan fitur Mock Location, agar dapat menjalankan fitur ini"
//            okButton {
//                assistant!!.stop()
//                finish()
//            }
//        }.show()
//    }
//
//    override fun onError(type: LocationAssistant.ErrorType?, message: String?) {
////        tvLocation.setText(getString(R.string.error))
//    }

    //ini akhir dari detect fake gps


    fun startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
//                    mRequestingLocationUpdates = true
                        startLocationUpdates()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
//                        openSettings()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
//        assistant?.stop()
        mSettingsClient
                ?.checkLocationSettings(mLocationSettingsRequest)
                ?.addOnSuccessListener(this) {
//                Log.i(MainActivity.TAG, "All location settings are satisfied.")
                    Toast.makeText(
                            applicationContext,
                            "Started location updates!",
                            Toast.LENGTH_SHORT
                    ).show()
                    if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@addOnSuccessListener
                    }
                    mFusedLocationClient!!.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback, Looper.myLooper()
                    )

                    updateUiLokasi()

                }
                ?.addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
//                        Log.i(
//                            MainActivity.TAG,
//                            "Location settings are not satisfied. Attempting to upgrade " +
//                                    "location settings "
//                        )
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(
                                        this,
                                        REQUEST_CHECK_SETTINGS
                                )
                            } catch (sie: IntentSender.SendIntentException) {
//                            Log.i(
//                                MainActivity.TAG,
//                                "PendingIntent unable to execute request."
//                            )
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage =
                                    "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings."
//                        Log.e(MainActivity.TAG, errorMessage)
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
                                    .show()
                        }
                    }
//                updateLocationUI()
                }
    }

    fun stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                ?.removeLocationUpdates(mLocationCallback)
                ?.addOnCompleteListener(this) {
                    Toast.makeText(
                            applicationContext,
                            "Location updates stopped!",
                            Toast.LENGTH_SHORT
                    ).show()
//                toggleButtons()
                }
    }

    fun getDistance(lat1A: Double?, lng1A: Double?, lat2A: Double?, lng2A: Double?): Double {
        var lat1 = lat1A?.let { Math.toRadians(it) }
        var lat2 = lat2A?.let { Math.toRadians(it) }
        var lng1 = lng1A?.let { Math.toRadians(it) }
        var lng2 = lng2A?.let { Math.toRadians(it) }
//        var lng1 = rad(lng1A), lng2 = rad(lng2A);
        var dLng = lng1?.let { lng2?.minus(it) }
        var dLat = lat1?.let { lat2?.minus(it) }
        var R = 6371 * 1000

//
        var a = Math.sin(dLat!!.div(2)) * Math.sin(dLat.div(2)) +
                Math.sin(dLng!!.div(2)) * Math.sin(dLng.div(2)) *
                Math.cos(lat1!!) * Math.cos(lat2!!)
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
        return R * c / 10
    }

    fun inputPresensiHadirPulang() {
        val pdPresensi = ProgressDialog(this)
        pdPresensi.setMessage("Mengirim presensi ...")
        pdPresensi.setCancelable(false)
        pdPresensi.show()

        val sendPresensiHadirPulang = service.sendPresensiHadirPulang(
                "" + urlPresensiSiswa,
                "presensiSiswa",
                "" + Tanggal.getTanggal(),
                "" + Tanggal.getBulan(),
                "" + nama,
                "" + nis,
                ""+androidId+""+nis,
                "" + jenisPresensi,
                ""+lokasiPresensi,
                ""+noWa
        )

        sendPresensiHadirPulang.enqueue(object : Callback<ResponseInputPresensiHadirPulang> {
            override fun onFailure(call: Call<ResponseInputPresensiHadirPulang>, t: Throwable) {
                pdPresensi.dismiss()
                toast("" + t.message)
            }

            override fun onResponse(
                    call: Call<ResponseInputPresensiHadirPulang>,
                    response: Response<ResponseInputPresensiHadirPulang>
            ) {
                pdPresensi.dismiss()
                val status = response.body()?.hasil

                if (status == "succes") {
                    pesanA = "Selamat presensi berhasil dimasukan"
                }else if(status == "failed") {
                    pesanA = "Presensi Gagal dimasukan"
                }else if(status == "denied") {
                    pesanA = "Presensi Gagal, harap menggunakan Hp yang telah di daftarkan"
                }else if(status == "sekolah") {
                    pesanA = "Presensi Gagal, Anda seharusnya presensi di Sekolah"
                }else if(status == "rumah") {
                    pesanA = "Presensi Gagal, Anda seharusnya presensi di Rumah"
                }else{
                    pesanA = "Presensi Gagal, Anda seharusnya presensi di tempat Magang"
                }

                alert {
                    title = "Konfirmasi"
                    message = pesanA
                    okButton {
                        finish()
                    }
                }.show()
            }

        })
    }

    fun updateUiLokasi(){
        if(mCurrentLocation!=null){
            tv_lokasi_instansi.text = "Posisi kordinat lokasi didapatkan"
            tv_lokasi_instansi.setTextColor(resources.getColor(R.color.colorBlack))
            btn_kirim.visibility = View.VISIBLE
            btn_lokasi.visibility = View.GONE
        }else{
            Toast.makeText(this,"Lokasi belum didapatkan, klik tombol Get Lokasi lagi", Toast.LENGTH_SHORT).show();
        }
    }
}