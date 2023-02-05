@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.tagihan

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import kotlinx.android.synthetic.main.activity_administrasi.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AdministrasiActivity : AppCompatActivity() {
    lateinit var pd: ProgressDialog
    lateinit var okHttpClient: OkHttpClient
    lateinit var gson: Gson
    lateinit var retrofit: Retrofit
    lateinit var service: ServiceClient
    lateinit var serverSiswa: String
    lateinit var urlTagihan: String
    lateinit var tingkatan: String
    lateinit var kelas: String
    lateinit var nis: String
    lateinit var nama:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrasi)

        pd = ProgressDialog(this)
        pd.setMessage("Load data ...")
        pd.setCancelable(false)
        pd.show()

        val sp = getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)


        serverSiswa = sp.getString(Constant.SISWA_SERVER, "").toString()
        urlTagihan = sp.getString(Constant.SISWA_LINK_TAGIHAN, "").toString()
        tingkatan = sp.getString(Constant.SISWA_TINGKATAN, "").toString()
        kelas = sp.getString(Constant.SISWA_KELAS, "").toString()
        nis = sp.getString(Constant.SISWA_NIS, "").toString()
        nama = sp.getString(Constant.SISWA_NAMA, "").toString()

        init()

        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

        gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
                .baseUrl("" + serverSiswa)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        service = retrofit.create(ServiceClient::class.java)

        val getTagihan = service.getInfoTagihan(
                "" + urlTagihan,
                "readTagihan",
                "" + tingkatan,
                "" + kelas,
                "" + nis)


        getTagihan.enqueue(object : Callback<ResponseTagihan> {
            override fun onFailure(call: Call<ResponseTagihan>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponseTagihan>, response: Response<ResponseTagihan>) {
                pd.dismiss()
                val status = response.body()?.hasil
                if(status == "succes"){
                    val body = response.body()




                    tv_admin_nis.text = nis
                    tv_admin_nama.text = nama
                    tv_admin_kelas.text = kelas

                    //tahun ajaran
                    tv_tagihan_tahun_ajaran.text = body?.info?.thnAjaran

                    //tagihan spp
                    tv_jml_byr_juli.text = convert(body?.tagSpp?.juli)
                    tv_jml_byr_agust.text = convert(body?.tagSpp?.agust)
                    tv_jml_byr_sept.text = convert(body?.tagSpp?.sept)
                    tv_jml_byr_okt.text = convert(body?.tagSpp?.okt)
                    tv_jml_byr_nov.text = convert(body?.tagSpp?.nov)
                    tv_jml_byr_des.text = convert(body?.tagSpp?.des)
                    tv_jml_byr_jan.text = convert(body?.tagSpp?.jan)
                    tv_jml_byr_feb.text = convert(body?.tagSpp?.feb)
                    tv_jml_byr_maret.text = convert(body?.tagSpp?.maret)
                    tv_jml_byr_april.text = convert(body?.tagSpp?.april)
                    tv_jml_byr_mei.text = convert(body?.tagSpp?.mei)
                    tv_jml_byr_juni.text = convert(body?.tagSpp?.juni)

                    tv_tgl_byr_juli.text = body?.tagSpp?.tglByrJuli
                    tv_tgl_byr_agust.text = body?.tagSpp?.tglByrAgust
                    tv_tgl_byr_sept.text = body?.tagSpp?.tglByrSept
                    tv_tgl_byr_okt.text = body?.tagSpp?.tglByrOkt
                    tv_tgl_byr_nov.text = body?.tagSpp?.tglByrNov
                    tv_tgl_byr_des.text = body?.tagSpp?.tglByrDes
                    tv_tgl_byr_jan.text = body?.tagSpp?.tglByrJan
                    tv_tgl_byr_feb.text = body?.tagSpp?.tglByrFeb
                    tv_tgl_byr_maret.text = body?.tagSpp?.tglByrMaret
                    tv_tgl_byr_april.text = body?.tagSpp?.tglByrApril
                    tv_tgl_byr_mei.text = body?.tagSpp?.tglByrMei
                    tv_tgl_byr_juni.text = body?.tagSpp?.tglByrJuni

                    //tag lain lain
                    tv_jml_byr_angs.text = convert(body?.tagLain?.angs)
                    tv_jml_byr_sem_1.text = convert(body?.tagLain?.sem1)
                    tv_jml_byr_sem_2.text = convert(body?.tagLain?.sem2)
                    tv_jml_byr_uang_praktik.text = convert(body?.tagLain?.uangPraktik)
                    tv_jml_byr_pkl.text = convert(body?.tagLain?.pkl)
                    tv_jml_byr_ebta.text = convert(body?.tagLain?.ebta)
                    tv_jml_byr_bakti_lulus.text = convert(body?.tagLain?.baktiLulus)

                    tv_tgl_byr_angs.text = body?.tagLain?.tglByrAngs.toString()
                    tv_tgl_byr_sem_1.text = body?.tagLain?.tglByrSem1.toString()
                    tv_tgl_byr_sem_2.text = body?.tagLain?.tglByrSem2.toString()
                    tv_tgl_byr_uang_praktik.text = body?.tagLain?.tglByrUangPraktik.toString()
                    tv_tgl_byr_pkl.text = body?.tagLain?.tglByrPkl.toString()
                    tv_tgl_byr_ebta.text = body?.tagLain?.tglByrEbta.toString()
                    tv_tgl_byr_bakti_lulus.text = body?.tagLain?.tglByrBaktiLulus.toString()

                }else{

                    toast("Data dengan nis "+nis+" tidak diketemukan")
                }

            }

        })


    }


    fun init(){
        when(tingkatan){
            "X" -> {
               tr_admin_pkl.visibility =View.GONE
               tr_admin_ebta.visibility = View.GONE
               tr_admin_bakti_lulus.visibility = View.GONE

            }
            "XI"->{
                tr_admin_angs.visibility = View.GONE
                tr_admin_ebta.visibility = View.GONE
                tr_admin_bakti_lulus.visibility = View.GONE
            }
            "XII"->{
                tr_admin_sem_2_x_xi.visibility =View.GONE
                tr_admin_pkl.visibility = View.GONE
                tr_admin_angs.visibility = View.GONE
            }
        }
    }

    fun convert(nilai:Int?):String{

        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(nilai)
//        return formatRupiah.format(nilai).replace("Rp","")
    }
}

