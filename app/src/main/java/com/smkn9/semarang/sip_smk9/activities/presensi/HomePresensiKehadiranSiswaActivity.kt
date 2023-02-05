@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.presensi

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.Tanggal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_home_presensi_kehadiran_siswa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePresensiKehadiranSiswaActivity : AppCompatActivity() {
    lateinit var androidId:String
    lateinit var nis:String
    lateinit var pd:ProgressDialog
    lateinit var latAcuan :String
    lateinit var lngAcuan :String
    lateinit var jarak :String
    lateinit var noWa:String
    lateinit var  service: ServiceClient
    lateinit var lokasiPresensi :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_presensi_kehadiran_siswa)
        service = ServiceNetwork.getService(parent_home_presensi_hadir_pulang)
        lokasiPresensi = intent.getStringExtra(Constant.SISWA_LOKASI_PRESENSI).toString()

        loadLokasiAcuan()


        androidId = Siswa.getAndroidId(parent_home_presensi_hadir_pulang)
        nis = Siswa.getNIS(parent_home_presensi_hadir_pulang)
        iv_presensi_siswa_hadir.setOnClickListener {
            cekStatusPresensi("hadir")
        }

        iv_presensi_siswa_pulang.setOnClickListener {
            cekStatusPresensi("pulang")
        }


    }

    private fun loadLokasiAcuan(){
        pd = ProgressDialog(this)
        pd.setMessage("Load lokasi acuan ...")
        pd.setCancelable(false)
        pd.show()

        val getLokasiAcuan = service.getLokasiAcuan(
                ""+Siswa.getLinkJurnalKelas(parent_home_presensi_hadir_pulang),
                "readLokasiAcuan",
                ""+Siswa.getNIS(parent_home_presensi_hadir_pulang),
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
                    jarak = data.jarakMax.toString()
                    noWa = data.noWa.toString()
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

    private fun cekStatusPresensi(jenisPresensi:String){
        pd = ProgressDialog(this)
        pd.setMessage("Cek status presensi ...")
        pd.setCancelable(false)
        pd.show()

        val getStatusPresensi = service.getStatusPresensiSiswa(
                ""+Siswa.getLinkJurnalKelas(parent_home_presensi_hadir_pulang),
                "readStatusPresensi",
                ""+ Tanggal.getTanggal(),
                ""+Tanggal.getBulan(),
                ""+nis,
                ""+jenisPresensi
        )

        getStatusPresensi.enqueue(object : Callback<ResponseReadStatusPresensi> {
            override fun onFailure(call: Call<ResponseReadStatusPresensi>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(
                    call: Call<ResponseReadStatusPresensi>,
                    response: Response<ResponseReadStatusPresensi>
            ) {
                pd.dismiss()
                val status = response.body()?.hasil

                if(status == "belum"){
                    startActivity(intentFor<InputPresensiKehadiranSiswaActivity>(
                            Constant.BUNDLE_DETAIL to jenisPresensi,
                            "lat" to latAcuan,
                            "lng" to lngAcuan,
                            "jarak" to jarak.toInt(),
                            Constant.SISWA_LOKASI_PRESENSI to lokasiPresensi,
                            "wa" to noWa
                    ))
                    finish()
                }else{
                    alert {
                        title = "Konfirmasi"
                        message = "Mohon maaf Anda telah melakukan presensi "+jenisPresensi+" pada hari ini"
                        okButton {

                        }
                    }.show()
                }
            }

        })


    }
}