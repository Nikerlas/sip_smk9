package com.smkn9.semarang.sip_smk9.model


import com.google.gson.annotations.SerializedName


data class ResponseInfoSoal(

	@field:SerializedName("detik")
	val detik: Int? = null,

	@field:SerializedName("jam")
	val jam: Int? = null,

	@field:SerializedName("menit")
	val menit: Int? = null,

	@field:SerializedName("jumlah_soal")
	val jumlahSoal: Int? = null
)