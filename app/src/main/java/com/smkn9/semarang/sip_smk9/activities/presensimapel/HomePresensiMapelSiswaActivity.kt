@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.presensimapel

import android.Manifest
import android.app.ProgressDialog
import android.content.IntentSender
import android.content.pm.PackageManager
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.presensi.ResponseReadLokasiAcuan
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.Tanggal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_home_presensi_mapel_siswa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.util.*

class HomePresensiMapelSiswaActivity : AppCompatActivity() {

    lateinit var service: ServiceClient
    lateinit var urlJurnalKelas :String
    lateinit var nis:String
    lateinit var androidId:String
    lateinit var pd:ProgressDialog
    var locationLatitude: Double? = null
    var locationLongitude: Double? = null
    lateinit var latAcuan :String
    lateinit var lngAcuan :String
    var jarak :Int = 0

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

    private lateinit var lokasiPresensi:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_presensi_mapel_siswa)

        service = ServiceNetwork.getService(parent_home_presensi_mapel_siswa)
        lokasiPresensi = intent.getStringExtra(Constant.SISWA_LOKASI_PRESENSI).toString()
        initUi()

        loadLokasiAcuan()


        urlJurnalKelas = Siswa.getLinkJurnalKelas(parent_home_presensi_mapel_siswa)
        nis = Siswa.getNIS(parent_home_presensi_mapel_siswa)
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

        btn_input_materi_mapel_lokasi.setOnClickListener {
            startLocationButtonClick()
        }

        btn_input_materi_mapel.setOnClickListener {
            if (!validation()){
                return@setOnClickListener
            }

            if(mCurrentLocation!=null){
                locationLatitude = mCurrentLocation?.latitude
                locationLongitude = mCurrentLocation?.longitude
            }else{
                toast("Lokasi belum didapatkan tunggu beberapa detik lagi")
                return@setOnClickListener
            }

            val jarakHitung =
                    getDistance(latAcuan.toDouble(), lngAcuan.toDouble(), locationLatitude, locationLongitude)

            if (jarakHitung > jarak) {
                toast("Maaf Anda berada di luar lokasi Presensi")
                return@setOnClickListener
            }


            inputPresensiMapel()

        }



    }

    override fun onPause() {
        // pausing location updates
        stopLocationUpdates()
        super.onPause()
    }

    fun initUi(){
        tv_input_materi_nis.text = Siswa.getNIS(parent_home_presensi_mapel_siswa)
        tv_input_materi_nama.text = Siswa.getNamaSiswa(parent_home_presensi_mapel_siswa)
        tv_input_materi_tanggal.text = Tanggal.getTanggal().toString()+" "+Tanggal.getBulan()

    }

    private fun loadLokasiAcuan(){
        pd = ProgressDialog(this)
        pd.setMessage("Load lokasi acuan ...")
        pd.setCancelable(false)
        pd.show()

        val getLokasiAcuan = service.getLokasiAcuan(
                ""+Siswa.getLinkJurnalKelas(parent_home_presensi_mapel_siswa),
                "readLokasiAcuan",
                ""+Siswa.getNIS(parent_home_presensi_mapel_siswa),
                ""+lokasiPresensi
        )

        getLokasiAcuan.enqueue(object : Callback<ResponseReadLokasiAcuan> {
            override fun onFailure(call: Call<ResponseReadLokasiAcuan>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(
                    call: Call<ResponseReadLokasiAcuan>,
                    response: Response<ResponseReadLokasiAcuan>
            ) {
                pd.dismiss()
                val data = response.body()
                val lokasi = data?.lokasi
                if (lokasi == "success"){
                    latAcuan = data.latAcuan.toString()
                    lngAcuan = data.longAcuan.toString()
                    jarak = data.jarakMax?.toInt()!!
                    init()
                }else{
                    alert {
                        title = "Konfirmasi"
                        message = "Mohon maaf Admin Anda belum memasukan lokasi Acuan"
                        okButton {
                            finish()
                        }
                    }.show()

                }

            }

        })

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

    private fun startLocationUpdates() {
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

    fun updateUiLokasi(){
        if(mCurrentLocation!=null){
            tv_input_materi_lokasi.text = "Kordinat lokasi didapatkan"
            tv_input_materi_lokasi.setTextColor(resources.getColor(R.color.colorBlack))
            btn_input_materi_mapel.visibility = View.VISIBLE
            btn_input_materi_mapel_lokasi.visibility = View.GONE
        }else{
            Toast.makeText(this,"Lokasi belum didapatkan, klik tombol Get Lokasi lagi", Toast.LENGTH_SHORT).show();
        }
    }

    fun validation():Boolean{
        if(et_input_materi_mapel.text.toString()==""){
            toast("Maaf nama mapel tidak boleh dikosongi ...")
            return false
        }

        if(et_input_materi_presensi_mandiri_kegiatan.text.toString()==""){
            toast("Maaf uraian kegiatan tidak boleh dikosongi ...")
            return false
        }
        return true
    }

    fun inputPresensiMapel() {
        val pdPresensi = ProgressDialog(this)
        pdPresensi.setMessage("Mengirim presensi ...")
        pdPresensi.setCancelable(false)
        pdPresensi.show()

        val durasi = spJamAkhir.selectedItem.toString().toInt()-spJamAwal.selectedItem.toString().toInt()+1
        val sendPresensiMateri = service.sendPresensiMapel(
                "" + urlJurnalKelas,
                "presensiMapel",
                "" + Tanggal.getTanggal(),
                "" + Tanggal.getBulan(),
                "" +Siswa.getNamaSiswa(parent_home_presensi_mapel_siswa),
                ""+nis,
                ""+androidId+""+nis,
                "" +spJamAwal.selectedItem.toString(),
                ""+durasi,
                ""+et_input_materi_mapel.text.toString(),
                ""+et_input_materi_presensi_mandiri_kegiatan.text.toString()
        )

        sendPresensiMateri.enqueue(object : Callback<ResponseInputPresensiMapel> {
            override fun onFailure(call: Call<ResponseInputPresensiMapel>, t: Throwable) {
                pdPresensi.dismiss()
                toast("" + t.message)
            }

            override fun onResponse(
                    call: Call<ResponseInputPresensiMapel>,
                    response: Response<ResponseInputPresensiMapel>
            ) {
                pdPresensi.dismiss()
                val status = response.body()?.hasil
                var pesanA =""
                if (status == "succes") {
                    pesanA = "Selamat presensi berhasil dimasukan"
                }else if(status == "failed") {
                    pesanA = "Presensi Gagal dimasukan"
                }else{
                    pesanA = "Presensi Gagal, harap menggunakan Hp yang telah di daftarkan"
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




}