package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName


data class ResponServerToken (

    @SerializedName("hasil")
    var hasil: String? = null,

    @SerializedName("jam")
    val jam: Int = 0,

    @SerializedName("menit")
    val menit: Int = 0,

    @SerializedName("detik")
    val detik: Int = 0,

    @SerializedName("jumlah_soal")
    val jumlahSoal: Int = 0


)