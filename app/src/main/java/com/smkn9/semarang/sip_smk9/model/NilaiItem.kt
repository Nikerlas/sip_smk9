package com.smkn9.semarang.sip_smk9.model
import com.google.gson.annotations.SerializedName


data class NilaiItem(

		@field:SerializedName("Produktif")
	val produktif: Produktif? = null,

		@field:SerializedName("Seni Budaya")
	val seniBudaya: SeniBudaya? = null,

		@field:SerializedName("Bhs Inggris")
	val bhsInggris: BhsInggris? = null,

		@field:SerializedName("Hansek")
	val hansek: Hansek? = null,

		@field:SerializedName("PPKn")
	val pPKn: PPKn? = null,

		@field:SerializedName("Bhs Indonesia")
	val bhsIndonesia: BhsIndonesia? = null,

		@field:SerializedName("Sejarah Indonesia")
	val sejarahIndonesia: SejarahIndonesia? = null,

		@field:SerializedName("Kepariwisataan")
	val kepariwisataan: Kepariwisataan? = null,

		@field:SerializedName("Baca Tulis Qur'an")
	val bacaTulisQurAn: BacaTulisQurAn? = null,

		@field:SerializedName("Bimbingan Konseling")
	val bimbinganKonseling: BimbinganKonseling? = null,

		@field:SerializedName("Penjasorkes")
	val penjasorkes: Penjasorkes? = null,

		@field:SerializedName("Ipa Terapan")
	val ipaTerapan: IpaTerapan? = null,

		@field:SerializedName("nama")
	val nama: String? = null,

		@field:SerializedName("Matematika")
	val matematika: Matematika? = null,

		@field:SerializedName("Kimia")
	val kimia: Kimia? = null,

		@field:SerializedName("Ekstrakurikuler")
	val ekstrakurikuler: Ekstrakurikuler? = null,

		@field:SerializedName("nis")
	val nis: String? = null,

		@field:SerializedName("PABP")
	val pABP: PABP? = null,

		@field:SerializedName("Simulasi Digital")
	val simulasiDigital: SimulasiDigital? = null,

		@field:SerializedName("Bhs Daerah")
	val bhsDaerah: BhsDaerah? = null,

		@field:SerializedName("Fisika")
	val fisika: Fisika? = null
)