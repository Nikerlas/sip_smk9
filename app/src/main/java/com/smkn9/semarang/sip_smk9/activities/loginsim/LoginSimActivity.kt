@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.loginsim

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.home.HomeActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_login.btn_login
import kotlinx.android.synthetic.main.activity_login_sim.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginSimActivity : AppCompatActivity() {
    private lateinit var etNis: EditText
    private lateinit var etPass: EditText
    private lateinit var spTingkatan : Spinner
    //ini yabg versi 1
//    private val BASE_URL = "https://script.google.com/macros/s/AKfycbxFVV-fGB4wt_1wc5wS5_OyIiauXlcwobSJIStu9F0ZX0LeDOKf/"
    //ini versi 2
    private val BASE_URL = "https://script.google.com/macros/s/AKfycby5vqrqFknw1Nts9cTJALUBOvqKlRXzFtS3Ep4cXIX-uHsqZZEfLOTuLtW9acNKfvzmvA/"
    lateinit var  okHttpClient: OkHttpClient
    lateinit var  gson: Gson
    lateinit var  retrofit: Retrofit
    lateinit var  service: ServiceClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_sim)
        checkPermission()
        etNis = et_nis_login
        etPass = et_pass_login
        spTingkatan = sp_tingkatan

        val isLogin = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE).getBoolean(Constant.SISWA_LOGIN,false)

        if(isLogin){
            startActivity(intentFor<HomeActivity>())
            finish()
            return
        }

        val  btnLogin = btn_login

        btnLogin.onClick {
            if(!validation()){
                return@onClick
            }
            val nis = etNis.text.toString()
            val pass = etPass.text.toString()
            val tingkatan = spTingkatan.selectedItem.toString()
            val pd = ProgressDialog(this@LoginSimActivity)


            pd.setMessage("Load data")
            pd.setCancelable(false)
            pd.show()

            okHttpClient = OkHttpClient().newBuilder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .build()

            gson = GsonBuilder().setLenient().create()

            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            service = retrofit.create(ServiceClient::class.java)

            val getLogin = service.getLogin(
                    "server_siswa",
                    ""+nis,
                    "loginSiswa",
                    ""+pass,
                    ""+tingkatan)


            getLogin.enqueue(object : Callback<ResponseLoginSiswa> {
                override fun onFailure(call: Call<ResponseLoginSiswa>, t: Throwable) {
                    pd.dismiss()
                    toast(""+t.message)
                }

                override fun onResponse(call: Call<ResponseLoginSiswa>, response: Response<ResponseLoginSiswa>) {
                    pd.dismiss()
                    val hasil = response.body()?.hasil
                    when (hasil) {
                        "succes" -> {

                            val namaSiswa = response.body()?.namaSiswa
                            val kelasSiswa = response.body()?.kelasSiswa
                            val serverSiswa = response.body()?.serverSiswa
                            val linkTagihanSiswa = response.body()?.linkTagihan
                            val linkInfoSiswa = response.body()?.linkInfo
                            val linkPresensiSiswa = response.body()?.linkPresensi
                            val linkRaportSiswa = response.body()?.linkRaport
                            val linkPesananTefa = response.body()?.linkPesananTefa
                            val linkKelulusan = response.body()?.linkKelulusan
                            val linkPinjamanPerpus = response.body()?.linkPinjamanPerpus
                            val linkDbServerSiswa = response.body()?.linkDbServerSiswa
                            val linkIjinSiswa = response.body()?.linkIjinSiswa

                            getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE).edit()
                                    .putBoolean(Constant.SISWA_LOGIN,true)
                                    .putString(Constant.SISWA_NAMA,namaSiswa)
                                    .putString(Constant.SISWA_NIS,nis)
                                    .putString(Constant.SISWA_TINGKATAN,tingkatan)
                                    .putString(Constant.SISWA_KELAS,kelasSiswa)
                                    .putString(Constant.SISWA_SERVER,serverSiswa)
                                    .putString(Constant.SISWA_LINK_TAGIHAN,linkTagihanSiswa)
                                    .putString(Constant.SISWA_LINK_INFO_SISWA,linkInfoSiswa)
                                    .putString(Constant.SISWA_LINK_PRESENSI_SISWA,linkPresensiSiswa)
                                    .putString(Constant.SISWA_LINK_RAPORT_SISWA,linkRaportSiswa)
                                    .putString(Constant.SISWA_LINK_PESANAN_TEFA_SISWA,linkPesananTefa)
                                    .putString(Constant.SISWA_LINK_PENGUMUMAN_KELULUSAN_SISWA,linkKelulusan)
                                    .putString(Constant.SISWA_LINK_PEMINJAMAN_PERPUS,linkPinjamanPerpus)
                                    .putString(Constant.SISWA_LINK_DB_SERVER_SISWA,linkDbServerSiswa)
                                    .putString(Constant.SISWA_LINK_IJIN_SISWA,linkIjinSiswa)
                                    .apply()

                            startActivity(intentFor<HomeActivity>())
                            finish()

                        }
                        "error" -> {
                            toast("NIS atau Password salah")
                        }
                        else -> {
                            toast("NIS tidak terdaftar")
                        }
                    }
                }

            })

        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA
                    ),
                    123
            )
        }
    }
    fun validation():Boolean{
        if(etNis.text.isEmpty()){
            toast("NIS tidak boleh kosong")
            return false

        }
        if(etPass.text.isEmpty()){
            toast("Password tidak boleh kosong")
            return false

        }
        return true
    }



}
