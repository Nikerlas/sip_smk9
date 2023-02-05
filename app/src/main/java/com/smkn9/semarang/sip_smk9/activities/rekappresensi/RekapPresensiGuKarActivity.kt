@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.rekappresensi

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_kirim_presensi_wajah.*
import kotlinx.android.synthetic.main.activity_rekap_presensi_gu_kar.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RekapPresensiGuKarActivity : AppCompatActivity() {
    lateinit var sp: SharedPreferences
    lateinit var  service: ServiceClient
    lateinit var urlPresensiSiswa:String
    lateinit var pd:ProgressDialog
    lateinit var bulanPresensi:String
    lateinit var tahunAjaran:String
    lateinit var nis:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekap_presensi_gu_kar)

        sp = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)
        bulanPresensi = intent.getStringExtra(Constant.SISWA_BULAN_PRESENSI).toString()
//        tahunAjaran = sp.getString(Constant.GURU_TAHUN_AJARAN,"").toString()
        val actionHeader = supportActionBar
        actionHeader?.title = "Presensi Bulan $bulanPresensi"
        urlPresensiSiswa = Siswa.getLinkJurnalKelas(parent_rekap_gukar)
        nis = Siswa.getNIS(parent_rekap_gukar)

//        urlPresensiGuru = sp.getString(Constant.SISWA_LINK_JURNAL_KELAS,"").toString()
//        kodeGuru = sp.getString(Constant.SISWA_NIS,"").toString()
        service = ServiceNetwork.getService(parent_rekap_gukar)
        loadRekapGukar()
    }


    fun loadRekapGukar(){
//        pd = ProgressDialog(this)
//        pd.setMessage("Load rekap presensi ...")
//        pd.setCancelable(false)
//        pd.show()
        pb_rekap_presensi_gukar.visibility = View.VISIBLE

        val getrekapPresensi = service.getRekapPresensiGuru(
            ""+urlPresensiSiswa,
            "rekapPresensi",
            ""+bulanPresensi,
            ""+nis
        )

//        Log.d("rekap", "loadRekapGukar: "+"url "+urlPresensiSiswa+" bulan "+bulanPresensi+" nis "+nis)

        getrekapPresensi.enqueue(object : Callback<ResponseRekapPresensiGukar> {
            override fun onFailure(call: Call<ResponseRekapPresensiGukar>, t: Throwable) {
//                pd.dismiss()
                pb_rekap_presensi_gukar.visibility = View.GONE
                toast(""+t.message)
//                Log.d("rekap", "loadRekapGukar: "+t.message+" kode "+t.hashCode())
            }

            override fun onResponse(
                call: Call<ResponseRekapPresensiGukar>,
                response: Response<ResponseRekapPresensiGukar>
            ) {
//                pd.dismiss()
                pb_rekap_presensi_gukar.visibility = View.GONE
                val data = response.body()?.rekap
                val pesan = data?.split(",")

                if (pesan != null) {
                    setPresensi(pesan)
                }

            }

        })

    }

    fun setPresensi(pesan : List<String>){

        tv_hadir1.text = pesan[0]
        tv_pulang1.text = pesan[1]
        tv_hadir2.text = pesan[3]
        tv_pulang2.text = pesan[4]
        tv_hadir3.text = pesan[6]
        tv_pulang3.text = pesan[7]
        tv_hadir4.text = pesan[9]
        tv_pulang4.text = pesan[10]
        tv_hadir5.text = pesan[12]
        tv_pulang5.text = pesan[13]
        tv_hadir6.text = pesan[15]
        tv_pulang6.text = pesan[16]
        tv_hadir7.text = pesan[18]
        tv_pulang7.text = pesan[19]
        tv_hadir8.text = pesan[21]
        tv_pulang8.text = pesan[22]
        tv_hadir9.text = pesan[24]
        tv_pulang9.text = pesan[25]
        tv_hadir10.text = pesan[27]
        tv_pulang10.text = pesan[28]

        tv_hadir11.text = pesan[30]
        tv_pulang11.text = pesan[31]
        tv_hadir12.text = pesan[33]
        tv_pulang12.text = pesan[34]
        tv_hadir13.text = pesan[36]
        tv_pulang13.text = pesan[37]
        tv_hadir14.text = pesan[39]
        tv_pulang14.text = pesan[40]
        tv_hadir15.text = pesan[42]
        tv_pulang15.text = pesan[43]
        tv_hadir16.text = pesan[45]
        tv_pulang16.text = pesan[46]
        tv_hadir17.text = pesan[48]
        tv_pulang17.text = pesan[49]
        tv_hadir18.text = pesan[51]
        tv_pulang18.text = pesan[52]
        tv_hadir19.text = pesan[54]
        tv_pulang19.text = pesan[55]
        tv_hadir20.text = pesan[57]
        tv_pulang20.text = pesan[58]

        tv_hadir21.text = pesan[60]
        tv_pulang21.text = pesan[61]
        tv_hadir22.text = pesan[63]
        tv_pulang22.text = pesan[64]
        tv_hadir23.text = pesan[66]
        tv_pulang23.text = pesan[67]
        tv_hadir24.text = pesan[69]
        tv_pulang24.text = pesan[70]
        tv_hadir25.text = pesan[72]
        tv_pulang25.text = pesan[73]
        tv_hadir26.text = pesan[75]
        tv_pulang26.text = pesan[76]
        tv_hadir27.text = pesan[78]
        tv_pulang27.text = pesan[79]
        tv_hadir28.text = pesan[81]
        tv_pulang28.text = pesan[82]
        tv_hadir29.text = pesan[84]
        tv_pulang29.text = pesan[85]
        tv_hadir30.text = pesan[87]
        tv_pulang30.text = pesan[88]

        tv_hadir31.text = pesan[90]
        tv_pulang31.text = pesan[91]

    }
}