package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName

//class ResponseInfo {}
data class ResponseInfo(

        @field:SerializedName("results")
        val results: List<Pengumuman>? = null
)