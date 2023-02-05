@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.adapter.PresensiAdapter
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.ResponsePresensiBulanan
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_detail_presensi.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DetailPresensiActivity : AppCompatActivity() {

    lateinit var pd: ProgressDialog
    lateinit var  okHttpClient: OkHttpClient
    lateinit var  gson: Gson
    lateinit var  retrofit: Retrofit
    lateinit var  service: ServiceClient
    lateinit var serverSiswa:String
    lateinit var urlPresensi:String
    lateinit var tingkatan:String
    lateinit var kelas:String
    lateinit var nis:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_presensi)

        val tvBulanPresensi = tv_presensi_bulan
        val rvPresensi = rv_presensi_bulanan

        val bulanPresensi = intent.getStringExtra(com.smkn9.semarang.sip_smk9.helper.Constant.BUNDLE_DETAIL)
        tvBulanPresensi.text = bulanPresensi

        rvPresensi.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 7)

        pd = ProgressDialog(this)
        pd.setMessage("Load data ...")
        pd.setCancelable(false)
        pd.show()

        val sp = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)


        serverSiswa = sp.getString(Constant.SISWA_SERVER,"").toString()
        urlPresensi = sp.getString(Constant.SISWA_LINK_PRESENSI_SISWA,"").toString()
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

        val getPresensi =  service.getPresensi(
                ""+urlPresensi,
                "readPresensiBulanan",
                ""+tingkatan,
                ""+kelas,
                ""+bulanPresensi,
                ""+nis
        )

        getPresensi.enqueue(object : Callback<ResponsePresensiBulanan> {
            override fun onFailure(call: Call<ResponsePresensiBulanan>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponsePresensiBulanan>, response: Response<ResponsePresensiBulanan>) {
                pd.dismiss()
                val respon = response.body()

//                val listStatus :MutableList<String> = mutableListOf()
                val listStatus :MutableList<String> = mutableListOf()

                val tgl1 = respon?.results?.get(0)?.tanggal1
                tgl1?.let { listStatus.add(it) }
                val tgl2 = respon?.results?.get(0)?.tanggal2
                tgl2?.let { listStatus.add(it) }
                val tgl3 = respon?.results?.get(0)?.tanggal3
                tgl3?.let { listStatus.add(it) }
                val tgl4 = respon?.results?.get(0)?.tanggal4
                tgl4?.let { listStatus.add(it) }
                val tgl5 = respon?.results?.get(0)?.tanggal5
                tgl5?.let { listStatus.add(it) }
                val tgl6 = respon?.results?.get(0)?.tanggal6
                tgl6?.let { listStatus.add(it) }
                val tgl7 = respon?.results?.get(0)?.tanggal7
                tgl7?.let { listStatus.add(it) }
                val tgl8 = respon?.results?.get(0)?.tanggal8
                tgl8?.let { listStatus.add(it) }
                val tgl9 = respon?.results?.get(0)?.tanggal9
                tgl9?.let { listStatus.add(it) }
                val tgl10 = respon?.results?.get(0)?.tanggal10
                tgl10?.let { listStatus.add(it) }
                val tgl11 = respon?.results?.get(0)?.tanggal11
                tgl11?.let { listStatus.add(it) }
                val tgl12 = respon?.results?.get(0)?.tanggal12
                tgl12?.let { listStatus.add(it) }
                val tgl13 = respon?.results?.get(0)?.tanggal13
                tgl13?.let { listStatus.add(it) }
                val tgl14 = respon?.results?.get(0)?.tanggal14
                tgl14?.let { listStatus.add(it) }
                val tgl15 = respon?.results?.get(0)?.tanggal15
                tgl15?.let { listStatus.add(it) }
                val tgl16 = respon?.results?.get(0)?.tanggal16
                tgl16?.let { listStatus.add(it) }
                val tgl17 = respon?.results?.get(0)?.tanggal17
                tgl17?.let { listStatus.add(it) }
                val tgl18 = respon?.results?.get(0)?.tanggal18
                tgl18?.let { listStatus.add(it) }
                val tgl19 = respon?.results?.get(0)?.tanggal19
                tgl19?.let { listStatus.add(it) }
                val tgl20 = respon?.results?.get(0)?.tanggal20
                tgl20?.let { listStatus.add(it) }
                val tgl21 = respon?.results?.get(0)?.tanggal21
                tgl21?.let { listStatus.add(it) }
                val tgl22 = respon?.results?.get(0)?.tanggal22
                tgl22?.let { listStatus.add(it) }
                val tgl23 = respon?.results?.get(0)?.tanggal23
                tgl23?.let { listStatus.add(it) }
                val tgl24 = respon?.results?.get(0)?.tanggal24
                tgl24?.let { listStatus.add(it) }
                val tgl25 = respon?.results?.get(0)?.tanggal25
                tgl25?.let { listStatus.add(it) }
                val tgl26 = respon?.results?.get(0)?.tanggal26
                tgl26?.let { listStatus.add(it) }
                val tgl27 = respon?.results?.get(0)?.tanggal27
                tgl27?.let { listStatus.add(it) }
                val tgl28 = respon?.results?.get(0)?.tanggal28
                tgl28?.let { listStatus.add(it) }
                val tgl29 = respon?.results?.get(0)?.tanggal29
                tgl29?.let { listStatus.add(it) }
                val tgl30 = respon?.results?.get(0)?.tanggal30
                tgl30?.let { listStatus.add(it) }
                val tgl31 = respon?.results?.get(0)?.tanggal31
                tgl31?.let { listStatus.add(it) }


                val adapterPresensi = PresensiAdapter(listStatus)
                rvPresensi.adapter = adapterPresensi
            }

        })

    }
}
