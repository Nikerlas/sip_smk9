package com.smkn9.semarang.sip_smk9.activities.raport

import com.google.gson.annotations.SerializedName

data class Hasil(

	@field:SerializedName("administrasi")
	val administrasi: String? = null,

	@field:SerializedName("link_raport")
	val linkRaport: String? = null,

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("pengumuman")
	val pengumuman: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)