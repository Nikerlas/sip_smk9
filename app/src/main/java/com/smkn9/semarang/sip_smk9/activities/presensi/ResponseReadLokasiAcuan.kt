package com.smkn9.semarang.sip_smk9.activities.presensi

import com.google.gson.annotations.SerializedName

data class ResponseReadLokasiAcuan(

	@field:SerializedName("longAcuan")
	val longAcuan: String? = null,

	@field:SerializedName("latAcuan")
	val latAcuan: String? = null,

	@field:SerializedName("jarakMax")
	val jarakMax: String? = null,

	@field:SerializedName("lokasi")
	val lokasi: String? = null,

	@field:SerializedName("noWa")
	val noWa: String? = null
)
