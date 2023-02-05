package com.smkn9.semarang.sip_smk9.activities.presensi

import com.google.gson.annotations.SerializedName

data class ResponseReadStatusPresensi(

	@field:SerializedName("status")
	val hasil: String? = null
)
