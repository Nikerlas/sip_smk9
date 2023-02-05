package com.smkn9.semarang.sip_smk9.network

import com.alifproduction.skansa.saygolearn.activities.pengumumankelulusan.ResponseStatusPengumuman
import com.smkn9.semarang.sip_smk9.activities.essay.ResponseReadEssay
import com.smkn9.semarang.sip_smk9.activities.gantipass.ResponseGantiPass
import com.smkn9.semarang.sip_smk9.activities.home.ResponseGetJurnalKelas
import com.smkn9.semarang.sip_smk9.activities.home.ResponseRegisterDevice
import com.smkn9.semarang.sip_smk9.activities.home.ResponseStatusRegistrasi
import com.smkn9.semarang.sip_smk9.activities.homepresensiwajah.ResponseLoginAdmin
import com.smkn9.semarang.sip_smk9.activities.loginsim.ResponseLoginSiswa
import com.smkn9.semarang.sip_smk9.activities.pengumumankelulusan.ResponseKelulusan
import com.smkn9.semarang.sip_smk9.activities.pinjamanperpus.ResponsePinjamanPerpus
import com.smkn9.semarang.sip_smk9.activities.presensi.ResponseInputPresensiHadirPulang
import com.smkn9.semarang.sip_smk9.activities.presensi.ResponseReadLokasiAcuan
import com.smkn9.semarang.sip_smk9.activities.presensi.ResponseReadStatusPresensi
import com.smkn9.semarang.sip_smk9.activities.presensimapel.ResponseInputPresensiMapel
import com.smkn9.semarang.sip_smk9.activities.raport.ResponsePengumumanRaport
import com.smkn9.semarang.sip_smk9.activities.raport.ResponseReadStatusPengumumanRaport
import com.smkn9.semarang.sip_smk9.activities.rekappresensi.ResponseRekapPresensiGukar
import com.smkn9.semarang.sip_smk9.activities.tagihan.ResponseTagihan
import com.smkn9.semarang.sip_smk9.activities.tefa.ResponsePesanTefa
import com.smkn9.semarang.sip_smk9.model.*


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by pertambangan on 17/02/18.
 */

interface ServiceClient {

    //actionFirst=cekKodeSoal&sheetNameClass=kelas_x&kodeGuru=WD10
    //ini untuk mendapatkan soal sesuai Guru
    @POST("exec")
    fun getSoalDariGuru(
            @Query("actionFirst") actionFirst: String,
            @Query("sheetNameClass") sheetNameClass: String,
            @Query("kodeGuru") kodeGuru: String,
            @Query("nis") nis: String): Call<ResponServerKodeGuru>


    //login admin register wajah
    @POST("exec")
    fun loginAdmin(
        @Query("url") urlKelas: String,
        @Query("action") action: String,
        @Query("username") username: String,
        @Query("pass") pass: String): Call<ResponseLoginAdmin>

    //login sim
    @POST("exec")
    @FormUrlEncoded
    fun getLogin(
            @Field("sheetName", encoded = true) sheetName :String,
            @Field("nis", encoded = true) nis :String,
            @Field("action", encoded = true) action :String,
            @Field("pass", encoded = true) pass :String,
            @Field("tingkatan", encoded = true) tingkatan :String
    ):Call<ResponseLoginSiswa>

    //ini untuk melakukan login
    @POST("exec")
    fun login(
            @Query("actionFirst") actionFirst: String,
            @Query("urlSheet") urlSheet: String,
            @Query("action") action: String,
            @Query("sheetName") sheetName: String,
            @Query("nis") nis: String,
            @Query("pass") pass: String): Call<ResponServer>

    @FormUrlEncoded
    @POST("exec")
    fun registerDevice(
            @Field(encoded = true,value = "url") url: String,
            @Field(encoded = true,value = "action") action: String,
            @Field(encoded = true,value = "nis") nis: String,
            @Field(encoded = true,value = "androidId") androidId: String): Call<ResponseRegisterDevice>


    @POST("exec")
    @FormUrlEncoded
    fun sendPresensiHadirPulang(
            @Field("url", encoded = true) url:String,
            @Field("action", encoded = true) action:String,
            @Field("tanggal", encoded = true) tanggal: String,
            @Field("bulan", encoded = true) bulan:String,
            @Field("nama", encoded = true) nama:String,
            @Field("nis", encoded = true) nis:String,
            @Field("androidId", encoded = true) androidId:String,
            @Field("jenisPresensi", encoded = true) jenisPresensi:String,
            @Field("lokasiPresensi", encoded = true) LokasiPresensi:String,
            @Field("noWa", encoded = true) noWa:String
    ):Call<ResponseInputPresensiHadirPulang>

