package com.smkn9.semarang.sip_smk9.activities.homepresensiwajah

import android.content.DialogInterface
import androidx.cardview.widget.CardView
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import com.google.gson.Gson
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import android.os.Bundle
import com.smkn9.semarang.sip_smk9.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.smkn9.semarang.sip_smk9.activities.MainActivity
import com.smkn9.semarang.sip_smk9.activities.registerwajah.RegisterActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_home_presensi_wajah.*
import org.jetbrains.anko.intentFor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePresensiWajahActivity : AppCompatActivity() {
    lateinit var cardRegister: CardView
    lateinit var cardPresensi: CardView
    var cardHelp: CardView? = null
    var cardSinkronisasi: CardView? = null
    var retrofit: Retrofit? = null
    var okHttpClient: OkHttpClient? = null
    var gson: Gson? = null
    var server: String? = null
    lateinit var service: ServiceClient
    lateinit var pbLoading: ProgressBar
    lateinit var urlPresensiSiswa: String
    lateinit var nis: String
    lateinit var nama: String
    lateinit var jenisPresensi: String
    lateinit var lokasiPresensi:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_presensi_wajah)
        cardRegister = findViewById(R.id.card_register)
        cardPresensi = findViewById(R.id.card_presensi)
        cardHelp = findViewById(R.id.card_helpdesk)
        cardSinkronisasi = findViewById(R.id.card_sinkronisasi)
        pbLoading = findViewById(R.id.pb_loading_home)

        urlPresensiSiswa = Siswa.getLinkJurnalKelas(parent_home_presensi_wajah)
        nama = Siswa.getNamaSiswa(parent_home_presensi_wajah)
        nis = Siswa.getNIS(parent_home_presensi_wajah)
        jenisPresensi = intent.getStringExtra(Constant.BUNDLE_DETAIL).toString()
        lokasiPresensi = intent.getStringExtra(Constant.SISWA_LOKASI_PRESENSI).toString()
        service = ServiceNetwork.getService(parent_home_presensi_wajah)

        cardRegister.setOnClickListener(View.OnClickListener { view ->
            val builder = AlertDialog.Builder(view.context)
            val v = LayoutInflater.from(view.context)
                .inflate(R.layout.dialog_social, view.rootView as ViewGroup, false)
            val username = v.findViewById<EditText>(R.id.et_username_register)
            val password = v.findViewById<EditText>(R.id.et_pass_register)
            builder.setView(v)
            builder.setPositiveButton(
                "Login",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    if (username.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@HomePresensiWajahActivity,
                            "Username atau pass tidak boleh kosong",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    val user = username.text.toString().toLowerCase().trim { it <= ' ' }
                    val pass = password.text.toString().toLowerCase().trim { it <= ' ' }
                    pbLoading.setVisibility(View.VISIBLE)

                    val loginAdminRegisterWajah = service.loginAdmin(
                        ""+urlPresensiSiswa,
                        "login",
                        ""+user,
                        ""+pass
                    )

                    loginAdminRegisterWajah.enqueue(object : Callback<ResponseLoginAdmin> {
                        override fun onResponse(
                            call: Call<ResponseLoginAdmin>,
                            response: Response<ResponseLoginAdmin>
                        ) {
                            pbLoading.setVisibility(View.GONE);
                            dialogInterface.dismiss();
                            val hasil = response.body()?.getHasil();
                            if(hasil.equals("sukses")){
                                startActivity(intentFor<RegisterActivity>());

                            }else{
                                Toast.makeText(this@HomePresensiWajahActivity, "Username atau password salah", Toast.LENGTH_SHORT).show();
                            }
                        }

                        override fun onFailure(call: Call<ResponseLoginAdmin>, t: Throwable) {
                            pbLoading.setVisibility(View.GONE);
                            dialogInterface.dismiss();
                            Toast.makeText(this@HomePresensiWajahActivity, ""+t.message, Toast.LENGTH_SHORT).show();
                        }

                    })

                })
                .setNegativeButton("Batal") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()


        })


        cardPresensi.setOnClickListener(View.OnClickListener {
                            startActivity(intentFor<MainActivity>(
                                Constant.BUNDLE_DETAIL to jenisPresensi,
                                Constant.SISWA_LOKASI_PRESENSI to lokasiPresensi
                            ))
//                startActivity(new Intent(HomePresensiWajahActivity.this, InputKodePresensiActivity.class));
//                startActivity(new Intent(HomePresensiWajahActivity.this, CekMockActivity.class));
        })
//        cardHelp.setOnClickListener(View.OnClickListener {
//            Toast.makeText(
//                this@HomePresensiWajahActivity,
//                "Maaf fitur ini sedang maintenance",
//                Toast.LENGTH_SHORT
//            ).show()
//        })
//        cardSinkronisasi.setOnClickListener(View.OnClickListener {
//            Toast.makeText(
//                this@HomePresensiWajahActivity,
//                "Maaf fitur ini sedang maintenance",
//                Toast.LENGTH_SHORT
//            ).show()
//        })
    }

//    override fun onRestart() {
//        super.onRestart()
//        val serverPeg = getSharedPreferences("headId", MODE_PRIVATE).getString("serverPegawai", "")
//        val sp = getSharedPreferences("headId", MODE_PRIVATE)
//        val idPeg = sp.getString("idPeg", "")
//        if (serverPeg == "") {
//            loadDataServer(idPeg)
//        }
//    }
//
//    private fun loadDataServer(idPegawai: String?) {
//        pbLoading!!.visibility = View.VISIBLE
//
////        Call<com.alifproduction.facerecognition.home.ResponseLoadServer> getServer = service.getServer(
////                "dataPegawai",
////                "" +idPegawai
////        );
////
////        getServer.enqueue(new Callback<com.alifproduction.facerecognition.home.ResponseLoadServer>() {
////            @Override
////            public void onResponse(Call<com.alifproduction.facerecognition.home.ResponseLoadServer> call, Response<com.alifproduction.facerecognition.home.ResponseLoadServer> response) {
////                pbLoading.setVisibility(View.GONE);
////                String status = response.body().getStatus();
////                if(status.equals("sukses")){
////                    String server = response.body().getLink();
////                    getSharedPreferences("headId", MODE_PRIVATE).edit().putString("serverPegawai",server).apply();
////                    Toast.makeText(HomePresensiWajahActivity.this, "Server individu berhasil di load", Toast.LENGTH_SHORT).show();
////
////                }else{
////                    Toast.makeText(HomePresensiWajahActivity.this, "Ada masalah di data Anda, silakan kontak Admin", Toast.LENGTH_SHORT).show();
////                }
////            }
////
////            @Override
////            public void onFailure(Call<com.alifproduction.facerecognition.home.ResponseLoadServer> call, Throwable t) {
////                pbLoading.setVisibility(View.GONE);
////                Toast.makeText(HomePresensiWajahActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
////            }
////        });
//    }
}