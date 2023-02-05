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
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Helper
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.database
import com.smkn9.semarang.sip_smk9.model.ResponServerKodeGuru
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceGenerator
import kotlinx.android.synthetic.main.activity_kode_guru.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.intentFor


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KodeGuruActivity : AppCompatActivity() {

    lateinit var spKelas: Spinner
    lateinit var etKodeGuru: EditText
    lateinit var tvNis: TextView
    lateinit var tingkatanKelas: String
    lateinit var nis: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kode_guru)


        spKelas = this.findViewById(R.id.sp_kelas)
        etKodeGuru = findViewById(R.id.et_kode_guru)
        tvNis = findViewById(R.id.tv_nis_cek_kode_guru)
        tvNis.text = Siswa.getNIS(parent_login_soal)
        //ini untuk ngecek apakah pertama kali, belum login
        val isFirst = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_FIRST,true)
        val isFirstEssay = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_FIRST_ESSAY,true)

        //ini untuk mengecek kesempatan yang sudah digunakan
        val kesempatan:Int = getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE)
                .getInt(Constant.TOMBOL_HOME_BUTTON,0)


        //ini untuk menampilkan tombol lanjut
        //kesempatan hanya dua kali
        if ((!isFirstEssay) and (kesempatan<3)){
            btn_lanjut_essay.visibility = View.VISIBLE
        }else {
            if ((!isFirst) and (kesempatan < 3)) {
                btn_lanjut.visibility = View.VISIBLE

            }
        }

        if(kesempatan == 3){
            clearAppData()
        }


    }

    fun kirimKodeGuru(view: View) {
        if (!validate()) {
            return
        }

        getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE).edit().clear().apply()
        database.use {
            delete(Constant.TABLE_SOAL)
            delete(Constant.TABLE_SOAL_ESSAY)
        }

        nis = tvNis.text.toString()
        val kelas = spKelas.selectedItem.toString()
        val kodeGuru = etKodeGuru.text.toString()

        when (kelas) {
            "X" -> tingkatanKelas = "kelas_x"
            "XI" -> tingkatanKelas = "kelas_xi"
            "XII" -> tingkatanKelas = "kelas_xii"
        }


        val pd = ProgressDialog(this)
        pd.setMessage("Load server ... ")
        pd.setCancelable(false)
        pd.show()

        if (!Helper.isNetworkAvailable(this@KodeGuruActivity)) {
            pd.dismiss()
            Toast.makeText(this, "Jaringan tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val service = ServiceGenerator.createService<ServiceClient>(ServiceClient::class.java)
        val cekKodeGuru = service.getSoalDariGuru(
                "cekKodeSoal",
                "" + tingkatanKelas,
                "" + kodeGuru,
                "" + nis)

        cekKodeGuru.enqueue(object : Callback<ResponServerKodeGuru> {
            override fun onResponse(call: Call<ResponServerKodeGuru>, response: Response<ResponServerKodeGuru>) {
                pd.dismiss()
                val hasil = response.body()?.hasil
                val server = response.body()?.server
                val mapel = response.body()?.mapel
                val nama = response.body()?.nama
                val hp = response.body()?.hp
                val sms = response.body()?.sms
                val ujian = response.body()?.ujian


                if (hasil == "error") {
                    Toast.makeText(this@KodeGuruActivity,
                            "Maaf Kode Guru tidak ditemukan",
                            Toast.LENGTH_SHORT).show()
                    return
                }

                if (hasil == "off") {
                    Toast.makeText(this@KodeGuruActivity,
                            "Maaf Ujian belum Aktif",
                            Toast.LENGTH_SHORT).show()

                } else {

                    getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                            .edit()
                            .putString(Constant.ID_SS_MASTER_GURU, hasil)
                            .putString(Constant.MAPEL_SISWA,mapel)
                            .putString(Constant.NAMA_SISWA,nama)
                            .putString(Constant.NIS_SISWA, nis)
                            .putString(Constant.NO_HP_ORTU_SISWA,hp.toString())
                            .putString(Constant.ID_SERVER_SISWA, server)
                            .putString(Constant.STATUS_SMS, sms)
                            .putString(Constant.JENIS_UJIAN, ujian)
                            .apply()

                    startActivity(Intent(this@KodeGuruActivity,
                            LoginActivity::class.java))
                    finish()
                }


            }

            override fun onFailure(call: Call<ResponServerKodeGuru>, t: Throwable) {
                pd.dismiss()
                Toast.makeText(this@KodeGuruActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun lanjutMengerjakan(view: View){
        startActivity(intentFor<MenuActivity>())
        getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constant.IS_LANJUT,true)
                .apply()
        finish()
    }


    fun validate(): Boolean {
        if (etKodeGuru.text.toString().isEmpty()) {
            Toast.makeText(this, "Kode tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (etNis.text.toString().isEmpty()) {
//            Toast.makeText(this, "NIS tidak boleh kosong", Toast.LENGTH_SHORT).show()
//            return false
//        }
        return true
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

    fun lanjutMengerjakanEssay(view: View) {
        startActivity(intentFor<MenuEssayActivity>())
        getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constant.IS_LANJUT,true)
                .apply()
        finish()
    }
}