    @POST("exec")
    @FormUrlEncoded
    fun sendPresensiMapel(
            @Field("url", encoded = true) url:String,
            @Field("action", encoded = true) action:String,
            @Field("tanggal", encoded = true) tanggal: String,
            @Field("bulan", encoded = true) bulan:String,
            @Field("nama", encoded = true) nama:String,
            @Field("nis", encoded = true) nis:String,
            @Field("androidId", encoded = true) androidId:String,
            @Field("jamAwal", encoded = true) jamAwal:String,
            @Field("durasi", encoded = true) durasi:String,
            @Field("mapel", encoded = true) mapel:String,
            @Field("kegiatan", encoded = true) kegiatan:String
    ):Call<ResponseInputPresensiMapel>

    @POST("exec")
    @FormUrlEncoded
    fun serviceMotorMobil(
            @Field("url", encoded = true) url:String,
            @Field("action", encoded = true) action:String,
            @Field("tanggal", encoded = true) tanggal: String,
            @Field("bulan", encoded = true) bulan:String,
            @Field("nama", encoded = true) nama:String,
            @Field("nis", encoded = true) nis:String,
            @Field("jenisPesanan", encoded = true) jenisPesanan:String,
            @Field("noHp", encoded = true) noHp:String,
            @Field("catatan", encoded = true) catatan:String,
            @Field("alamat", encoded = true) alamat:String
    ):Call<ResponsePesanTefa>

    @POST("exec")
    @FormUrlEncoded
    fun cariMotorMobil(
            @Field("url", encoded = true) url:String,
            @Field("action", encoded = true) action:String,
            @Field("tanggal", encoded = true) tanggal: String,
            @Field("bulan", encoded = true) bulan:String,
            @Field("nama", encoded = true) nama:String,
            @Field("nis", encoded = true) nis:String,
            @Field("jenisPesanan", encoded = true) jenisPesanan:String,
            @Field("noHp", encoded = true) noHp:String,
            @Field("catatan", encoded = true) catatan:String,
            @Field("alamat", encoded = true) alamat:String,
            @Field("dana", encoded = true) dana:String
    ):Call<ResponsePesanTefa>

    @POST("exec")
    @FormUrlEncoded
    fun updatePass(
            @Field("url", encoded = true) url:String,
            @Field("action", encoded = true) action:String,
            @Field("nis", encoded = true) nis:String,
            @Field("tingkatan", encoded = true) tingkatan:String,
            @Field("kelas", encoded = true) kelas:String,
            @Field("passLama", encoded = true) passLama:String,
            @Field("passBaru", encoded = true) passBaru:String
    ):Call<ResponseGantiPass>


    @GET("exec")
    fun getLokasiAcuan(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("nis") nis:String,
            @Query("posisi") lokasi:String
    ):Call<ResponseReadLokasiAcuan>

    @GET("exec")
    fun getStatusPresensiSiswa(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("tanggal") tanggal:String,
            @Query("bulan") bulan:String,
            @Query("nis") nis:String,
            @Query("jenisPresensi") jenisPresemdi:String
    ):Call<ResponseReadStatusPresensi>

    @GET("exec")
    fun getRekapPresensiGuru(
        @Query("url") url:String,
        @Query("action") action:String,
        @Query("bulan") bulan:String,
        @Query("kode") kodeGuru:String
    ):Call<ResponseRekapPresensiGukar>

    @GET("exec")
    fun getInfoTagihan(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("tingkatan") tingkatan:String,
            @Query("kelas") kelas:String,
            @Query("nis") nis:String
    ):Call<ResponseTagihan>

    @GET("exec")
    fun getInfoPinjamanPerpus(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("tingkatan") tingkatan:String,
            @Query("kelas") kelas:String,
            @Query("nis") nis:String
    ):Call<ResponsePinjamanPerpus>

    @GET("exec")
    fun getDaftarNilai(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("tingkatan") tingkatan:String,
            @Query("kelas") kelas:String,
            @Query("nis") nis:String
    ):Call<ResponseDaftarNilai>

