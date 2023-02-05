package com.smkn9.semarang.sip_smk9.model


import com.google.gson.annotations.SerializedName


data class ResponServerKodeGuru(

	@field:SerializedName("server")
	val server: String? = null,

	@field:SerializedName("ujian")
	var ujian:String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("hp")
	val hp: Long? = null,

	@field:SerializedName("sms")
	val sms :String? = null,

	@field:SerializedName("hasil")
	val hasil: String? = null,

	@field:SerializedName("mapel")
	val mapel: String? = null
)