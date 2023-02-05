package com.smkn9.semarang.sip_smk9.activities.pinjamanperpus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import kotlinx.android.synthetic.main.activity_pinjaman_perpus.*

class PinjamanPerpusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinjaman_perpus)
        init()

        val catatan = intent.getStringExtra(Constant.BUNDLE_DETAIL)
        tv_pinjaman_perpus_catatan.text = catatan
    }

    fun init(){
        val nis = Siswa.getNIS(parent_pinjam_perpus)
        val nama = Siswa.getNamaSiswa(parent_pinjam_perpus)
        val kelas = Siswa.getKelasSiswa(parent_pinjam_perpus)

        tv_pinjaman_perpus_nis.text = nis
        tv_pinjaman_perpus_nama.text = nama
        tv_pinjaman_perpus_kelas.text = kelas

    }
}