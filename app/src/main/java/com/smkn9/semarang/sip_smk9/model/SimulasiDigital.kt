package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName


data class SimulasiDigital(

	@field:SerializedName("PAS Gasal")
	val pASGasal: String? = null,

	@field:SerializedName("PTS Genap")
	val pTSGenap: String? = null,

	@field:SerializedName("PAS Genap")
	val pASGenap: String? = null,

	@field:SerializedName("PTS Gasal")
	val pTSGasal: String? = null
)