@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.raport

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.DetailInfoActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import kotlinx.android.synthetic.main.activity_validasi_nis_raport.*
import org.jetbrains.anko.intentFor

class ValidasiNisRaportActivity : AppCompatActivity() {
    lateinit var sp: SharedPreferences
    lateinit var urlLinkRaport:String
    lateinit var linkRaport:String
    lateinit var nis : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validasi_nis_raport)

        linkRaport = intent.getStringExtra(Constant.BUNDLE_DETAIL).toString()
        nis = Siswa.getNIS(parent_raport)
        tv_nis_cek_pengumuman_raport.text = nis


        btn_validasi_pengumuman_raport.setOnClickListener {

            startActivity(intentFor<DetailInfoActivity>(
                            Constant.BUNDLE_DETAIL to linkRaport
                    ))

        }
    }

//    fun cekPengumuman(nis:String) {
//        val pd = ProgressDialog(this)
//        pd.setMessage("Cek data ...")
//        pd.setCancelable(false)
//        pd.show()
//
//        val service = ServiceNetwork.getService(parent_raport)
//        urlLinkRaport = Siswa.getLinkRaport(parent_raport)
//        val getRaport = service.getPengumumanRaport(
//                ""+urlLinkRaport,
//                "readRaport",
//                ""+Siswa.getTingkatan(parent_raport),
//                ""+nis
//        )
//
//        getRaport.enqueue(object : Callback<ResponsePengumumanRaport> {
//            override fun onFailure(call: Call<ResponsePengumumanRaport>, t: Throwable) {
//                pd.dismiss()
//                toast(""+t.message)
//            }
//
//            override fun onResponse(call: Call<ResponsePengumumanRaport>, response: Response<ResponsePengumumanRaport>) {
//                pd.dismiss()
//                val hasil = response.body()?.linkRaport
//
//                if(hasil!= "gagal"){
//                    startActivity(intentFor<DetailInfoActivity>(
//                            Constant.BUNDLE_DETAIL to hasil
//                    ))
//
////                    et_nis_cek_pengumuman_kelulusan.setText("")
//
//
//                }else{
//                    alert {
//                        title = "konfirmasi"
//                        message = "Maaf NIS tidak ditemukan"
//                        okButton {  }
//                    }.show()
//                }
//            }
//
//        })
//    }
}