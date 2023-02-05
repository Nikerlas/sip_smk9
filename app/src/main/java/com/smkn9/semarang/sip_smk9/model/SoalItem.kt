package com.smkn9.semarang.sip_smk9.model

import com.google.gson.annotations.SerializedName


class SoalItem {

    @SerializedName("a")
    val answerA: String? = null

    @SerializedName("soal")
    val soal: String? = null

    @SerializedName("b")
    val answerB: String? = null

    @SerializedName("c")
    val answerC: String? = null

    @SerializedName("d")
    val answerD: String? = null

    @SerializedName("e")
    val answerE: String? = null

    val answerN = "none"

    @SerializedName("idGambar")
    val idGambar: String? = null

    @SerializedName("no_soal")
    val noSoal: Int = 0

    @SerializedName("idVideo")
    val idVideo: String? = null

    @SerializedName("a_gambar")
    private val aGambar: String? = null

    @SerializedName("b_gambar")
    private val bGambar: String? = null

    @SerializedName("c_gambar")
    private val cGambar: String? = null

    @SerializedName("d_gambar")
    private val dGambar: String? = null

    @SerializedName("e_gambar")
    private val eGambar: String? = null


    var selectedRadioButtonId: Int = 0
    var selectedPositionQuestion: Int = 0
    var finalAnswer: String? = null

    fun getaGambar(): String? {
        return aGambar
    }

    fun getbGambar(): String? {
        return bGambar
    }

    fun getcGambar(): String? {
        return cGambar
    }

    fun getdGambar(): String? {
        return dGambar
    }

    fun geteGambar(): String? {
        return eGambar
    }
}