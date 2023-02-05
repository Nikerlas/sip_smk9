@file:Suppress("DEPRECATION", "JavaCollectionsStaticMethodOnImmutableList")

package com.smkn9.semarang.sip_smk9.activities


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.adapter.SoalAdapter
import com.smkn9.semarang.sip_smk9.adapter.SoalAdapter2
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.KeyboardUtils
import com.smkn9.semarang.sip_smk9.helper.database
import com.smkn9.semarang.sip_smk9.model.*
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import okhttp3.OkHttpClient
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class MenuActivity : AppCompatActivity() {
    lateinit var rvMenuSoal: androidx.recyclerview.widget.RecyclerView
    lateinit var rvSoal: androidx.recyclerview.widget.RecyclerView
    lateinit var tvCountDownTimer: TextView
    lateinit var pbMenuSoal: ProgressBar
    var listSoal: List<SoalItem>? = ArrayList()
    var listSoal2: MutableList<SoalItem2> = mutableListOf()
    var listJawaban: MutableList<String> = ArrayList()
    var listIndekSoal: MutableList<Int> = ArrayList()
    var noSoal: MutableList<Int> = ArrayList()


    lateinit var adapter: com.smkn9.semarang.sip_smk9.adapter.SoalAdapter
    lateinit var adapter2: com.smkn9.semarang.sip_smk9.adapter.SoalAdapter2
    lateinit var noSoalAdapter: com.smkn9.semarang.sip_smk9.adapter.NoSoalAdapter
    lateinit var pd: ProgressDialog
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




    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val tvToolbar = findViewById<TextView>(R.id.tv_toolbar)
        pbMenuSoal = findViewById(R.id.pb_menu_soal)
        val namaMapel = getSharedPreferences(Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(Constant.MAPEL_SISWA, "")
        tvToolbar.text = namaMapel
        //toolbar.setTitle("Pemrograman Perangkat Bergerak");

        setSupportActionBar(toolbar)


        val isFirst = getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                .getBoolean(Constant.IS_FIRST, true)







        urlSheet = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_GURU, "").toString()

        server = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SERVER_SISWA, "").toString()

        nis = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.ID_SS_MASTER_SEKOLAH_HEADER, Context.MODE_PRIVATE)
                .getString(com.smkn9.semarang.sip_smk9.helper.Constant.NIS_SISWA, "").toString()


        jumlahSoal = getSharedPreferences(Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                .getInt(Constant.JUMLAH_SOAL, 0)

        okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
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

        rvMenuSoal = findViewById(R.id.rv_menu_navigation)
        rvSoal = findViewById(R.id.rv_soal)
        tvCountDownTimer = findViewById<View>(R.id.tv_content_time) as TextView

        rvMenuSoal.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 5)
        rvSoal.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)

        //bagian ini digunakan ketika swipe kiri kanan rv nya menjadi full
        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvSoal)

        if(isFirst){

            hour = getSharedPreferences(Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.JAM_UJIAN, 0)

            minute = getSharedPreferences(Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.MENIT_UJIAN, 0)

            second = getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.DURASI_UJIAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(com.smkn9.semarang.sip_smk9.helper.Constant.DETIK_UJIAN, 0)

//            pd = ProgressDialog(this)
//            pd.setMessage("Load data from server")
//            pd.setCancelable(false)
//            pd.show()
            pbMenuSoal.visibility = View.VISIBLE
            //blok ini digunakan untuk mendapat soal dari server
            val service = retrofit.create(ServiceClient::class.java)
            val requestSoal = service.getListSoal("" + urlSheet, "read", "soal")
            requestSoal.enqueue(object : Callback<Response> {
                override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
//                    pd.dismiss()
                    pbMenuSoal.visibility = View.GONE
                    listSoal = response.body()?.listSoal

                    //dilakukan untuk mengacak soal
                    Collections.shuffle(listSoal)

                    doAsync {
                        database.use {
                            listSoal?.indices?.forEach { i ->
                                insert(Constant.TABLE_SOAL,
                                        Constant.COLUMN_NO_SOAL to listSoal?.get(i)?.noSoal,
                                        Constant.COLUMN_SOAL to listSoal?.get(i)?.soal,
                                        Constant.COLUMN_SOAL_GAMBAR to listSoal?.get(i)?.idGambar,
                                        Constant.COLUMN_JAWABAN_A to listSoal?.get(i)?.answerA,
                                        Constant.COLUMN_JAWABAN_B to listSoal?.get(i)?.answerB,
                                        Constant.COLUMN_JAWABAN_C to listSoal?.get(i)?.answerC,
                                        Constant.COLUMN_JAWABAN_D to listSoal?.get(i)?.answerD,
                                        Constant.COLUMN_JAWABAN_E to listSoal?.get(i)?.answerE,
                                        Constant.COLUMN_JAWABAN_A_GAMBAR to listSoal?.get(i)?.getaGambar(),
                                        Constant.COLUMN_JAWABAN_B_GAMBAR to listSoal?.get(i)?.getbGambar(),
                                        Constant.COLUMN_JAWABAN_C_GAMBAR to listSoal?.get(i)?.getcGambar(),
                                        Constant.COLUMN_JAWABAN_D_GAMBAR to listSoal?.get(i)?.getdGambar(),
                                        Constant.COLUMN_JAWABAN_E_GAMBAR to listSoal?.get(i)?.geteGambar(),
                                        Constant.COLUMN_JAWABAN_FINAL to listSoal?.get(i)?.finalAnswer

                                )


                            }
                        }
                    }

                    adapter = SoalAdapter(listSoal, jumlahSoal, this@MenuActivity)
                    rvSoal.adapter = adapter



                    //untuk mencetak objek yang menghandle countdown timer
                    loadWaktu()
                    getSharedPreferences(Constant.IS_FIRST_HEADER, Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(Constant.IS_FIRST, false)
                            .apply()

                    cekKeyBoard()
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    pd.dismiss()
                    Toast.makeText(this@MenuActivity, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
        else{
            hour = getSharedPreferences(Constant.DURASI_UJIAN_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.JAM_UJIAN_SIMPAN, 0)

            minute = getSharedPreferences(Constant.DURASI_UJIAN_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.MENIT_UJIAN_SIMPAN, 0)

            second = getSharedPreferences(Constant.DURASI_UJIAN_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .getInt(Constant.DETIK_UJIAN_SIMPAN, 0)


                database.use {
                    val data = select(Constant.TABLE_SOAL)

                    val result = data.parseList(classParser<SoalItem2>())

                        listSoal2.addAll(result)

                }



            adapter2 = SoalAdapter2(listSoal2, jumlahSoal, this@MenuActivity)
            rvSoal.adapter = adapter2

            loadWaktu()
            cekKeyBoard()
        }


        val drawer = findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()


        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        // navigationView.setNavigationItemSelectedListener(this);
        listNoSoal = ArrayList()

        loadNomor()




    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInMultiWindowMode) {
//                timer.purge()
                finish()
            }
        }

    }



    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout
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

    fun cekKeyBoard(){
        KeyboardUtils.addKeyboardToggleListener(this@MenuActivity, object : KeyboardUtils.SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                if (isVisible) {
//                    timer.purge()
                    cekKesempatan()
                }
            }
        })
    }

    fun cekKesempatan(){
        val kesempatan = getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).getInt(Constant.TOMBOL_HOME_BUTTON, 0)
        when(kesempatan <3 ){
            true -> getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).edit().putInt(Constant.TOMBOL_HOME_BUTTON, kesempatan + 1).apply()
            false -> getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).edit().putBoolean(Constant.TOMBOL_HOME_BUTTON, true).apply()
        }
        finish()
    }


    //mengirim jawaban ke server di menu nav drawer
    fun kirimJawaban(view: View) {



        alert {
            message = "Apakah sudah yakin dengan jawaban Anda ?"
            yesButton {
                kirimJawabanFinal()
            }

            noButton {

            }
        }.show()


    }

    //ketika no di navigasi drawer diklik
    fun cobaNo(view: View) {
        val tv = view.findViewById<TextView>(R.id.tv_item_question_number)
        val noSoal = tv.text as String
        Toast.makeText(this, "" + tv.text, Toast.LENGTH_SHORT).show()
        rvSoal.scrollToPosition(Integer.valueOf(noSoal) - 1)

        closeNavigation()
    }


    //this part for showing question nummber di navigation drawer
    fun loadNomor() {
        listNoSoal.clear()
        for (i in noSoal.indices) {
            val no = QuestionNumberModel(noSoal[i])
            listNoSoal.add(no)
        }

        noSoalAdapter = com.smkn9.semarang.sip_smk9.adapter.NoSoalAdapter(this, listNoSoal)
        rvMenuSoal.adapter = noSoalAdapter
    }


    fun closeNavigation() {
        val drawer = findViewById<View>(R.id.drawer_layout) as androidx.drawerlayout.widget.DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {

        }
    }

    fun kirimJawabanFinal() {
//        val pd1 = ProgressDialog(this)
//        pd1.setMessage("send Answer to Server")
//        pd1.setCancelable(false)
//        pd1.show()

        pbMenuSoal.visibility = View.VISIBLE

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
                    pbMenuSoal.visibility = View.GONE
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
                val index = listSoal2.get(i).noSoal
                if (listSoal2.get(i).finalAnswer == null) {
                    pbMenuSoal.visibility = View.GONE
                    toast("Ada soal yang belum terjawab")
                    return
//                    jawaban = "none"

                } else {
                    jawaban = listSoal2.get(i).finalAnswer.toString()
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index.let { listIndekSoal.add(it) }
            }
        }


