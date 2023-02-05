package com.smkn9.semarang.sip_smk9.activities.pinjamanperpus

import com.google.gson.annotations.SerializedName

data class ResponsePinjamanPerpus(

	@field:SerializedName("hasil")
	val hasil: Hasil? = null
)