    @GET("exec")
    fun getInfo(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("sheetName") sheetName:String
    ):Call<ResponseInfo>


    @GET("exec")
    fun getPresensi(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("tingkatan") tingkatan:String,
            @Query("kelas") kelas:String,
            @Query("bulan") bulan:String,
            @Query("nis") nis:String
    ):Call<ResponsePresensiBulanan>


    @GET("exec")
    fun getJurnalKelas(
            @Query("url") url:String,
            @Query("action") action:String,
            @Query("tingkatan") tingkatan:String,
            @Query("kelas") kelas:String
    ):Call<ResponseGetJurnalKelas>



    @GET("exec")
    fun cekStatusRegister(
            @Query("url") url: String,
            @Query("action") action: String,
            @Query("nis") nis: String): Call<ResponseStatusRegistrasi>


    @GET("exec")
    fun getInfoKelulusan(
            @Query("url") url: String,
            @Query("action") action: String,
            @Query("nis") nis: String
    ): Call<ResponseKelulusan>

    @GET("exec")
    fun getStatusPengumuman(
            @Query("url") url: String,
            @Query("action") action: String
    ): Call<ResponseStatusPengumuman>

    @GET("exec")
    fun getStatusPengumumanRaport(
            @Query("url") url: String,
            @Query("action") action: String,
            @Query("tingkatan") tingkatan: String,
            @Query("nis") nis: String
    ): Call<ResponseReadStatusPengumumanRaport>

    @GET("exec")
    fun getPengumumanRaport(
            @Query("url") url: String,
            @Query("action") action: String,
            @Query("tingkatan") tingkatan: String,
            @Query("nis") nis: String
    ): Call<ResponsePengumumanRaport>

    //ini untuk mendapatkan soal
    @GET("exec")
    fun getListSoal(
            @Query("urlSheet") urlSheet: String,
            @Query("action") action: String,
            @Query("sheetName") sheetName: String): Call<Response>

    //ini untuk mendapatkan soal essay
    @GET("exec")
    fun getListSoalEssay(
            @Query("urlSheet") urlSheet: String,
            @Query("action") action: String,
            @Query("sheetName") sheetName: String): Call<ResponseReadEssay>

    //ini untuk mendapatkan hasil jawaban
    @GET("exec")
    fun getResultExam(
            @Query("urlSheet") urlSheet: String,
            @Query("action") action: String,
            @Query("sheetName") sheetName: String,
            @Query("nis") nis: String): Call<ResponServer>


    //ini untuk mengirim sms ke hp orang tua
    @GET("exec")
    fun sendSMS(
            @Query("urlSheet") urlSheet: String,
            @Query("action") action: String,
            @Query("sheetName") sheetName: String,
            @Query("nilai") nilai: String,
            @Query("mapel") mapel: String,
            @Query("noHp") noHp: String,
            @Query("nama") nama: String,
            @Query("ujian") ujian: String): Call<ResponServer>

    //ini untuk mengirim jawaban
    @FormUrlEncoded
    @POST("exec")
    fun sendAnswer(
            @Field("actionFirst") actionFirst: String,
            @Field("urlSheet") urlSheet: String,
            @Field(value = "action", encoded = true) action: String,
            @Field(value = "sheetName", encoded = true) sheetName: String,
            @Field(value = "nis", encoded = true) nis: String,
            @Field(value = "soal", encoded = true) listSoal: List<Int>,
            @Field(value = "jawaban", encoded = true) listJawaban: List<String>,
            @Field(value = "jumlahSoal", encoded = true) jumlahSoal: String): Call<ResponServer>


    //ini untuk pengecekan token
    @POST("exec")
    fun token(
            @Query("actionFirst") actionFirst: String,
            @Query("urlSheet") urlSheet: String,
            @Query("action") action: String,
            @Query("sheetName") sheetName: String,
            @Query("nis") nis: String,
            @Query("token") token: String): Call<ResponServerToken>

    //ini untuk mendapatkan info Soal
    @GET("exec")
    fun getInfoSoal(
            @Query("action") action: String,
            @Query("urlSheet") urlSheet: String,
            @Query("sheetName") sheetName: String): Call<ResponseInfoSoal>


    //ini untuk mendapatkan info sekolah
    //    @GET("exec")
    //    Call<ResponInfoSekolah> getInfoSekolah(@Query("action") String action,
    //                                          @Query("sheetName") String sheetName);
}
