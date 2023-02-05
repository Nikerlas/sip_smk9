@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.gantipass

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_ganti_pass.*
import kotlinx.android.synthetic.main.activity_home_presensi_kehadiran_siswa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GantiPassActivity : AppCompatActivity() {

    lateinit var  service: ServiceClient
    lateinit var url : String
    lateinit var nis : String
    lateinit var nama : String
    lateinit var kelas : String
    lateinit var tingkatan : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_pass)
        service = ServiceNetwork.getService(parent_pass)

        init()

        btn_ganti_pass.setOnClickListener {

            if(!validation()){
                return@setOnClickListener
            }else{
                updatePassword()
            }

        }
    }

    fun init(){
        nis = Siswa.getNIS(parent_pass)
        nama = Siswa.getNamaSiswa(parent_pass)
        kelas = Siswa.getKelasSiswa(parent_pass)
        url = Siswa.getDbServerSiswa(parent_pass)
        tingkatan = Siswa.getTingkatan(parent_pass)
        tv_ganti_pass_nis.text = nis
        tv_ganti_pass_nama.text = nama
        tv_ganti_pass_kelas.text = kelas
    }

    fun validation():Boolean{
        if(et_ganti_pass_pass_lama.text.isEmpty()){
            toast("Password lama tidak boleh kosong")
            return false

        }
        if(et_ganti_pass_pass_baru.text.isEmpty()){
            toast("Password baru tidak boleh kosong")
            return false

        }
        return true
    }

    fun updatePassword(){

        val pd = ProgressDialog(this)


        pd.setMessage("Update pass ... ")
        pd.setCancelable(false)
        pd.show()

        Log.d("ganti"," url : "+url+" nis : "+nis+" tingk : "+tingkatan+" kelas : "+kelas+" lama : "+et_ganti_pass_pass_lama.text.toString()+" baru : "+et_ganti_pass_pass_baru.text.toString())

        val updatePass = service.updatePass(
                ""+url,
                "gantiPassword",
                ""+nis,
                ""+tingkatan,
                ""+kelas,
                ""+et_ganti_pass_pass_lama.text.toString(),
                ""+et_ganti_pass_pass_baru.text.toString()
        )

        updatePass.enqueue(object : Callback<ResponseGantiPass> {
            override fun onFailure(call: Call<ResponseGantiPass>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponseGantiPass>, response: Response<ResponseGantiPass>) {
                pd.dismiss()
                val hasil = response.body()?.hasil
                var pesan = ""
                if(hasil == "sukses"){
                    pesan = "Selamat password Anda berhasil diganti"
                }else{
                    pesan = "Mohon maaf password Anda gagal diganti"
                }

                alert {
                    title = "Konfirmasi"
                    message = pesan
                    okButton {
                        finish()
                    }
                }.show()
            }

        })
        
    }


}