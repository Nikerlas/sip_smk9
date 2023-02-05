package com.smkn9.semarang.sip_smk9.helper

import java.util.*

object Tanggal {
    fun getTanggal(): Int{

        val date = Date()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        cal.time = date
        val tanggal = cal.get(Calendar.DAY_OF_MONTH)
        return tanggal
    }

    fun getBulan():String{
        val date = Date()
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        cal.time = date
        val month = cal.get(Calendar.MONTH)
        var bulan = ""

        when (month) {
            0 -> bulan = "JANUARI"
            1 -> bulan = "FEBRUARI"
            2 -> bulan = "MARET"
            3 -> bulan = "APRIL"
            4 -> bulan = "MEI"
            5 -> bulan = "JUNI"
            6 -> bulan = "JULI"
            7 -> bulan = "AGUSTUS"
            8 -> bulan = "SEPTEMBER"
            9 -> bulan = "OKTOBER"
            10 -> bulan = "NOVEMBER"
            11 -> bulan = "DESEMBER"
        }
        return bulan
    }
}