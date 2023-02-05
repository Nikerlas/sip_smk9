@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.adapter.DaftarNilaiAdapter
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.ResponseDaftarNilai
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_daftar_nilai.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DaftarNilaiActivity : AppCompatActivity() {
    lateinit var pd: ProgressDialog
    lateinit var  okHttpClient: OkHttpClient
    lateinit var  gson: Gson
    lateinit var  retrofit: Retrofit
    lateinit var  service: ServiceClient
    lateinit var  rvDetailNilai : androidx.recyclerview.widget.RecyclerView
    lateinit var namaSiswa:String
    lateinit var serverSiswa:String
    lateinit var urlNilai:String
    lateinit var tingkatan:String
    lateinit var kelas:String
    lateinit var nis:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_nilai)
        rvDetailNilai = rv_daftar_nilai


        pd = ProgressDialog(this)
        pd.setMessage("Load data ...")
        pd.setCancelable(false)
        pd.show()

        val sp = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)

        namaSiswa = sp.getString(Constant.SISWA_NAMA,"").toString()
        serverSiswa = sp.getString(Constant.SISWA_SERVER,"").toString()
        urlNilai = sp.getString(Constant.SISWA_LINK_RAPORT_SISWA,"").toString()
        tingkatan = sp.getString(Constant.SISWA_TINGKATAN,"").toString()
        kelas = sp.getString(Constant.SISWA_KELAS,"").toString()
        nis = sp.getString(Constant.SISWA_NIS,"").toString()


        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

        gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
                .baseUrl(""+serverSiswa)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        service = retrofit.create(ServiceClient::class.java)

        rvDetailNilai.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val getNilai = service.getDaftarNilai(
                ""+urlNilai,
                "readDaftarNilai",
                ""+tingkatan,
                ""+kelas,
                ""+nis)

        getNilai.enqueue(object : Callback<ResponseDaftarNilai> {
            override fun onFailure(call: Call<ResponseDaftarNilai>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponseDaftarNilai>, response: Response<ResponseDaftarNilai>) {
                pd.dismiss()
                val nilai = response.body()?.nilai?.get(0)

                val listMapel: MutableList<String> = mutableListOf()
                listMapel.add(0,"btq")
                listMapel.add(1,"bhs daerah")
                listMapel.add(2,"b ind")
                listMapel.add(3," b eng")
                listMapel.add(4,"bk")
                listMapel.add(5,"ekskul")
                listMapel.add(6,"fisika")
                listMapel.add(7,"hansek")
                listMapel.add(8,"ipa")
                listMapel.add(9,"pariwisata")
                listMapel.add(10,"kimia")
                listMapel.add(11,"mtk")
                listMapel.add(12,"pabp")
                listMapel.add(13,"ppkn")
                listMapel.add(14,"penjas")
                listMapel.add(15,"produktif")
                listMapel.add(16,"sejarah")
                listMapel.add(17,"seni budaya")
                listMapel.add(18,"simdig")

                val adapter = DaftarNilaiAdapter(nilai,listMapel,namaSiswa,nis,kelas)
                rvDetailNilai.adapter = adapter


            }

        })
    }
}
