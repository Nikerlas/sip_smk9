@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.alifproduction.skansa.saygolearn.activities.pengumumankelulusan.ResponseStatusPengumuman
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.*
import com.smkn9.semarang.sip_smk9.activities.home.ResponseStatusRegistrasi
import com.smkn9.semarang.sip_smk9.activities.homepresensiwajah.ResponseLoginAdmin
import com.smkn9.semarang.sip_smk9.activities.loginsim.LoginSimActivity
import com.smkn9.semarang.sip_smk9.activities.pengumumankelulusan.ValidasiNisKelulusanActivity
import com.smkn9.semarang.sip_smk9.activities.pinjamanperpus.PinjamanPerpusActivity
import com.smkn9.semarang.sip_smk9.activities.pinjamanperpus.ResponsePinjamanPerpus
import com.smkn9.semarang.sip_smk9.activities.presensi.HomePresensiKehadiranSiswaActivity
import com.smkn9.semarang.sip_smk9.activities.presensimapel.HomePresensiMapelSiswaActivity
import com.smkn9.semarang.sip_smk9.activities.raport.ResponsePengumumanRaport
import com.smkn9.semarang.sip_smk9.activities.raport.ValidasiNisRaportActivity
import com.smkn9.semarang.sip_smk9.activities.registerdevice.RegisterDeviceActivity
import com.smkn9.semarang.sip_smk9.activities.registerwajah.RegisterActivity
import com.smkn9.semarang.sip_smk9.activities.rekappresensi.RekapPresensiGuKarActivity
import com.smkn9.semarang.sip_smk9.activities.tagihan.AdministrasiActivity
import com.smkn9.semarang.sip_smk9.activities.tefa.TefaActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.model.Menu
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_home_presensi_wajah.*
import kotlinx.android.synthetic.main.item_menu.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//class MenuAdapter {}
class MenuAdapter(val listMenu: List<Menu>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<com.smkn9.semarang.sip_smk9.adapter.MenuAdapter.MenuViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): com.smkn9.semarang.sip_smk9.adapter.MenuAdapter.MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return com.smkn9.semarang.sip_smk9.adapter.MenuAdapter.MenuViewHolder(view)
    }

    class MenuViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        val tvNamaMenu = view.tv_item_menu
        val ivGambarMenu = view.iv_item_menu
        val pbGambarMenu = view.pb_loading_menu

        fun bindMenu(menu: Menu) {
            tvNamaMenu.text = menu.namaMenu
            ivGambarMenu.setImageResource(menu.gambarMenu)

            itemView.onClick {
                when (menu.namaMenu) {
//                    "Web Sekolah" -> {
//                        itemView.context.startActivity(
//                            itemView.context.intentFor<DetailInfoActivity>(
//                                Constant.BUNDLE_DETAIL to "http://smkpelnus2.sch.id/"
//                            )
//                        )
//                    }

                    "Ijin" -> {
                        itemView.context.startActivity(
                            itemView.context.intentFor<DetailInfoActivity>(
                                Constant.BUNDLE_DETAIL to Siswa.getLinkIjinSiswa(itemView)
                            )
                        )
                    }

                    "CBT" -> {
                        itemView.context.startActivity(
                            itemView.context.intentFor<DetailInfoActivity>(
                                Constant.BUNDLE_DETAIL to "http://117.20.58.162/cbt/"
                            )
                        )
                    }

                    "Pusdaka" -> {
                        itemView.context.startActivity(
                            itemView.context.intentFor<DetailInfoActivity>(
                                Constant.BUNDLE_DETAIL to "http://117.20.58.162:8181/pusdaka/"
                            )
                        )
                    }

                    "Pengumuman Kelulusan" -> {
                        val pd = ProgressDialog(itemView.context)
                        pd.setMessage("Cek status pengumuman ...")
                        pd.setCancelable(false)
                        pd.show()

                        val sp = itemView.context.getSharedPreferences(
                            Constant.MASTER_SISWA_HEADER,
                            Context.MODE_PRIVATE
                        )

                        val urlKelulusan =
                            sp.getString(Constant.SISWA_LINK_PENGUMUMAN_KELULUSAN_SISWA, "")

                        val service = ServiceNetwork.getService(itemView)
                        val getStatusPengumuman = service.getStatusPengumuman(
                            "" + urlKelulusan,
                            "readStatusPengumuman"
                        )

                        getStatusPengumuman.enqueue(object : Callback<ResponseStatusPengumuman> {
                            override fun onFailure(
                                call: Call<ResponseStatusPengumuman>,
                                t: Throwable
                            ) {
                                pd.dismiss()
                                itemView.context.toast("" + t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponseStatusPengumuman>,
                                response: Response<ResponseStatusPengumuman>
                            ) {
                                pd.dismiss()
                                val status = response.body()?.status?.toLowerCase()
                                if (status == "on") {
                                    itemView.context.startActivity(itemView.context.intentFor<ValidasiNisKelulusanActivity>())
                                } else {
                                    itemView.context.alert {
                                        title = "Konfirmasi"
                                        message = "Pengumuman Kelulusan belum dibuka"
                                        okButton { }
                                    }.show()
                                }
                            }

                        })
                    }
                    "Info Sekolah" -> itemView.context.startActivity(
                        itemView.context.intentFor<DetailActivity>(
                            Constant.BUNDLE_DETAIL to "INFO"
                        )
                    )
                    "Tefa" -> itemView.context.startActivity(itemView.context.intentFor<TefaActivity>())
                    "Administrasi" -> itemView.context.startActivity(itemView.context.intentFor<AdministrasiActivity>())
//                    "Gerakan Literasi Nasional" -> itemView.context.startActivity(itemView.context.intentFor<DetailInfoActivity>(Constant.BUNDLE_DETAIL to "https://gln.kemdikbud.go.id/glnsite/"))
                    "Presensi Mapel" -> {
                        val pd = ProgressDialog(itemView.context)
                        pd.setMessage("Cek status registrasi presensi ...")
                        pd.setCancelable(false)
                        pd.show()

                        val nis = Siswa.getNIS(itemView)
                        val getStatusRegister =
                            ServiceNetwork.getService(itemView).cekStatusRegister(
                                "" + Siswa.getLinkJurnalKelas(itemView),
                                "readStatusRegisterPresensi",
                                "" + nis
                            )

                        getStatusRegister.enqueue(object : Callback<ResponseStatusRegistrasi> {
                            override fun onFailure(
                                call: Call<ResponseStatusRegistrasi>,
                                t: Throwable
                            ) {
                                pd.dismiss()
                                itemView.context.toast("" + t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponseStatusRegistrasi>,
                                response: Response<ResponseStatusRegistrasi>
                            ) {
                                pd.dismiss()
                                pd.dismiss()
                                val status = response.body()?.status

                                if (status == "belum") {
                                    itemView.context.alert {
                                        title = "Konfirmasi"
                                        message =
                                            "Maaf Anda belum melakukan registrasi device untuk presensi"
                                        positiveButton("Daftar") {
                                            itemView.context.startActivity(itemView.context.intentFor<RegisterDeviceActivity>())
                                        }
                                    }.show()

                                } else {
                                    itemView.context.startActivity(itemView.context.intentFor<HomePresensiMapelSiswaActivity>())
                                }
                            }

                        })
                    }
                    "Peminjaman Perpus" -> {
                        val pd = ProgressDialog(itemView.context)
                        pd.setMessage("Cek data peminjaman perpus ...")
                        pd.setCancelable(false)
                        pd.show()

                        val nis = Siswa.getNIS(itemView)
                        val kelas = Siswa.getKelasSiswa(itemView)
                        val tingkatan = Siswa.getTingkatan(itemView)
                        val urlPerpus = Siswa.getLinkPinjamanPerpus(itemView)
                        val getInfoPerpus =
                            ServiceNetwork.getService(itemView).getInfoPinjamanPerpus(
                                "" + urlPerpus,
                                "readPinjamanPerpus",
                                "" + tingkatan,
                                "" + kelas,
                                "" + nis
                            )

                        getInfoPerpus.enqueue(object : Callback<ResponsePinjamanPerpus> {
                            override fun onFailure(
                                call: Call<ResponsePinjamanPerpus>,
                                t: Throwable
                            ) {
                                pd.dismiss()
                                itemView.context.toast("" + t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponsePinjamanPerpus>,
                                response: Response<ResponsePinjamanPerpus>
                            ) {
                                pd.dismiss()
                                val status = response.body()?.hasil?.status

                                if (status == "gagal") {
                                    itemView.context.alert {
                                        title = "Konfirmasi"
                                        message = "" + response.body()?.hasil?.pesan.toString()
                                        okButton {

                                        }
                                    }.show()

                                } else {
                                    itemView.context.startActivity(
                                        itemView.context.intentFor<PinjamanPerpusActivity>(
                                            Constant.BUNDLE_DETAIL to response.body()?.hasil?.pesan.toString()
                                        )
                                    )

                                }
                            }

                        })
                    }
                    "Ujian" -> itemView.context.startActivity(itemView.context.intentFor<KodeGuruActivity>())
                    "Raport" -> {
                        val pd = ProgressDialog(itemView.context)
                        pd.setMessage("Cek status pengumuman Raport ...")
                        pd.setCancelable(false)
                        pd.show()

                        val sp = itemView.context.getSharedPreferences(
                            Constant.MASTER_SISWA_HEADER,
                            Context.MODE_PRIVATE
                        )

                        val urlRaport = sp.getString(Constant.SISWA_LINK_RAPORT_SISWA, "")
                        val tingkatan = sp.getString(Constant.SISWA_TINGKATAN, "")
                        val nis = sp.getString(Constant.SISWA_NIS, "")

                        val service = ServiceNetwork.getService(itemView)
                        val getStatusPengumuman = service.getPengumumanRaport(
                            "" + urlRaport,
                            "readRaport",
                            "" + tingkatan,
                            "" + nis
                        )

                        getStatusPengumuman.enqueue(object : Callback<ResponsePengumumanRaport> {
                            override fun onFailure(
                                call: Call<ResponsePengumumanRaport>,
                                t: Throwable
                            ) {
                                pd.dismiss()
                                itemView.context.toast("" + t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponsePengumumanRaport>,
                                response: Response<ResponsePengumumanRaport>
                            ) {
                                pd.dismiss()
                                val status = response.body()?.hasil?.status
                                if (status == "sukses") {
                                    val pengumuman =
                                        response.body()?.hasil?.pengumuman?.toLowerCase()
                                    if (pengumuman == "on") {
                                        val administrasi =
                                            response.body()?.hasil?.administrasi?.toLowerCase()

                                        if (administrasi == "lunas") {
                                            itemView.context.startActivity(
                                                itemView.context.intentFor<ValidasiNisRaportActivity>(
                                                    Constant.BUNDLE_DETAIL to response.body()?.hasil?.linkRaport
                                                )
                                            )
                                        } else {
                                            val pesan = response.body()?.hasil?.pesan
                                            itemView.context.alert {
                                                title = "Konfirmasi"
                                                message = "" + pesan
                                                okButton { }
                                            }.show()
                                        }


                                    } else {
                                        itemView.context.alert {
                                            title = "Konfirmasi"
                                            message = "Maaf Pengumumuman Raport belum dibuka"
                                            okButton { }
                                        }.show()
                                    }


                                } else {
                                    itemView.context.alert {
                                        title = "Konfirmasi"
                                        message = "Maaf NIS Anda tidak ditemukan"
                                        okButton { }
                                    }.show()
                                }
                            }

                        })
                    }
//                        itemView.context.startActivity(itemView.context.intentFor<DaftarNilaiActivity>())
                    "Keluar" -> {
                        itemView.context.alert("Yakin mau keluar dari aplikasi ?") {
                            yesButton {
                                itemView.context.getSharedPreferences(
                                    Constant.MASTER_SISWA_HEADER,
                                    Context.MODE_PRIVATE
                                ).edit().clear().apply()
                                itemView.context.startActivity(itemView.context.intentFor<LoginSimActivity>())
                                val b = itemView.context as Activity
                                b.finish()
                            }
                            noButton { }
                        }.show()
                    }
                    "Rekap Kehadiran" -> {
//                        itemView.context.toast("Mohon maaf fitur rekap sedang dalam tahap pengembangan")
                        val arrBulan =
                            itemView.context.resources.getStringArray(R.array.bulan).toList()
                        itemView.context.selector(
                            "Pilih Bulan kehadiran",
                            arrBulan
                        ) { dialogInterface,
                            i ->
                            run {
                                itemView.context.startActivity(
                                    itemView.context.intentFor<RekapPresensiGuKarActivity>(
                                        Constant.SISWA_BULAN_PRESENSI to arrBulan[i]

                                    )
                                )

                                //ini untuk rekap presensi kehadiran di jam pelajaran
//                                itemView.context.startActivity(itemView.context.intentFor<DetailPresensiActivity>(Constant.BUNDLE_DETAIL to arrBulan[i]))
                            }

                        }
                    }
                    "Registrasi Wajah" -> {
                        val builder = AlertDialog.Builder(itemView.context)
                        val v = LayoutInflater.from(itemView.context)
                            .inflate(R.layout.dialog_social, itemView.rootView as ViewGroup, false)
                        val username = v.findViewById<EditText>(R.id.et_username_register)
                        val password = v.findViewById<EditText>(R.id.et_pass_register)
                        builder.setView(v)
                        builder.setPositiveButton(
                            "Login",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                if (username.text.toString().isEmpty() || password.text.toString()
                                        .isEmpty()
                                ) {
                                    Toast.makeText(
                                        itemView.context,
                                        "Username atau pass tidak boleh kosong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@OnClickListener
                                }
                                val user = username.text.toString().toLowerCase().trim { it <= ' ' }
                                val pass = password.text.toString().toLowerCase().trim { it <= ' ' }
                                pbGambarMenu.setVisibility(View.VISIBLE)
                                val service = ServiceNetwork.getService(itemView)
                                val urlPresensiSiswa = Siswa.getLinkJurnalKelas(itemView)

                                val loginAdminRegisterWajah = service.loginAdmin(
                                    "" + urlPresensiSiswa,
                                    "login",
                                    "" + user,
                                    "" + pass
                                )

                                loginAdminRegisterWajah.enqueue(object :
                                    Callback<ResponseLoginAdmin> {
                                    override fun onResponse(
                                        call: Call<ResponseLoginAdmin>,
                                        response: Response<ResponseLoginAdmin>
                                    ) {
                                        pbGambarMenu.setVisibility(View.GONE);
                                        dialogInterface.dismiss();
                                        val hasil = response.body()?.getHasil();
                                        if (hasil.equals("sukses")) {
                                            itemView.context.startActivity(itemView.context.intentFor<RegisterActivity>());

                                        } else {
                                            Toast.makeText(
                                                itemView.context,
                                                "Username atau password salah",
                                                Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ResponseLoginAdmin>,
                                        t: Throwable
                                    ) {
                                        pbGambarMenu.setVisibility(View.GONE);
                                        dialogInterface.dismiss();
                                        Log.d(
                                            "menu login",
                                            "user : " + user + " pass : " + pass + " error : " + t.message
                                        )
                                        Toast.makeText(
                                            itemView.context,
                                            "" + t.message,
                                            Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                })

                            })
                            .setNegativeButton("Batal") { dialogInterface, i -> dialogInterface.dismiss() }
                        builder.show()
                    }
                    "Presensi\nHadir Pulang" -> {
                        val arrLokasi =
                            itemView.context.resources.getStringArray(R.array.lokasi).toList()
                        itemView.context.selector(
                            "Pilih Lokasi Presensi",
                            arrLokasi
                        ) { dialogInterface,
                            i ->
                            run {
                                val pd = ProgressDialog(itemView.context)
                                pd.setMessage("Cek status registrasi hp presensi ...")
                                pd.setCancelable(false)
                                pd.show()

                                val nis = Siswa.getNIS(itemView)
                                val getStatusRegister =
                                    ServiceNetwork.getService(itemView).cekStatusRegister(
                                        "" + Siswa.getLinkJurnalKelas(itemView),
                                        "readStatusRegisterPresensi",
                                        "" + nis
                                    )

                                getStatusRegister.enqueue(object :
                                    Callback<ResponseStatusRegistrasi> {
                                    override fun onFailure(
                                        call: Call<ResponseStatusRegistrasi>,
                                        t: Throwable
                                    ) {
                                        pd.dismiss()
                                        itemView.context.toast("" + t.message)
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseStatusRegistrasi>,
                                        response: Response<ResponseStatusRegistrasi>
                                    ) {
                                        pd.dismiss()
//                                        pd.dismiss()
                                        val status = response.body()?.status

                                        if (status == "belum") {
                                            itemView.context.alert {
                                                title = "Konfirmasi"
                                                message =
                                                    "Maaf Anda belum melakukan registrasi device untuk presensi"
                                                positiveButton("Daftar") {
                                                    itemView.context.startActivity(itemView.context.intentFor<RegisterDeviceActivity>())
                                                }
                                            }.show()

                                        } else {
                                            itemView.context.startActivity(
                                                itemView.context.intentFor<HomePresensiKehadiranSiswaActivity>(
                                                    Constant.SISWA_LOKASI_PRESENSI to arrLokasi[i]
                                                )
                                            )

                                        }
                                    }

                                })


                            }
                        }
                    }
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return listMenu.size
    }

    override fun onBindViewHolder(
        holder: com.smkn9.semarang.sip_smk9.adapter.MenuAdapter.MenuViewHolder,
        position: Int
    ) {
        holder.bindMenu(listMenu[position])
    }
}