@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.essay

import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.KodeGuruActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.ResponServer
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class EssayResultActivity : AppCompatActivity() {
    lateinit var nis: String
    lateinit var urlSheet: String
    lateinit var server: String
    lateinit var service: ServiceClient
    lateinit var okHttpClient: OkHttpClient
    lateinit var gson: Gson
    lateinit var retrofit: Retrofit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essay_result)

//        urlSheet = getSharedPreferences(com.smkn9.semarang.simsiswa.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
//                .getString(com.smkn9.semarang.simsiswa.helper.Constant.ID_SS_MASTER_GURU, "").toString()
        server = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SERVER_SISWA, "").toString()

        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

        gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
                .baseUrl(server)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        service = retrofit.create(ServiceClient::class.java)
    }

    override fun onBackPressed() {

    }

    fun selesaiUjian(view: View) {
        selesai()
//        clearAppData()
    }


    fun selesai() {


        val dataSiswa = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
        val urlSheet = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_GURU, "")
        val mapel = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.MAPEL_SISWA, "")
        val noHp = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.NO_HP_ORTU_SISWA, "")
        val nama = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.NAMA_SISWA, "")
        val sms = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.STATUS_SMS, "")
        val statusUjian = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.JENIS_UJIAN, "")


        if (sms == "on") {
            val pdFinal = ProgressDialog(this)
            pdFinal.setMessage("Membersihkan log ujian ... ")
            pdFinal.show()
            val sendSms = service.sendSMS(
                    "" + urlSheet,
                    "send_sms",
                    "hasil",
                    "0",
                    "" + mapel,
                    "" + noHp,
                    "" + nama,
                    ""+statusUjian)

            sendSms.enqueue(object : Callback<ResponServer> {
                override fun onFailure(call: Call<ResponServer>, t: Throwable) {
                    pdFinal.dismiss()
                    toast("" + t.message)
                }

                override fun onResponse(call: Call<ResponServer>, response: Response<ResponServer>) {
                    pdFinal.dismiss()
                    val hasil = response.body()?.hasil

                    if (hasil == "success") {
                        getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
                        clearAppData()
                        startActivity(Intent(this@EssayResultActivity, KodeGuruActivity::class.java))
                        finish()
                    }
                }

            })
        } else {
            getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
            clearAppData()
            startActivity(Intent(this@EssayResultActivity, KodeGuruActivity::class.java))
            finish()
        }


    }


    fun clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData() // note: it has a return value!
            } else {
                val packageName = applicationContext.packageName
                val runtime = Runtime.getRuntime()
                runtime.exec("pm clear $packageName")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}