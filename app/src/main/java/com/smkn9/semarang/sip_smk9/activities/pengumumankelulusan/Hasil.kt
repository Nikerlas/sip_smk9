package com.smkn9.semarang.sip_smk9.activities.pengumumankelulusan

import com.google.gson.annotations.SerializedName

data class Hasil(

	@field:SerializedName("administrasi")
	val administrasi: String? = null,

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("link_kelulusan")
	val linkKelulusan: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)