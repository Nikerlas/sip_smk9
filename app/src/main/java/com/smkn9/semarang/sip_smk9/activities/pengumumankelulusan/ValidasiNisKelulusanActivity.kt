@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.pengumumankelulusan

import android.app.ProgressDialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.DetailInfoActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_validasi_nis_kelulusan.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidasiNisKelulusanActivity : AppCompatActivity() {
    lateinit var sp: SharedPreferences
    lateinit var urlLinkKelulusan:String
    lateinit var nis:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validasi_nis_kelulusan)

        nis = Siswa.getNIS(parent_kelulusan)
        tv_nis_cek_pengumuman_kelulusan.text = nis

        btn_validasi_pengumuman.setOnClickListener {

            cekPengumuman(nis)
        }
    }

    fun cekPengumuman(nis:String) {
        val pd = ProgressDialog(this)
        pd.setMessage("Cek data ...")
        pd.setCancelable(false)
        pd.show()

        val service = ServiceNetwork.getService(parent_kelulusan)
        urlLinkKelulusan = Siswa.getLinkKelulusan(parent_kelulusan)
        val getKelulusan = service.getInfoKelulusan(
                ""+urlLinkKelulusan,
                "readKelulusan",
                ""+nis
        )

        getKelulusan.enqueue(object : Callback<ResponseKelulusan> {
            override fun onFailure(call: Call<ResponseKelulusan>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponseKelulusan>, response: Response<ResponseKelulusan>) {
                pd.dismiss()
                val hasil = response.body()?.hasil?.status

                if(hasil!= "gagal"){
                    val administrasi = response.body()?.hasil?.administrasi

                    if(administrasi == "lunas"){
                        val link = response.body()?.hasil?.linkKelulusan
                        startActivity(intentFor<DetailInfoActivity>(
                                Constant.BUNDLE_DETAIL to link
                        ))
                    }else{
                        val pesan = response.body()?.hasil?.pesan
                        alert {
                            title = "konfirmasi"
                            message = ""+pesan
                            okButton {
                                finish()
                            }
                        }.show()

                    }


                }else{
                    alert {
                        title = "konfirmasi"
                        message = "Maaf NIS tidak ditemukan"
                        okButton {
                            finish()
                        }
                    }.show()
                }
            }

        })
    }
}