//        Log.d("infoData",""+listIndekSoal.toString())
//        var jawaban: String
//        for (i in 0 until jumlahSoal) {
//            //            String soal = listSoal.get(i).getSoal();
//            val index = listSoal?.get(i)?.noSoal
//            if (listSoal?.get(i)?.finalAnswer == null) {
//                jawaban = "none"
//
//            } else {
//                jawaban = listSoal?.get(i)?.finalAnswer.toString()
//            }
//            //                listSoalTerjawab.add(i, soal);
//            listJawaban.add(i, jawaban)
//
//            index?.let { listIndekSoal.add(it) }
//        }


        val service = retrofit.create(ServiceClient::class.java)
        val sendAnswer = service.sendAnswer(
                "ujian",
                "" + urlSheet,
                "insert",
                "jawaban",
                nis,
                listIndekSoal,
                listJawaban,
                "" + listIndekSoal.size)

        sendAnswer.enqueue(object : Callback<ResponServer> {
            override fun onResponse(call: Call<ResponServer>, response: retrofit2.Response<ResponServer>) {
//                pd1.dismiss()
                pbMenuSoal.visibility = View.GONE
                val hasil = response.body()?.hasil
                if (hasil == "success") {
                    startActivity(Intent(this@MenuActivity, ResultActivity::class.java))
//                    timer.purge()
                    finish()
                } else {
                    Toast.makeText(this@MenuActivity, "" + "Pengiriman gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
//                pd1.dismiss()
//                Log.d("infoRespon",""+t.message)
                pbMenuSoal.visibility = View.GONE
                Toast.makeText(this@MenuActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun kirimJawabanFinalWaktuHabis() {

        pbMenuSoal.visibility = View.VISIBLE

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
                    pbMenuSoal.visibility = View.GONE
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
                val index = listSoal2.get(i).noSoal
                if (listSoal2.get(i).finalAnswer == null) {
                    pbMenuSoal.visibility = View.GONE
                    jawaban = "none"

                } else {
                    jawaban = listSoal2.get(i).finalAnswer.toString()
                }
                //                listSoalTerjawab.add(i, soal);
                listJawaban.add(i, jawaban)

                index.let { listIndekSoal.add(it) }
            }
        }


        val service = retrofit.create(ServiceClient::class.java)
        val sendAnswer = service.sendAnswer(
                "ujian",
                "" + urlSheet,
                "insert",
                "jawaban",
                nis,
                listIndekSoal,
                listJawaban,
                "" + listIndekSoal.size)

        sendAnswer.enqueue(object : Callback<ResponServer> {
            override fun onResponse(call: Call<ResponServer>, response: retrofit2.Response<ResponServer>) {
//                pd1.dismiss()
                pbMenuSoal.visibility = View.GONE
                val hasil = response.body()?.hasil
                if (hasil == "success") {
                    startActivity(Intent(this@MenuActivity, ResultActivity::class.java))
//                    timer.purge()
                    finish()
                } else {
                    Toast.makeText(this@MenuActivity, "" + "Pengiriman gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponServer>, t: Throwable) {
//                pd1.dismiss()
//                Log.d("infoRespon",""+t.message)
                pbMenuSoal.visibility = View.GONE
                Toast.makeText(this@MenuActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun loadWaktu(){
        timer = Timer()

        val kesempatan = getSharedPreferences(Constant.TOMBOL_HOME_RECENT_HEADER, Context.MODE_PRIVATE).getInt(Constant.TOMBOL_HOME_BUTTON, 0)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                //proses menghitung mundur ini
                //kita lakukan di background agar tidak menganggu main Thread
                runOnUiThread {
                    tvCountDownTimer.text = "Sisa waktu : ${com.smkn9.semarang.sip_smk9.activities.MenuActivity.Companion.hour} : ${com.smkn9.semarang.sip_smk9.activities.MenuActivity.Companion.minute} : ${com.smkn9.semarang.sip_smk9.activities.MenuActivity.Companion.second}"

                    //memberi event jika waktu habis
                    //mulai dari jam dulu
                    if (hour == 0 && minute == 0 && second == 0) {
                        Toast.makeText(this@MenuActivity,
                                "Maaf waktu anda habis",
                                Toast.LENGTH_SHORT).show()
//                        timer.cancel()
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
                        1 -> second = second - 1 + 1
                        2 -> second = second - 1 + 1
                    }
//                    second--
                }
            }
        }, 0, 1000)
    }

    fun onRefreshNoSoal(view: View) {
        noSoalAdapter.notifyDataSetChanged()
    }


}
