package com.smkn9.semarang.sip_smk9.activities.pinjamanperpus

import com.google.gson.annotations.SerializedName

data class Hasil(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)