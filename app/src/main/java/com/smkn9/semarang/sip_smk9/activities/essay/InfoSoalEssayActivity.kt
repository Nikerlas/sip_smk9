@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.essay

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Helper
import com.smkn9.semarang.sip_smk9.model.ResponseInfoSoal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_info_soal_essay.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class InfoSoalEssayActivity : AppCompatActivity() {

    lateinit var tvNis: TextView
    lateinit var urlSheet: String
    lateinit var nis: String
    lateinit var server: String


    private var okHttpClient: OkHttpClient? = null
    private var gson: Gson? = null
    private var retrofit: Retrofit? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_soal_essay)
        tvNis = findViewById(R.id.tv_nis_info_essay)



        val sp = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)

        urlSheet = sp.getString(Constant.ID_SS_MASTER_GURU, "").toString()

        nis = sp.getString(Constant.NIS_SISWA, "").toString()

        server = sp.getString(Constant.ID_SERVER_SISWA, "").toString()

        val nama = sp.getString(Constant.NAMA_SISWA, "")
        val mapel = sp.getString(Constant.MAPEL_SISWA, "")




        tvNis.text = nis
        tv_nama_info_essay.text = nama
        tv_mapel_uji_essay.text = mapel


        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

        gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
                .baseUrl(server)
                .client(okHttpClient!!)
                .addConverterFactory(GsonConverterFactory.create(gson!!))
                .build()

        infoSoalEssay()
    }

    fun infoSoalEssay() {
        val pd = ProgressDialog(this)
        pd.setMessage(" Load info soal Essay... ")
        pd.setCancelable(false)
        pd.show()

        if (!Helper.isNetworkAvailable(this@InfoSoalEssayActivity)) {
            pd.dismiss()
            Toast.makeText(this, "Jaringan tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val service = retrofit!!.create(ServiceClient::class.java)

        val getInfoSoal = service.getInfoSoal(
                "read_info_soal_essay",
                "" + urlSheet,
                "durasi_pengerjaan"
        )

        getInfoSoal.enqueue(object : Callback<ResponseInfoSoal> {
            override fun onFailure(call: Call<ResponseInfoSoal>, t: Throwable) {
                pd.dismiss()
                toast("" + t.message)
            }

            override fun onResponse(call: Call<ResponseInfoSoal>, response: Response<ResponseInfoSoal>) {
                pd.dismiss()
                val jam = response.body()?.jam
                val menit = response.body()?.menit
                val detik = response.body()?.detik
                val jmlSoal = response.body()?.jumlahSoal


                getSharedPreferences(Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(Constant.JAM_UJIAN, jam!!)
                        .putInt(Constant.MENIT_UJIAN, menit!!)
                        .putInt(Constant.DETIK_UJIAN, detik!!)
                        .putInt(Constant.JUMLAH_SOAL_ESSAY, jmlSoal!!)
                        .apply()

                tv_durasi_ujian_essay.text = jam.toString() + " jam, " + menit.toString() + " menit, " + detik.toString() + " detik"
                tv_jml_soal_uji_essay.text = jmlSoal.toString()
            }

        })


    }

    fun kerjakanEssay(view: View) {
        startActivity(intentFor<MenuEssayActivity>())
        finish()

    }
}