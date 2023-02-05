package com.smkn9.semarang.sip_smk9.model


import com.google.gson.annotations.SerializedName


data class ResponInfoSekolah (

    @SerializedName("nama")
    var nama: String? = null,

    @SerializedName("idIcon")
    var idIcon: String? = null


)