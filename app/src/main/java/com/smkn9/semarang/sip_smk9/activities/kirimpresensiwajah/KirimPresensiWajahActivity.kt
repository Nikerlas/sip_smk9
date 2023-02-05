package com.smkn9.semarang.sip_smk9.activities.kirimpresensiwajah

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.presensi.ResponseInputPresensiHadirPulang
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.Tanggal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_kirim_presensi_wajah.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KirimPresensiWajahActivity : AppCompatActivity() {
    lateinit var service: ServiceClient
    lateinit var sp: SharedPreferences
    lateinit var jenisPresensi: String
    lateinit var urlPresensiSiswa: String
    lateinit var nis: String
    lateinit var pesanA: String
    lateinit var nama: String
    lateinit var androidId:String
    lateinit var lokasiPresensi:String
    lateinit var noWa:String
    lateinit var llPresensiWajah:LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kirim_presensi_wajah)
        llPresensiWajah = findViewById(R.id.ll_mengirim_presensi_wajah)
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        urlPresensiSiswa = Siswa.getLinkJurnalKelas(parent_kirim_presensi_wajah)
        nama = Siswa.getNamaSiswa(parent_kirim_presensi_wajah)
        nis = Siswa.getNIS(parent_kirim_presensi_wajah)
        jenisPresensi = intent.getStringExtra(Constant.BUNDLE_DETAIL).toString()
        lokasiPresensi = intent.getStringExtra(Constant.SISWA_LOKASI_PRESENSI).toString()
        noWa = intent.getStringExtra("wa").toString()
        service = ServiceNetwork.getService(parent_kirim_presensi_wajah)

        inputPresensiHadirPulang()
    }


    fun inputPresensiHadirPulang() {
//        val pdPresensi = ProgressDialog(this)
//        pdPresensi.setMessage("Mengirim presensi ...")
//        pdPresensi.setCancelable(false)
//        pdPresensi.show()
//        pbLoading.visibility = View.VISIBLE

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
//                pdPresensi.dismiss()
                llPresensiWajah.visibility = View.GONE
                toast("" + t.message)
            }

            override fun onResponse(
                call: Call<ResponseInputPresensiHadirPulang>,
                response: Response<ResponseInputPresensiHadirPulang>
            ) {
                llPresensiWajah.visibility = View.GONE
//                pdPresensi.dismiss()
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
}