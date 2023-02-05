package com.smkn9.semarang.sip_smk9.model


import com.google.gson.annotations.SerializedName


data class ResponseDaftarNilai(

	@field:SerializedName("nilai")
	val nilai: List<NilaiItem?>? = null
)