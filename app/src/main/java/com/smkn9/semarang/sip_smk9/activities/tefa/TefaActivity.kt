@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.tefa

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.helper.Tanggal
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_tefa.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TefaActivity : AppCompatActivity() {
    lateinit var service :ServiceClient
    lateinit var pd :ProgressDialog
    lateinit var urlTefa:String
    lateinit var tanggal:String
    lateinit var bulan:String
    lateinit var nama:String
    lateinit var nis:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tefa)
        service = ServiceNetwork.getService(parent_tefa)
        pd = ProgressDialog(this)
        pd.setMessage("Mengirim pesanan ke Server ... ")
        pd.setCancelable(false)


        urlTefa = Siswa.getPesananTefa(parent_tefa)
        tanggal = Tanggal.getTanggal().toString()
        bulan = Tanggal.getBulan()
        nama = Siswa.getNamaSiswa(parent_tefa)
        nis = Siswa.getNIS(parent_tefa)

        initUi()

        cv_service_motor_menu.setOnClickListener {
            cv_service_motor.visibility = View.VISIBLE
            cv_service_mobil.visibility = View.GONE
            cv_mencari_motor.visibility = View.GONE
            cv_mencari_mobil.visibility = View.GONE
        }

        cv_service_mobil_menu.setOnClickListener {
            cv_service_motor.visibility = View.GONE
            cv_service_mobil.visibility = View.VISIBLE
            cv_mencari_motor.visibility = View.GONE
            cv_mencari_mobil.visibility = View.GONE
        }

        cv_mencari_motor_menu.setOnClickListener {
            cv_service_motor.visibility = View.GONE
            cv_service_mobil.visibility = View.GONE
            cv_mencari_motor.visibility = View.VISIBLE
            cv_mencari_mobil.visibility = View.GONE
        }

        cv_mencari_mobil_menu.setOnClickListener {
            cv_service_motor.visibility = View.GONE
            cv_service_mobil.visibility = View.GONE
            cv_mencari_motor.visibility = View.GONE
            cv_mencari_mobil.visibility = View.VISIBLE
        }


        btn_pesan_service_motor.setOnClickListener {
            if(!validationServiceMotor()){
                return@setOnClickListener
            }


            serviceMotorMobil(
                    "Service Motor",
                    ""+et_hp_service_motor.text.toString(),
                    ""+et_catatan_service_motor.text.toString(),
                    ""+et_alamat_service_motor.text.toString()
            )

        }

        btn_pesan_service_mobil.setOnClickListener {
            if(!validationServiceMobil()){
                return@setOnClickListener
            }

            serviceMotorMobil(
                    "Service Mobil",
                    ""+et_hp_service_mobil.text.toString(),
                    ""+et_catatan_service_mobil.text.toString(),
                    ""+et_alamat_service_mobil.text.toString()
            )

        }

        btn_pesan_mencari_motor.setOnClickListener {
            if(!validationCariMotor()){
                return@setOnClickListener
            }

            cariMotorMobil(
                    "Jasa Pencarian Motor",
                    ""+et_hp_mencari_motor.text.toString(),
                    ""+et_catatan_mencari_motor.text.toString(),
                    ""+et_alamat_mencari_motor.text.toString(),
                    ""+et_dana_mencari_motor.text.toString()
            )
        }

        btn_pesan_mencari_mobil.setOnClickListener {
            if(!validationCariMobil()){
                return@setOnClickListener
            }

            cariMotorMobil(
                    "Jasa Pencarian Mobil",
                    ""+et_hp_mencari_mobil.text.toString(),
                    ""+et_catatan_mencari_mobil.text.toString(),
                    ""+et_alamat_mencari_mobil.text.toString(),
                    ""+et_dana_mencari_mobil.text.toString()
            )
        }

    }

    private fun initUi(){
        tv_nis_service_motor.text = nis
        tv_nis_service_mobil.text = nis
        tv_nis_mencari_motor.text = nis
        tv_nis_mencari_mobil.text = nis


        tv_nama_service_motor.text = nama
        tv_nama_service_mobil.text = nama
        tv_nama_mencari_motor.text = nama
        tv_nama_mencari_mobil.text = nama

    }

    private fun serviceMotorMobil(jenisPesanan:String,noHp:String,catatan:String,alamat:String){
        pd.show()
        val serviceMotorMobil = service.serviceMotorMobil(
                ""+urlTefa,
                "serviceKendaraan",
                ""+tanggal,
                ""+bulan,
                ""+nama,
                ""+nis,
                ""+jenisPesanan,
                ""+noHp,
                ""+catatan,
                ""+alamat
        )

        serviceMotorMobil.enqueue(object : Callback<ResponsePesanTefa> {
            override fun onFailure(call: Call<ResponsePesanTefa>, t: Throwable) {
                pd.dismiss()
                Log.d("sekolahane", "url : "+urlTefa+" nis : "+nis+" nama : "+nama+" tgl : "+tanggal+" bulan : "+bulan+" jenisPesanan : "+jenisPesanan+" pesan : "+t.message)
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponsePesanTefa>, response: Response<ResponsePesanTefa>) {
                pd.dismiss()
                Log.d("sekolahane", "url : "+urlTefa+" nis : "+nis+" nama : "+nama+" tgl : "+tanggal+" bulan : "+bulan+"jenisPesanan : "+jenisPesanan+" body : "+response.body())
                val hasil = response.body()?.hasil
                var pesan = ""
                if(hasil == "sukses"){
                    pesan = "Pesanan Anda telah diterima Teaching Factory Sekolah. Mohon ditunggu konfirmasi dari team kami."
                }else{
                    pesan = "Pesanan Anda gagal diterima Teaching Factory Sekolah. Mohon diulangi kembali pemesanannya."
                }

                alert {
                    title = "Konfirmasi"
                    message = pesan
                    okButton {
                        finish()
                    }
                }.show()
            }

        })
    }

    private fun cariMotorMobil(jenisPesanan:String,noHp:String,catatan:String,alamat:String,dana:String){
        pd.show()
        val serviceMotorMobil = service.cariMotorMobil(
                ""+urlTefa,
                "cariKendaraan",
                ""+tanggal,
                ""+bulan,
                ""+nama,
                ""+nis,
                ""+jenisPesanan,
                ""+noHp,
                ""+catatan,
                ""+alamat,
                ""+dana
        )

        serviceMotorMobil.enqueue(object : Callback<ResponsePesanTefa> {
            override fun onFailure(call: Call<ResponsePesanTefa>, t: Throwable) {
                pd.dismiss()
                toast(""+t.message)
            }

            override fun onResponse(call: Call<ResponsePesanTefa>, response: Response<ResponsePesanTefa>) {
                pd.dismiss()
                val hasil = response.body()?.hasil
                var pesan = ""
                if(hasil == "sukses"){
                    pesan = "Pesanan Anda telah diterima Teaching Factory Sekolah. Mohon ditunggu konfirmasi dari team kami."
                }else{
                    pesan = "Pesanan Anda gagal diterima Teaching Factory Sekolah. Mohon diulangi kembali pemesanannya."
                }

                alert {
                    title = "Konfirmasi"
                    message = pesan
                    okButton {
                        finish()
                    }
                }.show()
            }

        })
    }

    private fun validationServiceMotor():Boolean{
        if(et_hp_service_motor.text.toString()==""){
            toast("No HP tidak boleh kosong")
            return false
        }

        if(et_alamat_service_motor.text.toString()==""){
            toast("Alamat tidak boleh kosong")
            return false
        }

        if(et_catatan_service_motor.text.toString()==""){
            toast("Catatan untuk Mekanik tidak boleh kosong")
            return false
        }
        return true
    }

    private fun validationServiceMobil():Boolean{
        if(et_hp_service_mobil.text.toString()==""){
            toast("No HP tidak boleh kosong")
            return false
        }

        if(et_alamat_service_mobil.text.toString()==""){
            toast("Alamat tidak boleh kosong")
            return false
        }

        if(et_catatan_service_mobil.text.toString()==""){
            toast("Catatan untuk Mekanik tidak boleh kosong")
            return false
        }
        return true
    }

    private fun validationCariMotor():Boolean{
        if(et_hp_mencari_motor.text.toString()==""){
            toast("No HP tidak boleh kosong")
            return false
        }

        if(et_alamat_mencari_motor.text.toString()==""){
            toast("Alamat tidak boleh kosong")
            return false
        }

        if(et_catatan_mencari_motor.text.toString()==""){
            toast("Catatan untuk Mekanik tidak boleh kosong")
            return false
        }

        if(et_dana_mencari_motor.text.toString()==""){
            toast("Besaran dana untuk mencari kendaraan tidak boleh kosong")
            return false
        }
        return true
    }

    private fun validationCariMobil():Boolean{
        if(et_hp_mencari_mobil.text.toString()==""){
            toast("No HP tidak boleh kosong")
            return false
        }

        if(et_alamat_mencari_mobil.text.toString()==""){
            toast("Alamat tidak boleh kosong")
            return false
        }

        if(et_catatan_mencari_mobil.text.toString()==""){
            toast("Catatan untuk Mekanik tidak boleh kosong")
            return false
        }

        if(et_dana_mencari_mobil.text.toString()==""){
            toast("Besaran dana untuk mencari kendaraan tidak boleh kosong")
            return false
        }
        return true
    }


}