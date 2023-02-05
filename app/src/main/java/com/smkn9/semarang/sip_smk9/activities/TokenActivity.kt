@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.chaos.view.PinView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.ResponServerToken
import com.smkn9.semarang.sip_smk9.network.ServiceClient

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenActivity : AppCompatActivity() {
    lateinit var pvValidasi: PinView
    lateinit var nis: String
    lateinit var server: String
    lateinit var urlSheet: String

    private var okHttpClient: OkHttpClient? = null
    private var gson: Gson? = null
    private var retrofit: Retrofit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        pvValidasi = findViewById(R.id.pv_validasi)

        urlSheet = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.ID_SS_MASTER_GURU, "").toString()
        server = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.ID_SERVER_SISWA, "").toString()
        nis = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.NIS_SISWA, "").toString()

        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

        gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
                .baseUrl(server!!)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create(gson!!))
                .build()


        val dataSiswa = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
        val sms = dataSiswa.getString(com.smkn9.semarang.sip_smk9.helper.Constant.STATUS_SMS, "")

//        Log.d("infoSms", "onCreate: "+sms);
    }

    fun cekToken(view: View) {
        if (!valid()) {
            return
        }

        val pd = ProgressDialog(this)
        pd.setMessage("Cek Token")
        pd.setCancelable(false)
        pd.show()


        val token = pvValidasi.text.toString()
        val service = retrofit!!.create(ServiceClient::class.java)

        val sendToken = service.token(
                "ujian",
                "" + urlSheet!!,
                "token",
                "login",
                nis,
                token)

        sendToken.enqueue(object : Callback<ResponServerToken> {
            override fun onResponse(call: Call<ResponServerToken>, response: Response<ResponServerToken>) {
                pd.dismiss()
                val hasil = response.body()!!.hasil
//                val namaMapel = response.body()!!.namaMapel


                val jam = response.body()!!.jam
                val menit = response.body()!!.menit
                val detik = response.body()!!.detik
                val jumlahSoal = response.body()!!.jumlahSoal

//                getSharedPreferences(Constant.NAMA_MAPEL_HEADER, Context.MODE_PRIVATE)
//                        .edit()
//                        .putString(Constant.NAMA_MAPEL, namaMapel)
//                        .apply()

                getSharedPreferences(Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(Constant.JAM_UJIAN, jam)
                        .putInt(Constant.MENIT_UJIAN, menit)
                        .putInt(Constant.DETIK_UJIAN, detik)
                        .putInt(Constant.JUMLAH_SOAL, jumlahSoal)
                        .apply()





                when (hasil) {
                    "success" -> {
                        //                        Bundle c = new Bundle();
                        //                        c.putString("nis", nis);
                        val i = Intent(this@TokenActivity,MenuActivity::class.java)
                        //                        i.putExtras(c);
                        startActivity(i)
                        getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply()
                        finish()
                    }
                    "used" -> Toast.makeText(this@TokenActivity, "Token sudah pernah digunakan", Toast.LENGTH_SHORT).show()
                    "failed" -> Toast.makeText(this@TokenActivity, "Token salah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServerToken>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(this@TokenActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun valid(): Boolean {
        if (pvValidasi.text.toString().isEmpty()) {
            Toast.makeText(this, "Token tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onBackPressed() {
        //        super.onBackPressed();
    }
}
