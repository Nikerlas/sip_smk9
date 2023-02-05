package com.smkn9.semarang.sip_smk9.activities.loginsim

import com.google.gson.annotations.SerializedName

data class ResponseLoginSiswa(


        @SerializedName("hasil")
        var hasil: String? = null,

        @SerializedName("nama_siswa")
        var namaSiswa: String? = null,

        @SerializedName("kelas_siswa")
        var kelasSiswa: String? = null,

        @SerializedName("server_siswa")
        var serverSiswa: String? = null,

        @SerializedName("link_presensi")
        var linkPresensi: String? = null,

        @SerializedName("link_info")
        var linkInfo: String? = null,

        @SerializedName("link_tagihan")
        var linkTagihan: String? = null,

        @SerializedName("link_raport")
        var linkRaport: String? = null,

        @SerializedName("link_pesanan_tefa")
        var linkPesananTefa: String? = null,

        @SerializedName("link_pengumuman_kelulusan")
        var linkKelulusan: String? = null,

        @SerializedName("link_pinjaman_perpus")
        var linkPinjamanPerpus: String? = null,

        @SerializedName("link_server_siswa")
        var linkDbServerSiswa: String? = null,

        @SerializedName("link_ijin_siswa")
        var linkIjinSiswa: String? = null
)