package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName

//class ResponsePresensiBulanan {}
data class ResponsePresensiBulanan(

        @field:SerializedName("results")
        val results: List<ResultsItem?>? = null
)