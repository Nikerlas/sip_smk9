package com.smkn9.semarang.sip_smk9.activities.rekappresensi

import com.google.gson.annotations.SerializedName

data class ResponseRekapPresensi(

	@field:SerializedName("rekap")
	val rekap: String? = null
)
