@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.activities.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.gantipass.GantiPassActivity
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Siswa
import com.smkn9.semarang.sip_smk9.model.Menu
import com.smkn9.semarang.sip_smk9.network.ServiceClient
import com.smkn9.semarang.sip_smk9.network.ServiceNetwork
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_main_home.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.util.*


class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var service: ServiceClient
    private lateinit var pd: ProgressDialog
    var listMenu: MutableList<Menu> = mutableListOf()
    lateinit var tvNama: TextView
    lateinit var tvKelas: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar_home)
        setSupportActionBar(toolbar)

        val calendar = Calendar.getInstance().time
        val dateFormat = DateFormat.getDateInstance(DateFormat.FULL).format(calendar)

        val dateTextView = findViewById<TextView>(R.id.date)
        dateTextView.text = dateFormat
//        Siswa.getNamaSiswa(parent_home)
//        Siswa.getKelasSiswa(parent_home)
        initDrawer()
        val rvHome = rv_home
        rvHome.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
        val listNamaMenu = arrayOf(
//                "Web Sekolah",
//                "Info Sekolah",
//                "Pusdaka",
                "Registrasi Wajah",
                "Presensi\nHadir Pulang",
//                "Presensi Mapel",
                "Ijin",
                "Rekap Kehadiran",
//                "Pengumuman Kelulusan",
//                "Administrasi",
//                "Peminjaman Perpus",
//                "Ujian",
//                "CBT",
//                "Raport",
                "Keluar")
        val listGambarMenu = arrayOf(
//                R.drawable.website,
//                R.drawable.info,
//                R.drawable.pusdaka,
                R.drawable.register,
                R.drawable.attendance,
                R.drawable.surat_ijin,
//                R.drawable.perpustakaan,
                R.drawable.kehadiran,
//                R.drawable.pengumuman_un,
//                R.drawable.spp,
//                R.drawable.peminjaman_perpus,
//                R.drawable.ujian,
//                R.drawable.cbt,
//                R.drawable.daftar_nilai,
                R.drawable.logout)


        for (i in listGambarMenu.indices) {
            val menu = Menu(listGambarMenu[i], listNamaMenu[i])
            listMenu.add(menu)
        }

        val adapter = com.smkn9.semarang.sip_smk9.adapter.MenuAdapter(listMenu)
        rvHome.adapter = adapter

        val jurnalKelas = Siswa.getLinkJurnalKelas(parent_home)
        if (jurnalKelas == "") {
            getJurnalKelas()
        }




        val drawer = findViewById<View>(R.id.parent_home) as androidx.drawerlayout.widget.DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()



    }


    override fun onNavigationItemSelected(menu: MenuItem): Boolean {
        val id = menu.itemId
        if (id == R.id.nav_home_ganti_pass){
            startActivity(intentFor<GantiPassActivity>())
        }

        val drawer = findViewById<View>(R.id.parent_home) as androidx.drawerlayout.widget.DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun initDrawer(){
        val navigationView = nav_view_home
        val headerView = navigationView.getHeaderView(0)
        val tvNama = headerView.findViewById<View>(R.id.tv_nav_home_nama) as TextView
        tvNama.text = Siswa.getNamaSiswa(parent_home)
        val tvKelas = headerView.findViewById<View>(R.id.tv_nav_home_kelas) as TextView
        tvKelas.text = Siswa.getKelasSiswa(parent_home)

        navigationView.setNavigationItemSelectedListener(this)
    }



    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.parent_home) as androidx.drawerlayout.widget.DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed();
        }
    }

    fun getJurnalKelas() {

        pd = ProgressDialog(this)
        pd.setMessage("Load data ...")
        pd.setCancelable(false)
        pd.show()



        service = ServiceNetwork.getService(parent_home)
        val getlink = service.getJurnalKelas(
                "" + Siswa.getLinkPresensi(parent_home),
                "readJurnalKelas",
                "" + Siswa.getTingkatan(parent_home),
                "" + Siswa.getKelasSiswa(parent_home)
        )

        getlink.enqueue(object : Callback<ResponseGetJurnalKelas> {
            override fun onFailure(call: Call<ResponseGetJurnalKelas>, t: Throwable) {
                pd.dismiss()
                toast("" + t.message)
            }

            override fun onResponse(call: Call<ResponseGetJurnalKelas>, response: Response<ResponseGetJurnalKelas>) {
                pd.dismiss()
                val link = response.body()?.linkKelas
                val sp = Siswa.getSp(parent_home)
                sp.edit().putString(Constant.SISWA_LINK_JURNAL_KELAS, link).apply()
//                tv_nav_home_nama.text = Siswa.getNamaSiswa(parent_home)
//                tv_nav_home_kelas.text = Siswa.getKelasSiswa(parent_home)
            }

        })

    }
}
