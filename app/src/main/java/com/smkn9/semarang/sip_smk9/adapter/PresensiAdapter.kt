@file:Suppress("DEPRECATION")

package com.smkn9.semarang.sip_smk9.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smkn9.semarang.sip_smk9.R
import kotlinx.android.synthetic.main.item_presensi.view.*

//class PresensiAdapter {}
class PresensiAdapter(val listStatusPresensi:List<String>): androidx.recyclerview.widget.RecyclerView.Adapter<com.smkn9.semarang.sip_smk9.adapter.PresensiAdapter.PresensiViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.smkn9.semarang.sip_smk9.adapter.PresensiAdapter.PresensiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_presensi,parent,false)
        return com.smkn9.semarang.sip_smk9.adapter.PresensiAdapter.PresensiViewHolder(view)
    }

    class PresensiViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        val cvPresensi = view.cv_item_presensi
        val tvTgglPresensi = view.tv_item_presensi_tanggal
        val tvStatusPresensi = view.tv_item_presensi_status



        @SuppressLint("ResourceAsColor")
        fun bindString(position: Int, statusPresensi: String){

            val tanggalPresensi = position+1
            tvTgglPresensi.text = tanggalPresensi.toString()
            if(statusPresensi!=""){
                tvStatusPresensi.text = statusPresensi
                when(statusPresensi.toUpperCase()){
                    "H" -> cvPresensi.setCardBackgroundColor(itemView.context.resources.getColor(R.color.colorGreen) )
                    "A" -> cvPresensi.setCardBackgroundColor(itemView.context.resources.getColor(R.color.colorRed))
                    "I" -> cvPresensi.setCardBackgroundColor(itemView.context.resources.getColor(R.color.colorYellow))
                    "S" -> cvPresensi.setCardBackgroundColor(itemView.context.resources.getColor(R.color.colorPurple))
                    "BL" -> cvPresensi.setCardBackgroundColor(itemView.context.resources.getColor(R.color.colorOrange))
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return listStatusPresensi.size
    }

    override fun onBindViewHolder(holder: com.smkn9.semarang.sip_smk9.adapter.PresensiAdapter.PresensiViewHolder, position: Int) {
        holder.bindString(position,listStatusPresensi[position])
    }
}