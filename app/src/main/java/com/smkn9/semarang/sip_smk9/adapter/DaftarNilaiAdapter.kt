package com.smkn9.semarang.sip_smk9.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.model.NilaiItem
import kotlinx.android.synthetic.main.item_daftar_nilai.view.*

//class DaftarNilaiAdapter {}
class DaftarNilaiAdapter(val nilai: NilaiItem?, val namaMapel:List<String>, val namaSiswa:String, val nis:String, val kelas:String): androidx.recyclerview.widget.RecyclerView.Adapter<DaftarNilaiAdapter.SPPViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarNilaiAdapter.SPPViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_nilai,parent,false)
        return SPPViewHolder(view)
    }

    class SPPViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        val tvNIS= view.tv_item_nilai_nis
        val tvNama = view.tv_item_nilai_nama
        val tvKelas= view.tv_item_nilai_kelas
        val tvNamaMapel = view.tv_item_nilai_nama_mapel
        val tvNilaiPTSGasal= view.tv_item_nilai_pts_gasal
        val tvNilaiPASGasal= view.tv_item_nilai_pas_gasal
        val tvNilaiPTSGenap= view.tv_item_nilai_pts_genap
        val tvNilaiPASGenap= view.tv_item_nilai_pas_genap



        fun bindString(nilai: NilaiItem?,listNamaMapel:List<String> ,namaSiswa:String, nis:String, kelas:String, position:Int ){

            tvNIS.text = nis
            tvNama.text = namaSiswa
            tvKelas.text = kelas

            when(position){
                0 -> {
                    tvNamaMapel.text = "Baca Tulis Qur'an"
                    tvNilaiPTSGasal.text = nilai?.bacaTulisQurAn?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.bacaTulisQurAn?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.bacaTulisQurAn?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.bacaTulisQurAn?.pASGenap

                }
                1 -> {
                    tvNamaMapel.text = "Bahasa Daerah"
                    tvNilaiPTSGasal.text = nilai?.bhsDaerah?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.bhsDaerah?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.bhsDaerah?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.bhsDaerah?.pASGenap
                }
                2 -> {
                    tvNamaMapel.text = "Bahasa Indonesia"
                    tvNilaiPTSGasal.text = nilai?.bhsIndonesia?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.bhsIndonesia?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.bhsIndonesia?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.bhsIndonesia?.pASGenap
                }
                3 -> {
                    tvNamaMapel.text = "Bahasa Inggris"
                    tvNilaiPTSGasal.text = nilai?.bhsInggris?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.bhsInggris?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.bhsInggris?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.bhsInggris?.pASGenap
                }
                4 -> {
                    tvNamaMapel.text = "Bimbingan Konseling"
                    tvNilaiPTSGasal.text = nilai?.bimbinganKonseling?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.bimbinganKonseling?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.bimbinganKonseling?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.bimbinganKonseling?.pASGenap
                }
                5 -> {
                    tvNamaMapel.text = "Ekstrakurikuler"
                    tvNilaiPTSGasal.text = nilai?.ekstrakurikuler?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.ekstrakurikuler?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.ekstrakurikuler?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.ekstrakurikuler?.pASGenap
                }
                6 -> {
                    tvNamaMapel.text = "Fisika"
                    tvNilaiPTSGasal.text = nilai?.fisika?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.fisika?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.fisika?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.fisika?.pASGenap
                }
                7 -> {
                    tvNamaMapel.text = "Hansek"
                    tvNilaiPTSGasal.text = nilai?.hansek?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.hansek?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.hansek?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.hansek?.pASGenap
                }
                8 -> {
                    tvNamaMapel.text = "Ipa Terapan"
                    tvNilaiPTSGasal.text = nilai?.ipaTerapan?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.ipaTerapan?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.ipaTerapan?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.ipaTerapan?.pASGenap
                }
                9 -> {
                    tvNamaMapel.text = "Kepariwisataan"
                    tvNilaiPTSGasal.text = nilai?.kepariwisataan?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.kepariwisataan?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.kepariwisataan?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.kepariwisataan?.pASGenap
                }
                10 -> {
                    tvNamaMapel.text ="Kimia"
                    tvNilaiPTSGasal.text = nilai?.kimia?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.kimia?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.kimia?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.kimia?.pASGenap
                }
                11 -> {
                    tvNamaMapel.text ="Matematika"
                    tvNilaiPTSGasal.text = nilai?.matematika?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.matematika?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.matematika?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.matematika?.pASGenap
                }
                12 -> {
                    tvNamaMapel.text ="PABP"
                    tvNilaiPTSGasal.text = nilai?.pABP?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.pABP?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.pABP?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.pABP?.pASGenap
                }
                13 -> {
                    tvNamaMapel.text ="PPKn"
                    tvNilaiPTSGasal.text = nilai?.pPKn?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.pPKn?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.pPKn?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.pPKn?.pASGenap
                }
                14 -> {
                    tvNamaMapel.text ="Penjasorkes"
                    tvNilaiPTSGasal.text = nilai?.penjasorkes?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.penjasorkes?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.penjasorkes?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.penjasorkes?.pASGenap
                }
                15 -> {
                    tvNamaMapel.text ="Produktif"
                    tvNilaiPTSGasal.text = nilai?.produktif?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.produktif?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.produktif?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.produktif?.pASGenap
                }
                16 -> {
                    tvNamaMapel.text ="Sejarah Indonesia"
                    tvNilaiPTSGasal.text = nilai?.sejarahIndonesia?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.sejarahIndonesia?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.sejarahIndonesia?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.sejarahIndonesia?.pASGenap
                }
                17 -> {
                    tvNamaMapel.text ="Seni Budaya"
                    tvNilaiPTSGasal.text = nilai?.seniBudaya?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.seniBudaya?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.seniBudaya?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.seniBudaya?.pASGenap
                }
                18 -> {
                    tvNamaMapel.text ="Simulasi Digital"
                    tvNilaiPTSGasal.text = nilai?.simulasiDigital?.pTSGasal
                    tvNilaiPASGasal.text = nilai?.simulasiDigital?.pASGasal
                    tvNilaiPTSGenap.text = nilai?.simulasiDigital?.pTSGenap
                    tvNilaiPASGenap.text = nilai?.simulasiDigital?.pASGenap
                }
            }


        }

    }

    override fun getItemCount(): Int {
        return namaMapel.size
    }

    override fun onBindViewHolder(holder: DaftarNilaiAdapter.SPPViewHolder, position: Int) {
        holder.bindString(nilai,namaMapel,namaSiswa,nis,kelas,position)
    }
}