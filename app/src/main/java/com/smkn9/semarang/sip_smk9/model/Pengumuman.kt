package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName

//class Pengumuman {}
data class Pengumuman(

        @field:SerializedName("link_dokumen")
        val linkDokumen: String? = null,

        @field:SerializedName("tanggal")
        val tanggal: String? = null,

        @field:SerializedName("perihal")
        val perihal: String? = null
)