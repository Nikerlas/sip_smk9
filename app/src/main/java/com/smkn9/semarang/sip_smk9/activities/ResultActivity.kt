@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities

import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.google.gson.Gson
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.essay.InfoSoalEssayActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.ResponServer
import com.smkn9.semarang.sip_smk9.network.ServiceClient

import okhttp3.OkHttpClient
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ResultActivity : AppCompatActivity() {
    lateinit var tvResult: TextView
    lateinit var pd: ProgressDialog
    lateinit var nis: String
    lateinit var urlSheet: String
    lateinit var server: String
    lateinit var service: ServiceClient
    lateinit var okHttpClient: OkHttpClient
    lateinit var gson: Gson
    lateinit var retrofit: Retrofit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
//        tvResult = findViewById<View>(R.id.tv_result_nilai) as TextView

//        pd = ProgressDialog(this)
//        pd.setMessage("load result from server")
//        pd.show()
//
//
//        urlSheet = getSharedPreferences(com.smkn9.semarang.simsiswa.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
//                .getString(com.smkn9.semarang.simsiswa.helper.Constant.ID_SS_MASTER_GURU, "").toString()
//        server = getSharedPreferences(com.smkn9.semarang.simsiswa.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
//                .getString(com.smkn9.semarang.simsiswa.helper.Constant.ID_SERVER_SISWA, "").toString()
//
//        nis = getSharedPreferences(com.smkn9.semarang.simsiswa.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
//                .getString(com.smkn9.semarang.simsiswa.helper.Constant.NIS_SISWA, "").toString()
//
//        okHttpClient = OkHttpClient().newBuilder()
//                .connectTimeout(120, TimeUnit.SECONDS)
//                .readTimeout(120, TimeUnit.SECONDS)
//                .writeTimeout(120, TimeUnit.SECONDS)
//                .build()
//
//        gson = GsonBuilder().setLenient().create()
//
//        retrofit = Retrofit.Builder()
//                .baseUrl(server)
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//
//        service = retrofit.create(ServiceClient::class.java)
//
//        val getResult = service.getResultExam("" + urlSheet, "read_result", "hasil", nis)
//
//
//        getResult.enqueue(object : Callback<ResponServer> {
//            override fun onResponse(call: Call<ResponServer>, response: Response<ResponServer>) {
//                pd.dismiss()
//                val hasil = response.body()!!.hasil
//                tvResult.text = hasil
//
//                //                Call<ResponServer> sendNotif = service.sendNotif("ujian",""+urlSheet,"notifikasi","sendNotif",nis,hasil);
//                //
//                //                sendNotif.enqueue(new Callback<ResponServer>() {
//                //                    @Override
//                //                    public void onResponse(Call<ResponServer> call1, Response<ResponServer> response1) {
//                //                        Toast.makeText(ResultActivity.this, "sukses", Toast.LENGTH_SHORT).show();
//                //                    }
//                //
//                //                    @Override
//                //                    public void onFailure(Call<ResponServer> call1, Throwable t1) {
//                //                        Toast.makeText(ResultActivity.this, ""+t1.getMessage(), Toast.LENGTH_SHORT).show();
//                //                    }
//                //                });
//            }
//
//            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
//                pd.dismiss()
//                Toast.makeText(this@ResultActivity, "" + t.message, Toast.LENGTH_SHORT).show()
//            }
//        })

    }

    fun selesai(view: View) {

        getSharedPreferences("jawaban", Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("listJawabanHeader", Context.MODE_PRIVATE).edit().clear().apply()
//        getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
//        getSharedPreferences(Constant.NAMA_MAPEL_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.LIST_JAWABAN_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
//        startActivity(Intent(this@ResultActivity, KodeGuruActivity::class.java))
        val dataSiswa = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
        val urlSheet = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_GURU, "")
        val mapel = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.MAPEL_SISWA, "")
        val noHp = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.NO_HP_ORTU_SISWA, "")
        val nama = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.NAMA_SISWA, "")
        val sms = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.STATUS_SMS, "")
        val statusUjian = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.JENIS_UJIAN, "")


        if (sms == "on") {
            val pdFinal = ProgressDialog(this)
            pdFinal.setMessage("Clear cache ... ")
            pdFinal.show()
            val sendSms = service.sendSMS(
                    "" + urlSheet,
                    "send_sms",
                    "hasil",
                    "" + tvResult.text,
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
                        startActivity(Intent(this@ResultActivity, KodeGuruActivity::class.java))
                        finish()
                    }
                }

            })
        } else {
            getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
            clearAppData()
            startActivity(Intent(this@ResultActivity, KodeGuruActivity::class.java))
            finish()
        }


    }

    override fun onBackPressed() {

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

    fun lanjutEssay(view: View) {
        startActivity(intentFor<InfoSoalEssayActivity>())
        finish()
    }
}
