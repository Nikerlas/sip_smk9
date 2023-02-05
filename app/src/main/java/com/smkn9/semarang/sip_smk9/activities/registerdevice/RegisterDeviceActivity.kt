@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.registerdevice

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.home.ResponseRegisterDevice
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_register_device.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterDeviceActivity : AppCompatActivity() {

    lateinit var sp: SharedPreferences
    lateinit var urlPresensi:String
    lateinit var nis:String
    lateinit var androidId:String
    lateinit var pd: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)

        sp = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)
        urlPresensi = sp.getString(Constant.SISWA_LINK_JURNAL_KELAS,"")!!
        nis = sp.getString(Constant.NIS_SISWA,"")!!
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        insertUi()

        btn_register_device.setOnClickListener {
            registerDevice()
        }
    }

    fun insertUi(){
        tv_nis_register.text = nis
        tv_android_id_register.text = androidId
    }

    fun registerDevice(){
        pd = ProgressDialog(this)
        pd.setMessage("registrasi device ...")
        pd.setCancelable(false)
        pd.show()

        val service = ServiceNetwork.getService(parent_register)
        val sendRegister = service.registerDevice(
                ""+urlPresensi,
                "registerDevice",
                ""+nis,
                ""+androidId+""+nis
        )


        sendRegister.enqueue(object : Callback<ResponseRegisterDevice> {
            override fun onFailure(call: Call<ResponseRegisterDevice>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(
                    call: Call<ResponseRegisterDevice>,
                    response: Response<ResponseRegisterDevice>
            ) {
                pd.dismiss()
                val status = response.body()?.status
                if(status == "sukses"){
                    alert {
                        title = "Konfirmasi"
                        message = "Selamat Device dan NIS Anda telah terdaftar"
                        okButton {
                            finish()
                        }
                    }.show()
                }else{
                    toast("Device dan NIS Anda gagal terdaftar")
                }
            }

        })
    }
}