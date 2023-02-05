@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.essay

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.adapter.SoalEssayAdapter
import com.smkn9.semarang.sip_smk9.adapter.SoalEssayAdapter2
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.model.*
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import okhttp3.OkHttpClient
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MenuEssayActivity : AppCompatActivity() {

    lateinit var rvMenuSoalEssay: androidx.recyclerview.widget.RecyclerView
    lateinit var rvSoalEssay: androidx.recyclerview.widget.RecyclerView
    lateinit var tvCountDownTimer: TextView
    lateinit var pbMenuSoalEssay: ProgressBar
    var listSoal: List<SoalEssayItem>? = ArrayList()
    var listSoal2: MutableList<SoalEssayItem2> = mutableListOf()
    var listJawaban: MutableList<String> = ArrayList()
    var listIndekSoal: MutableList<Int> = ArrayList()
    var noSoal: MutableList<Int> = ArrayList()

    lateinit var adapter: SoalEssayAdapter
    lateinit var adapter2: SoalEssayAdapter2
    lateinit var noSoalEssayAdapter: NoSoalEssayAdapter

//    lateinit var pd: ProgressDialog
    lateinit var timer: Timer


    companion object {
        var hour: Int = 0
        var minute: Int = 0
        var second: Int = 0

    }

    var jumlahSoal: Int = 0
    lateinit var urlSheet: String
    lateinit var server: String
    lateinit var okHttpClient: OkHttpClient
    lateinit var gson: Gson
    lateinit var retrofit: Retrofit

    lateinit var nis: String
    lateinit var listNoSoal: MutableList<QuestionNumberModel>
    lateinit var b: Bundle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_essay)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_essay)
        val tvToolbar = findViewById<TextView>(R.id.tv_toolbar_essay)
        pbMenuSoalEssay = findViewById(R.id.pb_menu_soal_essay)

        val namaMapel = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.MAPEL_SISWA, "")


        tvToolbar.text = namaMapel
        //toolbar.setTitle("Pemrograman Perangkat Bergerak");

        setSupportActionBar(toolbar)


        val isFirst = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_FIRST_ESSAY, true)

        urlSheet = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.ID_SS_MASTER_GURU, "").toString()

        server = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.ID_SERVER_SISWA, "").toString()

        nis = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.NIS_SISWA, "").toString()


        jumlahSoal = getSharedPreferences(Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                .getInt(Constant.JUMLAH_SOAL_ESSAY, 0)

        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()

        gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
                .baseUrl(server)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        //---menambahkan banyak soal untuk no soal//


        for (i in 1..jumlahSoal) {

            noSoal.add(i)

        }

        //----//

        rvMenuSoalEssay = findViewById(R.id.rv_menu_navigation_essay)
        rvSoalEssay = findViewById(R.id.rv_soal_essay)
        tvCountDownTimer = findViewById(R.id.tv_content_time_essay)

        rvMenuSoalEssay.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 5)
        rvSoalEssay.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)

        //bagian ini digunakan ketika swipe kiri kanan rv nya menjadi full
        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvSoalEssay)

        if(isFirst){

            hour = getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.JAM_UJIAN_ESSAY, 0)

            minute = getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.MENIT_UJIAN_ESSAY, 0)

            second = getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.DETIK_UJIAN_ESSAY, 0)

            pbMenuSoalEssay.visibility = View.VISIBLE
            //blok ini digunakan untuk mendapat soal dari server
            val service = retrofit.create(ServiceClient::class.java)
            val requestSoal = service.getListSoalEssay(
                    "" + urlSheet,
                    "read_essay",
                    "soal_essay")
            requestSoal.enqueue(object : Callback<ResponseReadEssay> {
                override fun onResponse(call: Call<ResponseReadEssay>, response: retrofit2.Response<ResponseReadEssay>) {
//                    pd.dismiss()
                    pbMenuSoalEssay.visibility = View.GONE
                    listSoal = response.body()?.soalEssay as List<SoalEssayItem>?


                    //dilakukan untuk mengacak soal
                    Collections.shuffle(listSoal)





                    adapter = SoalEssayAdapter(listSoal, jumlahSoal, this@MenuEssayActivity)
                    rvSoalEssay.adapter = adapter

                    val kesempatan = 0
                    getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).edit().putInt(Constant.TOMBOL_HOME_BUTTON, kesempatan).apply()
                    //untuk mencetak objek yang menghandle countdown timer
                    loadWaktu()
                    getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(Constant.IS_FIRST_ESSAY, false)
                            .apply()

//                    cekKeyBoard()
                }

                override fun onFailure(call: Call<ResponseReadEssay>, t: Throwable) {
//                    pd.dismiss()
                    pbMenuSoalEssay.visibility = View.GONE
                    Toast.makeText(this@MenuEssayActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
        else{
            hour = getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.JAM_UJIAN_ESSAY_SIMPAN, 0)

            minute = getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.MENIT_UJIAN_ESSAY_SIMPAN, 0)

            second = getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.DETIK_UJIAN_ESSAY_SIMPAN, 0)




//            database.use {
//                val data = select(Constant.TABLE_SOAL_ESSAY)
//
//                val result = data.parseList(classParser<SoalEssayItem2>())
//
//                listSoal2.addAll(result)
//
//            }



            adapter2 = SoalEssayAdapter2(jumlahSoal, this@MenuEssayActivity)
            rvSoalEssay.adapter = adapter2

            loadWaktu()
//            cekKeyBoard()
        }

        val drawer = findViewById<View>(R.id.drawer_layout_menu_essay) as androidx.drawerlayout.widget.DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()


        val navigationView = findViewById<View>(R.id.nav_view_essay) as NavigationView
        // navigationView.setNavigationItemSelectedListener(this);
        listNoSoal = ArrayList()

        loadNomor()



    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInMultiWindowMode) {
                timer.purge()
                finish()
            }
        }

    }



    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout_menu_essay) as androidx.drawerlayout.widget.DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed();
        }
    }

    override fun onUserLeaveHint() {
        timer.purge()
        cekKesempatan()

    }

    fun loadNomor() {
        listNoSoal.clear()
        for (i in noSoal.indices) {
            val no = QuestionNumberModel(noSoal[i])
            listNoSoal.add(no)
        }

        noSoalEssayAdapter = NoSoalEssayAdapter(this, listNoSoal)
        rvMenuSoalEssay.adapter = noSoalEssayAdapter
    }

    fun loadWaktu(){
        timer = Timer()

        val kesempatan = getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).getInt(Constant.TOMBOL_HOME_BUTTON, 0)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                //proses menghitung mundur ini
                //kita lakukan di background agar tidak menganggu main Thread
                runOnUiThread {
                    tvCountDownTimer.text = "Sisa waktu : ${hour} : ${minute} : ${second}"

                    //memberi event jika waktu habis
                    //mulai dari jam dulu
                    if (hour == 0 && minute == 0 && second == 0) {
                        Toast.makeText(this@MenuEssayActivity,
                                "Maaf waktu anda habis",
                                Toast.LENGTH_SHORT).show()
//                        timer.purge()
                        kirimJawabanFinalWaktuHabis()

                    } else if (minute == 0 && second == 0) {
                        hour--
                        minute = 60
                        second = 60
                    } else if (second == 0) {
                        minute--
                        second = 60
                    }
                    when (kesempatan) {
                        0 -> second = second - 1
                        1 -> second = second - 1+1
                        2 -> second = second - 1+1
                    }
//                    second--
                }
            }
        }, 0, 1000)
    }
