@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Helper
import com.smkn9.semarang.sip_smk9.model.ResponServer
import com.smkn9.semarang.sip_smk9.model.ResponseInfoSoal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_login.*

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    lateinit var etPass: EditText
    lateinit var ivLogoSekolah: ImageView
    lateinit var tvNamaSekolah: TextView
    lateinit var tvNis: TextView
    lateinit var urlSheet: String
    lateinit var nis: String
    lateinit var server: String


    private var okHttpClient: OkHttpClient? = null
    private var gson: Gson? = null
    private var retrofit: Retrofit? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        setContentView(R.layout.activity_login)
        super.onCreate(savedInstanceState)
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tvNis = findViewById(R.id.tv_nis)
        etPass = findViewById(R.id.et_pass)
        ivLogoSekolah = findViewById(R.id.iv_icon_sekolah)



        val sp = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)

        urlSheet = sp.getString(Constant.ID_SS_MASTER_GURU, "").toString()

        nis = sp.getString(Constant.NIS_SISWA, "").toString()

        server = sp.getString(Constant.ID_SERVER_SISWA, "").toString()

        val nama = sp.getString(Constant.NAMA_SISWA, "")
        val mapel = sp.getString(Constant.MAPEL_SISWA, "")




        tvNis.text = nis
        tv_nama.text = nama
        tv_mapel_uji.text = mapel
//        tv_durasi_ujian.text = jam.toString()+" jam, "+menit.toString()+" menit, "+detik.toString()+" detik"
//        tv_jml_soal_uji.text = jmlSoal.toString()


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

        infoSoal()
    }

    fun infoSoal() {
        val pd = ProgressDialog(this)
        pd.setMessage(" Load info soal ... ")
        pd.setCancelable(false)
        pd.show()

        if (!Helper.isNetworkAvailable(this@LoginActivity)) {
            pd.dismiss()
            Toast.makeText(this, "Jaringan tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val service = retrofit!!.create(ServiceClient::class.java)

        val getInfoSoal = service.getInfoSoal(
                "read_info_soal",
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
                        .putInt(Constant.JUMLAH_SOAL, jmlSoal!!)
                        .apply()

                tv_durasi_ujian.text = jam.toString() + " jam, " + menit.toString() + " menit, " + detik.toString() + " detik"
                tv_jml_soal_uji.text = jmlSoal.toString()


            }

        })


    }

    fun login(view: View) {

        if (!validasi()) {
            return
        }

        val pd = ProgressDialog(this)
        pd.setMessage("Cek user ... ")
        pd.setCancelable(false)
        pd.show()


        if (!Helper.isNetworkAvailable(this@LoginActivity)) {
            pd.dismiss()
            Toast.makeText(this, "Jaringan tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }


        val pass = etPass.text.toString()

        val service = retrofit!!.create(ServiceClient::class.java)

        val cekLogin = service.login(

                "ujian",
                "" + urlSheet!!,
                "login",
                "login",
                "" + nis!!,
                "" + pass)

        cekLogin.enqueue(object : Callback<ResponServer> {
            override fun onResponse(call: Call<ResponServer>, response: Response<ResponServer>) {
                pd.dismiss()
                val hasil = response.body()!!.hasil

                if (hasil == "success") {
                    val i = Intent(this@LoginActivity, TokenActivity::class.java)
//                    val i = Intent(this@LoginActivity, MenuActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "" + hasil, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(this@LoginActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun validasi(): Boolean {

        if (etPass.text.toString().isEmpty()) {
            Toast.makeText(this, "Maaf Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}
