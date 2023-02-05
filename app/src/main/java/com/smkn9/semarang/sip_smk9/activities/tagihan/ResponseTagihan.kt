package com.smkn9.semarang.sip_smk9.activities.tagihan

import com.google.gson.annotations.SerializedName

data class ResponseTagihan(

	@field:SerializedName("hasil")
	val hasil: String? = null,

	@field:SerializedName("tag_lain")
	val tagLain: TagLain? = null,

	@field:SerializedName("tag_spp")
	val tagSpp: TagSpp? = null,

	@field:SerializedName("info")
   val info: Info? = null
)