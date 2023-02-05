package com.smkn9.semarang.sip_smk9.activities.essay

import com.google.gson.annotations.SerializedName

class SoalEssayItem {

	@SerializedName("soal")
	val soal: String? = null

	@SerializedName("soal_gambar")
	val soalGambar: String? = null

	@SerializedName("no_soal")
	val noSoal: Int? = null

	var finalAnswer: String? = null
}