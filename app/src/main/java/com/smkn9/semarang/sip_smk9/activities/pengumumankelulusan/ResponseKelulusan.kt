package com.smkn9.semarang.sip_smk9.activities.pengumumankelulusan

import com.google.gson.annotations.SerializedName

data class ResponseKelulusan(

	@field:SerializedName("hasil")
	val hasil: Hasil? = null
)