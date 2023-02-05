package com.smkn9.semarang.sip_smk9.model

data class SoalItem2(

        val id:Int? = null,

        val noSoal: Int = 0,

        val soal: String? = null,

        val idGambar: String? = null,

        val idVideo: String? = null,

        val answerA: String? = null,


        val answerB: String? = null,


        val answerC: String? = null,


        val answerD: String? = null,


        val answerE: String? = null,

        val finalAnswerServer :String? = null,

        private val aGambar: String? = null,


        private val bGambar: String? = null,


        private val cGambar: String? = null,


        private val dGambar: String? = null,


        private val eGambar: String? = null
) {


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

