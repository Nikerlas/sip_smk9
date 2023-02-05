package com.smkn9.semarang.sip_smk9.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.model.Pengumuman
import kotlinx.android.synthetic.main.item_info.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick

//class InfoAdapter {}
class InfoAdapter(val listPengumuman:List<Pengumuman>): androidx.recyclerview.widget.RecyclerView.Adapter<com.smkn9.semarang.sip_smk9.adapter.InfoAdapter.PengumumanViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.smkn9.semarang.sip_smk9.adapter.InfoAdapter.PengumumanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_info,parent,false)
        return com.smkn9.semarang.sip_smk9.adapter.InfoAdapter.PengumumanViewHolder(view)
    }

    class PengumumanViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        val tvTanggalPengumuman = view.tv_item_info_tanggal
        val tvPerihalPengumuman = view.tv_item_info_perihal

        fun bindPengumuman(pengumuman: Pengumuman){
            tvTanggalPengumuman.text = pengumuman.tanggal
            tvPerihalPengumuman.text = pengumuman.perihal

            itemView.onClick {
                itemView.context.startActivity(itemView.context.intentFor<com.smkn9.semarang.sip_smk9.activities.DetailInfoActivity>(com.smkn9.semarang.sip_smk9.helper.Constant.BUNDLE_DETAIL to pengumuman.linkDokumen))
            }
        }

    }

    override fun getItemCount(): Int {
        return listPengumuman.size
    }

    override fun onBindViewHolder(holder: com.smkn9.semarang.sip_smk9.adapter.InfoAdapter.PengumumanViewHolder, position: Int) {
        holder.bindPengumuman(listPengumuman[position])
    }
}