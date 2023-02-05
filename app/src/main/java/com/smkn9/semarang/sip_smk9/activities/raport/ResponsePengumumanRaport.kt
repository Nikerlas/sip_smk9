package com.smkn9.semarang.sip_smk9.activities.raport

import com.google.gson.annotations.SerializedName

data class ResponsePengumumanRaport(

	@field:SerializedName("hasil")
	val hasil: Hasil? = null
)