//    fun cekKeyBoard(){
//        KeyboardUtils.addKeyboardToggleListener(this@MenuEssayActivity, object : KeyboardUtils.SoftKeyboardToggleListener {
//            override fun onToggleSoftKeyboard(isVisible: Boolean) {
//                if (isVisible) {
//                    cekKesempatan()
//                }
//            }
//        })
//    }

    fun cekKesempatan(){
        val kesempatan = getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).getInt(Constant.TOMBOL_HOME_BUTTON, 0)
        when(kesempatan <3 ){
            true -> getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).edit().putInt(Constant.TOMBOL_HOME_BUTTON, kesempatan + 1).apply()
            false -> getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).edit().putBoolean(Constant.TOMBOL_HOME_BUTTON, true).apply()
        }
        finish()
    }

    fun onRefreshNoSoalEssay(view: View) {
        noSoalEssayAdapter.notifyDataSetChanged()
    }

    fun kirimJawabanEssay(view: View) {
        alert {
            message = "Apakah sudah yakin dengan jawaban Anda ?"
            yesButton {
                kirimJawabanFinal()
//                cobaGet()
            }

            noButton {

            }
        }.show()
    }

    fun cobaGet(){
        pbMenuSoalEssay.visibility = View.VISIBLE

        closeNavigation()

        val isLanjut = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_LANJUT, false)
        listJawaban.clear()
        listIndekSoal.clear()

        if (!isLanjut){
            var jawaban: String
            for (i in 0 until jumlahSoal) {
                //            String soal = listSoal.get(i).getSoal();
                val index = listSoal?.get(i)?.noSoal
                if (listSoal?.get(i)?.finalAnswer == null) {
                    jawaban = "none"

                } else {
                    jawaban = listSoal?.get(i)?.finalAnswer.toString()
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index?.let { listIndekSoal.add(it) }
            }
        }else{
            var jawaban: String
            for (i in 0 until jumlahSoal) {
                //            String soal = listSoal.get(i).getSoal();
                val index = getSharedPreferences(Constant.LIST_NO_ESSAY_HEADER, MODE_PRIVATE)
                        .getInt(Constant.LIST_NO_ESSAY+i, 0)
                val jawabanEssay = getSharedPreferences(Constant.LIST_JAWABAN_ESSAY_HEADER, MODE_PRIVATE).getString(Constant.LIST_JAWABAN_ESSAY+i,"")
                if ( jawabanEssay== "") {
                    jawaban = "none"

                } else {
                    jawaban = jawabanEssay!!
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index.let {
                    if (it != null) {
                        listIndekSoal.add(it)
                    }
                }
            }
        }

        val service = retrofit.create(ServiceClient::class.java)
        val sendAnswer = service.sendAnswer(
                "ujian",
                "" + urlSheet,
                "insert",
                "jawaban_essay",
                nis,
                listIndekSoal,
                listJawaban,
                "" + listIndekSoal.size)

        sendAnswer.enqueue(object : Callback<ResponServer> {
            override fun onResponse(call: Call<ResponServer>, response: retrofit2.Response<ResponServer>) {
//                pd1.dismiss()
                pbMenuSoalEssay.visibility = View.GONE
                val hasil = response.body()?.hasil
                if (hasil == "success") {
//                    startActivity(Intent(this@MenuEssayActivity, ResultActivity::class.java))
//                    finish()
                    toast("Berhasil")
                } else {
                    Toast.makeText(this@MenuEssayActivity, "" + "Pengiriman gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
//                pd1.dismiss()
//                Log.d("infoRespon",""+t.message)
                pbMenuSoalEssay.visibility = View.GONE
                Toast.makeText(this@MenuEssayActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })



    }

    fun kirimJawabanFinal() {


        pbMenuSoalEssay.visibility = View.VISIBLE

        closeNavigation()

        val isLanjut = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_LANJUT, false)
        listJawaban.clear()
        listIndekSoal.clear()

        if (!isLanjut){
            var jawaban: String
            for (i in 0 until jumlahSoal) {
                //            String soal = listSoal.get(i).getSoal();
                val index = listSoal?.get(i)?.noSoal
                if (listSoal?.get(i)?.finalAnswer == null) {
                    pbMenuSoalEssay.visibility = View.GONE
                    toast("Ada soal yang belum dijawab")
                    return
//                    jawaban = "none"

                } else {
                    jawaban = listSoal?.get(i)?.finalAnswer.toString()
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index?.let { listIndekSoal.add(it) }
            }
        }else{
            var jawaban: String
            for (i in 0 until jumlahSoal) {
                //            String soal = listSoal.get(i).getSoal();
                val index = getSharedPreferences(Constant.LIST_NO_ESSAY_HEADER, MODE_PRIVATE)
                        .getInt(Constant.LIST_NO_ESSAY+i, 0)
                val jawabanEssay = getSharedPreferences(Constant.LIST_JAWABAN_ESSAY_HEADER, MODE_PRIVATE)
                        .getString(Constant.LIST_JAWABAN_ESSAY+i,"")
                if ( jawabanEssay== "") {
                    pbMenuSoalEssay.visibility = View.GONE
                    toast("Ada soal yang belum dijawab")
                    return
//                    jawaban = "none"

                } else {
                    jawaban = jawabanEssay!!
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index?.let { listIndekSoal.add(it) }
            }

            Log.d("ujian","jawaban "+listJawaban.toString()+" url "+urlSheet+" jumlah soal : "+listIndekSoal)

        }

        val service = retrofit.create(ServiceClient::class.java)
        val sendAnswer = service.sendAnswer(
                "ujian",
                "" + urlSheet,
                "insert",
                "jawaban_essay",
                nis,
                listIndekSoal,
                listJawaban,
                "" + listIndekSoal.size)

        sendAnswer.enqueue(object : Callback<ResponServer> {
            override fun onResponse(call: Call<ResponServer>, response: retrofit2.Response<ResponServer>) {
//                pd1.dismiss()
                pbMenuSoalEssay.visibility = View.GONE
                val hasil = response.body()?.hasil
                if (hasil == "success") {
                    startActivity(Intent(this@MenuEssayActivity, EssayResultActivity::class.java))
                    timer.purge()
                    finish()
                } else {
                    Toast.makeText(this@MenuEssayActivity, "" + "Pengiriman gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
//                pd1.dismiss()
//                Log.d("infoRespon",""+t.message)
                pbMenuSoalEssay.visibility = View.GONE
                Toast.makeText(this@MenuEssayActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun kirimJawabanFinalWaktuHabis() {


        pbMenuSoalEssay.visibility = View.VISIBLE

        closeNavigation()

        val isLanjut = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_LANJUT, false)
        listJawaban.clear()
        listIndekSoal.clear()

        if (!isLanjut){
            var jawaban: String
            for (i in 0 until jumlahSoal) {
                //            String soal = listSoal.get(i).getSoal();
                val index = listSoal?.get(i)?.noSoal
                if (listSoal?.get(i)?.finalAnswer == null) {
                    pbMenuSoalEssay.visibility = View.GONE
                    jawaban = "none"

                } else {
                    jawaban = listSoal?.get(i)?.finalAnswer.toString()
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index?.let { listIndekSoal.add(it) }
            }
        }else{
            var jawaban: String
            for (i in 0 until jumlahSoal) {
                //            String soal = listSoal.get(i).getSoal();
                val index = getSharedPreferences(Constant.LIST_NO_ESSAY_HEADER, MODE_PRIVATE)
                        .getInt(Constant.LIST_NO_ESSAY+i, 0)
                val jawabanEssay = getSharedPreferences(Constant.LIST_JAWABAN_ESSAY_HEADER, MODE_PRIVATE)
                        .getString(Constant.LIST_JAWABAN_ESSAY+i,"")
                if ( jawabanEssay== "") {
                    pbMenuSoalEssay.visibility = View.GONE
                    jawaban = "none"

                } else {
                    jawaban = jawabanEssay!!
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index?.let { listIndekSoal.add(it) }
            }


        }

        val service = retrofit.create(ServiceClient::class.java)
        val sendAnswer = service.sendAnswer(
                "ujian",
                "" + urlSheet,
                "insert",
                "jawaban_essay",
                nis,
                listIndekSoal,
                listJawaban,
                "" + listIndekSoal.size)

        sendAnswer.enqueue(object : Callback<ResponServer> {
            override fun onResponse(call: Call<ResponServer>, response: retrofit2.Response<ResponServer>) {
//                pd1.dismiss()
                pbMenuSoalEssay.visibility = View.GONE
                val hasil = response.body()?.hasil
                if (hasil == "success") {
                    startActivity(Intent(this@MenuEssayActivity, EssayResultActivity::class.java))
                    timer.purge()
                    finish()
                } else {
                    Toast.makeText(this@MenuEssayActivity, "" + "Pengiriman gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
//                pd1.dismiss()
//                Log.d("infoRespon",""+t.message)
                pbMenuSoalEssay.visibility = View.GONE
                Toast.makeText(this@MenuEssayActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun cobaNoEssay(view: View) {

        val tv = view.findViewById<TextView>(R.id.tv_item_question_essay_number)
        val noSoal = tv.text as String
        Toast.makeText(this, "" + tv.text, Toast.LENGTH_SHORT).show()
        rvSoalEssay.scrollToPosition(Integer.valueOf(noSoal) - 1)

        closeNavigation()
    }

    fun closeNavigation() {
        val drawer = findViewById<View>(R.id.drawer_layout_menu_essay) as androidx.drawerlayout.widget.DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {

        }
    }




}