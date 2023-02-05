package com.smkn9.semarang.sip_smk9.activities.tagihan

import com.google.gson.annotations.SerializedName

data class TagLain(

	@field:SerializedName("ebta")
	val ebta: Int? = null,

	@field:SerializedName("tgl_byr_bakti_lulus")
	val tglByrBaktiLulus: String? = null,

	@field:SerializedName("sem_1")
	val sem1: Int? = null,

	@field:SerializedName("sem_2")
	val sem2: Int? = null,

	@field:SerializedName("tgl_byr_uang_praktik")
	val tglByrUangPraktik: String? = null,

	@field:SerializedName("tgl_byr_pkl")
	val tglByrPkl: String? = null,

	@field:SerializedName("angs ")
	val angs: Int? = null,

	@field:SerializedName("tgl_byr_ebta")
	val tglByrEbta: String? = null,

	@field:SerializedName("pkl")
	val pkl: Int? = null,

	@field:SerializedName("bakti_lulus")
	val baktiLulus: Int? = null,

	@field:SerializedName("tgl_byr_sem_2")
	val tglByrSem2: String? = null,

	@field:SerializedName("uang_praktik")
	val uangPraktik: Int? = null,

	@field:SerializedName("tgl_byr_angs ")
	val tglByrAngs: String? = null,

	@field:SerializedName("tgl_byr_sem_1")
	val tglByrSem1: String? = null
)