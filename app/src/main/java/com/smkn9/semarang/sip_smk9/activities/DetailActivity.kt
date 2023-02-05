@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.Pengumuman
import com.smkn9.semarang.sip_smk9.model.ResponseInfo
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {
    lateinit var pd: ProgressDialog
    lateinit var  okHttpClient: OkHttpClient
    lateinit var  gson: Gson
    lateinit var  retrofit: Retrofit
    lateinit var  service: ServiceClient
    lateinit var  rvDetail : androidx.recyclerview.widget.RecyclerView
    lateinit var namaSiswa:String
    lateinit var serverSiswa:String
    lateinit var urlSpp:String
    lateinit var urlInfo:String
    lateinit var tingkatan:String
    lateinit var kelas:String
    lateinit var nis:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        rvDetail = rv_detail


        pd = ProgressDialog(this)
        pd.setMessage("Load data ...")
        pd.setCancelable(false)
        pd.show()

        val sp = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)

        namaSiswa = sp.getString(Constant.SISWA_NAMA,"").toString()
        serverSiswa = sp.getString(Constant.SISWA_SERVER,"").toString()
        urlInfo = sp.getString(Constant.SISWA_LINK_INFO_SISWA,"").toString()
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

        val jenisActivity = intent.getStringExtra(Constant.BUNDLE_DETAIL)

        when(jenisActivity){

            "INFO"->loadInfo(urlInfo)

        }

    }



    fun loadInfo(url:String){
        rvDetail.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, true)
        val getInfo = service.getInfo(""+url,"info","info")

        getInfo.enqueue(object : Callback<ResponseInfo> {
            override fun onFailure(call: Call<ResponseInfo>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponseInfo>, response: Response<ResponseInfo>) {
                pd.dismiss()
                val listInfo = response.body()?.results

                val adapter = com.smkn9.semarang.sip_smk9.adapter.InfoAdapter(listInfo as List<Pengumuman>)
                rvDetail.adapter = adapter
            }

        })
    }

}
