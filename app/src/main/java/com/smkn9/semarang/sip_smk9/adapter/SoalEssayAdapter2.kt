package com.smkn9.semarang.sip_smk9.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.chrisbanes.photoview.PhotoView
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity.Companion.hour
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity.Companion.minute
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity.Companion.second
import com.smkn9.semarang.sip_smk9.helper.Constant
import org.jetbrains.anko.toast

//class SoalAdapter22 {
//}

class SoalEssayAdapter2
(private val jumlahSoal: Int?, private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<SoalEssayAdapter2.SoalViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoalEssayAdapter2.SoalViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_question_essay, parent, false)
        return SoalViewHolder(v)
    }

    inner class SoalViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        internal var tvSoal: TextView
        internal var ivSoal: ImageView
        internal var etJawaban: EditText
        internal var btnSimpan : Button? = null

        init {
            tvSoal = itemView.findViewById<View>(R.id.tv_item_question_essay) as TextView
            ivSoal = itemView.findViewById<View>(R.id.iv_item_question_essay) as PhotoView
            etJawaban = itemView.findViewById<View>(R.id.et_jawaban_essay) as EditText
            btnSimpan = itemView.findViewById(R.id.btn_simpan_adapter_essay)


        }


    }

    override fun onBindViewHolder(holder: SoalEssayAdapter2.SoalViewHolder, position: Int) {
        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform()
                .timeout(300000)
        val sp  = holder.itemView.context.getSharedPreferences(Constant.LIST_SOAL_ESSAY_HEADER, MODE_PRIVATE)
        val spJawab  = holder.itemView.context.getSharedPreferences(Constant.LIST_JAWABAN_ESSAY_HEADER, MODE_PRIVATE)

//        holder.getPosition(position)
        if ((sp.getString(Constant.LIST_SOAL_GAMBAR_ESSAY+position,"")== "") or (  sp.getString(Constant.LIST_SOAL_GAMBAR_ESSAY+position,"") == null) ) {
            holder.ivSoal.visibility = View.GONE
        } else {
            holder.ivSoal.visibility = View.VISIBLE
            val linkGambar = sp.getString(Constant.LIST_SOAL_GAMBAR_ESSAY+position,"")
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .apply(requestOptions)
                    .into(holder.ivSoal)

        }




        holder.tvSoal.text = sp.getString(Constant.LIST_SOAL_ESSAY+position,"")
        //memasukan pilihan jawaban di setiap pertanyaan ke dalam ArrayList
        if(holder.etJawaban.text.toString() == "") {
            holder.etJawaban.append(spJawab.getString(Constant.LIST_JAWABAN_ESSAY + position, ""))
        }








        holder.btnSimpan?.setOnClickListener {

            holder.itemView.context.getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_SIMPAN_HEADER, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(Constant.JAM_UJIAN_ESSAY_SIMPAN, hour)
                    .putInt(Constant.MENIT_UJIAN_ESSAY_SIMPAN, minute)
                    .putInt(Constant.DETIK_UJIAN_ESSAY_SIMPAN, second)
                    .apply()

            holder.itemView.context.getSharedPreferences(Constant.LIST_JAWABAN_ESSAY_HEADER, MODE_PRIVATE)
                    .edit().putString(Constant.LIST_JAWABAN_ESSAY+position,holder.etJawaban.text.toString()).apply()

            holder.itemView.context.toast("Jawaban berhasil disimpan")

        }







    }

    private fun loadSharedPreferences(position: Int) {
        val sp = context.getSharedPreferences("jawaban", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("soal$position", true)
        editor.apply()

        val rxSharedPreferences = RxSharedPreferences.create(sp)

        //SharedPreferences sp2 = context.getSharedPreferences("jawaban",Context.MODE_PRIVATE);
        //        final boolean spCoba = sp.getBoolean("soal"+position,false);
        //        final Observable<Boolean> spObservable = Observable.create(new ObservableOnSubscribe<Boolean>() {
        //            @Override
        //            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
        //           //assert spCoba != null;
        //               // spCoba
        //            }
        //        });
    }

    override fun getItemCount(): Int {
        //        return listSoal.size();
        //        return 40;
        return jumlahSoal!!
    }




    companion object {
        private val RECOVERY_REQUEST = 1
    }


}
