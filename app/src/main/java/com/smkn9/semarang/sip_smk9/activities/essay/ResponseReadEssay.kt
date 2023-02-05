package com.smkn9.semarang.sip_smk9.activities.essay

import com.google.gson.annotations.SerializedName

data class ResponseReadEssay(

	@SerializedName("soal_essay")
	val soalEssay: List<SoalEssayItem?>? = null
)