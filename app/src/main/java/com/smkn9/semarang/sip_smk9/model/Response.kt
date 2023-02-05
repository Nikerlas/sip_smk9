package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName

data class Response (
    @SerializedName("soal")
    var listSoal: List<SoalItem>? = null

    )