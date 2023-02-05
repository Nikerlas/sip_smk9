package com.smkn9.semarang.sip_smk9.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.github.chrisbanes.photoview.PhotoView
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity.Companion.hour
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity.Companion.minute
import com.smkn9.semarang.sip_smk9.activities.essay.MenuEssayActivity.Companion.second
import com.smkn9.semarang.sip_smk9.activities.essay.SoalEssayItem
import com.smkn9.semarang.sip_smk9.helper.Constant
import com.smkn9.semarang.sip_smk9.helper.Constant.LIST_JAWABAN_ESSAY_HEADER
import org.jetbrains.anko.toast

//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;

/**
 * Created by pertambangan on 18/02/18.
 */

class SoalEssayAdapter

(private val listSoal: List<SoalEssayItem>?, private val jumlahSoal: Int?, private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<SoalEssayAdapter.SoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.smkn9.semarang.sip_smk9.adapter.SoalEssayAdapter.SoalViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_question_essay, parent, false)
//        val frameLayout = FrameLayout(parent.context)
//        frameLayout.addView(v)
        return SoalViewHolder(v)
    }

    inner class SoalViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        internal var tvSoal: TextView
        internal var ivSoal: ImageView
        internal var pbSoalEssay: ProgressBar
        internal var etJawaban: EditText
        internal var btnSimpan : Button? = null

        init {
            tvSoal = itemView.findViewById<View>(R.id.tv_item_question_essay) as TextView
            ivSoal = itemView.findViewById<View>(R.id.iv_item_question_essay) as PhotoView
            pbSoalEssay = itemView.findViewById<View>(R.id.pb_soal_essay) as ProgressBar
            etJawaban = itemView.findViewById<View>(R.id.et_jawaban_essay) as EditText
            btnSimpan = itemView.findViewById(R.id.btn_simpan_adapter_essay)


        }


    }

    override fun onBindViewHolder(holder: com.smkn9.semarang.sip_smk9.adapter.SoalEssayAdapter.SoalViewHolder, position: Int) {
        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform()
                .timeout(300000)
        val imageListener = object : RequestListener<Drawable?> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                holder.pbSoalEssay.visibility = View.GONE
                Toast.makeText(holder.itemView.context, "Gambar Soal gagal muncul", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                holder.pbSoalEssay.visibility = View.GONE
                return false
            }

        }
        //ini untuk mendapatkan no soal
        listSoal?.get(position)?.noSoal?.let {
            holder.itemView.context.getSharedPreferences(Constant.LIST_NO_ESSAY_HEADER, MODE_PRIVATE)
                .edit()
                .putInt(Constant.LIST_NO_ESSAY+position, it)
                .apply()
        }

        //ini untuk mendapatkan soal teks
        holder.itemView.context.getSharedPreferences(Constant.LIST_SOAL_ESSAY_HEADER, MODE_PRIVATE)
                .edit()
                .putString(Constant.LIST_SOAL_ESSAY+position, listSoal?.get(position)?.soal)
                .apply()

        //ini untuk mendapatkan soal gambar
        holder.itemView.context.getSharedPreferences(Constant.LIST_SOAL_ESSAY_HEADER, MODE_PRIVATE)
                .edit()
                .putString(Constant.LIST_SOAL_GAMBAR_ESSAY+position, listSoal?.get(position)?.soalGambar)
                .apply()




//        holder.getPosition(position)
        if (!(listSoal?.get(position)?.soalGambar === "")) {
            holder.ivSoal.visibility = View.VISIBLE
            holder.pbSoalEssay.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.soalGambar
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivSoal)
            //            Glide.with(context).load("https://drive.google.com/thumbnail?id=" + linkGambar).into(holder.ivSoal);
        } else {
            holder.ivSoal.visibility = View.GONE
            holder.pbSoalEssay.visibility = View.GONE
        }

        holder.tvSoal.text = listSoal?.get(position)?.soal.toString()

        holder.etJawaban.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                listSoal?.get(position)?.finalAnswer = holder.etJawaban.text.toString()
            }

        })


        holder.btnSimpan?.setOnClickListener {

            holder.itemView.context.getSharedPreferences(Constant.DURASI_UJIAN_ESSAY_SIMPAN_HEADER, MODE_PRIVATE)
                    .edit()
                    .putInt(Constant.JAM_UJIAN_ESSAY_SIMPAN, hour)
                    .putInt(Constant.MENIT_UJIAN_ESSAY_SIMPAN, minute)
                    .putInt(Constant.DETIK_UJIAN_ESSAY_SIMPAN, second)
                    .apply()

            holder.itemView.context.getSharedPreferences(LIST_JAWABAN_ESSAY_HEADER, MODE_PRIVATE)
                    .edit()
                    .putString(Constant.LIST_JAWABAN_ESSAY+position,listSoal?.get(position)?.finalAnswer)
                    .apply()

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


