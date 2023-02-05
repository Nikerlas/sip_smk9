package com.smkn9.semarang.sip_smk9.helper

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.view.View

object Siswa {




    fun getSp(view:View):SharedPreferences{
        return view.context.getSharedPreferences(Constant.MASTER_SISWA_HEADER, Context.MODE_PRIVATE)

    }

    fun getStatusLogin(view: View):Boolean{
        return getSp(view).getBoolean(Constant.SISWA_LOGIN,false)
    }

    fun getNamaSiswa(view: View):String{
        return getSp(view).getString(Constant.SISWA_NAMA,"").toString()
    }

    fun getNIS(view: View):String{
        return getSp(view).getString(Constant.SISWA_NIS,"").toString()
    }

    fun getTingkatan(view: View):String{
        return getSp(view).getString(Constant.SISWA_TINGKATAN,"").toString()
    }

    fun getKelasSiswa(view: View):String{
        return getSp(view).getString(Constant.SISWA_KELAS,"").toString()
    }

    fun getDbServerSiswa(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_DB_SERVER_SISWA,"").toString()
    }

    fun getServerSimSiswa(view: View):String{
        return getSp(view).getString(Constant.SISWA_SERVER,"").toString()
    }

    fun getLinkTagihan(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_TAGIHAN,"").toString()
    }

    fun getLinkInfo(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_INFO_SISWA,"").toString()
    }

    fun getLinkKelulusan(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_PENGUMUMAN_KELULUSAN_SISWA,"").toString()
    }

    fun getLinkPinjamanPerpus(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_PEMINJAMAN_PERPUS,"").toString()
    }

    fun getLinkPresensi(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_PRESENSI_SISWA,"").toString()
    }

    fun getLinkIjinSiswa(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_IJIN_SISWA,"").toString()
    }

    fun getLinkRaport(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_RAPORT_SISWA,"").toString()
    }

    fun getLinkJurnalKelas(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_JURNAL_KELAS,"").toString()
    }

    fun getAndroidId(view: View):String{
        return Settings.Secure.getString(view.context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getPesananTefa(view: View):String{
        return getSp(view).getString(Constant.SISWA_LINK_PESANAN_TEFA_SISWA,"").toString()
    }
}