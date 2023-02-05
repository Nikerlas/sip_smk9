package com.smkn9.semarang.sip_smk9.adapter

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
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
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.model.SoalItem
import org.jetbrains.anko.toast
import java.util.*


//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;

/**
 * Created by pertambangan on 18/02/18.
 */

class SoalAdapter
//    YouTubePlayerView video;
//List<RadioButton>listRadioButton;
//RadioButton rbA, rbB, rbC, rbD, rbE;

(private val listSoal: List<SoalItem>?, private val jumlahSoal: Int?, private val context: Context) : RecyclerView.Adapter<SoalAdapter.SoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoalAdapter.SoalViewHolder{

            val v = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false)
            return SoalViewHolder(v)
        }


    inner class SoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvSoal: TextView
        internal var rgJawaban: RadioGroup
        internal var rbA: RadioButton
        internal var rbB: RadioButton
        internal var rbC: RadioButton
        internal var rbD: RadioButton
        internal var rbE: RadioButton
        internal var rbN: RadioButton? = null
        internal var ivSoal: ImageView
        internal var pbSoal: ProgressBar
        internal var pbRbA: ProgressBar
        internal var pbRbB: ProgressBar
        internal var pbRbC: ProgressBar
        internal var pbRbD: ProgressBar
        internal var pbRbE: ProgressBar
        internal var ivRbA: ImageView
        internal var ivRbB: ImageView
        internal var ivRbC: ImageView
        internal var ivRbD: ImageView
        internal var ivRbE: ImageView
        internal var video: YouTubeThumbnailView
        internal var rl: RelativeLayout
        internal var btnSimpan : Button? = null

        init {
            tvSoal = itemView.findViewById<View>(R.id.tv_item_question) as TextView
            rgJawaban = itemView.findViewById<View>(R.id.rg_item_question) as RadioGroup

            rbA = itemView.findViewById<View>(R.id.rb_a) as RadioButton
            rbB = itemView.findViewById<View>(R.id.rb_b) as RadioButton
            rbC = itemView.findViewById<View>(R.id.rb_c) as RadioButton
            rbD = itemView.findViewById<View>(R.id.rb_d) as RadioButton
            rbE = itemView.findViewById<View>(R.id.rb_e) as RadioButton
            // rbN = (RadioButton) itemView.findViewById(R.id.rb_n);

            ivSoal = itemView.findViewById<View>(R.id.iv_item_question) as PhotoView
            pbSoal = itemView.findViewById<View>(R.id.pb_soal) as ProgressBar
            pbRbA = itemView.findViewById<View>(R.id.pb_rb_a) as ProgressBar
            pbRbB = itemView.findViewById<View>(R.id.pb_rb_b) as ProgressBar
            pbRbC = itemView.findViewById<View>(R.id.pb_rb_c) as ProgressBar
            pbRbD = itemView.findViewById<View>(R.id.pb_rb_d) as ProgressBar
            pbRbE = itemView.findViewById<View>(R.id.pb_rb_e) as ProgressBar

            ivRbA = itemView.findViewById<View>(R.id.iv_rb_a) as ImageView
            ivRbB = itemView.findViewById<View>(R.id.iv_rb_b) as ImageView
            ivRbC = itemView.findViewById<View>(R.id.iv_rb_c) as ImageView
            ivRbD = itemView.findViewById<View>(R.id.iv_rb_d) as ImageView
            ivRbE = itemView.findViewById<View>(R.id.iv_rb_e) as ImageView


//            ImageZoomHelper.setViewZoomable(ivSoal)
//            ImageZoomHelper.setZoom(ivSoal,true)

            video = itemView.findViewById(R.id.youtube_view)
            rl = itemView.findViewById(R.id.rl_movie)

            btnSimpan = itemView.findViewById(R.id.btn_simpan_adapter)


        }


    }

    override fun onBindViewHolder(holder: SoalAdapter.SoalViewHolder, position: Int) {
      memasukanData(holder,position)
    }

    fun memasukanData(holder: SoalViewHolder, position: Int){
        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform()
                .timeout(300000)


//        val requestOptions = RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .priority(Priority.HIGH)
//                .dontAnimate()
//                .dontTransform()

        val imageListener = object : RequestListener<Drawable?> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                holder.pbSoal.visibility = View.GONE
                holder.pbRbA.visibility = View.GONE
                holder.pbRbB.visibility = View.GONE
                holder.pbRbC.visibility = View.GONE
                holder.pbRbD.visibility = View.GONE
                holder.pbRbE.visibility = View.GONE
                Toast.makeText(holder.itemView.context, ""+e?.message, Toast.LENGTH_SHORT).show()
//                Log.d("infoSoal",e?.message.toString())
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                holder.pbSoal.visibility = View.GONE
                holder.pbRbA.visibility = View.GONE
                holder.pbRbB.visibility = View.GONE
                holder.pbRbC.visibility = View.GONE
                holder.pbRbD.visibility = View.GONE
                holder.pbRbE.visibility = View.GONE
                return false
            }

        }

//        holder.getPosition(position)
        if (!(listSoal?.get(position)?.idGambar === "")) {
            holder.ivSoal.visibility = View.VISIBLE
            holder.pbSoal.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.idGambar
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivSoal)
            //            Glide.with(context).load("https://drive.google.com/thumbnail?id=" + linkGambar).into(holder.ivSoal);
        } else {
            holder.ivSoal.visibility = View.GONE
        }


        if (!(listSoal?.get(position)?.idVideo === "")) {

            val onThumbnailLoadedListener = object : YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                override fun onThumbnailLoaded(youTubeThumbnailView: YouTubeThumbnailView, s: String) {
                    youTubeThumbnailView.visibility = View.VISIBLE
                }

                override fun onThumbnailError(youTubeThumbnailView: YouTubeThumbnailView, errorReason: YouTubeThumbnailLoader.ErrorReason) {

                }
            }

            holder.rl.visibility = View.VISIBLE
            holder.video.initialize(com.smkn9.semarang.sip_smk9.helper.Config.YOUTUBE_API_KEY, object : YouTubeThumbnailView.OnInitializedListener {
                override fun onInitializationSuccess(youTubeThumbnailView: YouTubeThumbnailView, youTubeThumbnailLoader: YouTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(listSoal?.get(position)?.idVideo)
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener)
                }

                override fun onInitializationFailure(youTubeThumbnailView: YouTubeThumbnailView, youTubeInitializationResult: YouTubeInitializationResult) {

                }
            })

            holder.video.setOnClickListener {
                val intent = YouTubeStandalonePlayer.createVideoIntent(context as Activity, com.smkn9.semarang.sip_smk9.helper.Config.YOUTUBE_API_KEY, listSoal?.get(position)?.idVideo, 0, true, false)
                context.startActivity(intent)
            }
        } else {
            holder.rl.visibility = View.GONE
        }



        holder.tvSoal.text = listSoal?.get(position)?.soal.toString()
        //memasukan pilihan jawaban di setiap pertanyaan ke dalam ArrayList
        val listJawaban = ArrayList<String>()
        listJawaban.add(listSoal?.get(position)?.answerA.toString())
        listJawaban.add(listSoal?.get(position)?.answerB.toString())
        listJawaban.add(listSoal?.get(position)?.answerC.toString())
        listJawaban.add(listSoal?.get(position)?.answerD.toString())
        listJawaban.add(listSoal?.get(position)?.answerE.toString())


        //memasukan pilihan jawaban ke dalam masing2 radiobutton sesuai posisinya


        //membuat pilihan jawaban teracak
        //        Collections.shuffle(listJawaban);

        holder.rbA.text = listJawaban[0]
        holder.rbB.text = listJawaban[1]
        holder.rbC.text = listJawaban[2]
        holder.rbD.text = listJawaban[3]
        holder.rbE.text = listJawaban[4]



        //menampilkan gambar pada pilihan a
        if (!(listSoal?.get(position)?.getaGambar() === "")) {
            holder.ivRbA.visibility = View.VISIBLE
            holder.pbRbA.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.answerA
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivRbA)
        } else {
            holder.ivRbA.visibility = View.GONE
            holder.pbRbA.visibility = View.GONE
        }

        //menampilkan gambar pada pilihan b
        if (!(listSoal?.get(position)?.getbGambar() === "")) {
            holder.ivRbB.visibility = View.VISIBLE
            holder.pbRbB.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.answerB
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivRbB)
        } else {
            holder.ivRbB.visibility = View.GONE
            holder.pbRbB.visibility = View.GONE
        }

        //menampilkan gambar pada pilihan c
        if (!(listSoal?.get(position)?.getcGambar() === "")) {
            holder.ivRbC.visibility = View.VISIBLE
            holder.pbRbC.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.answerC
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivRbC)
        } else {
            holder.ivRbC.visibility = View.GONE
            holder.pbRbC.visibility = View.GONE
        }

        //menampilkan gambar pada pilihan d
        if (!(listSoal?.get(position)?.getdGambar() === "")) {
            holder.ivRbD.visibility = View.VISIBLE
            holder.pbRbD.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.answerD
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivRbD)
        } else {
            holder.ivRbD.visibility = View.GONE
            holder.pbRbD.visibility = View.GONE
        }

        //menampilkan gambar pada pilihan e
        if (!(listSoal?.get(position)?.geteGambar() === "")) {
            holder.ivRbE.visibility = View.VISIBLE
            holder.pbRbE.visibility = View.VISIBLE
            val linkGambar = listSoal?.get(position)?.answerE
            Glide.with(context)
                    .load("https://docs.google.com/uc?id=$linkGambar")
                    .listener(imageListener)
                    .apply(requestOptions)
                    .into(holder.ivRbE)
        } else {
            holder.ivRbE.visibility = View.GONE
            holder.pbRbE.visibility = View.GONE
        }

        //        listJawaban.clear();


        holder.rgJawaban.tag = position


        holder.rgJawaban.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {


                val radioButtonId = group.checkedRadioButtonId
                //menghindari efek duplikasi kita pakai tag
                val clickedPos = group.tag as Int

                listSoal?.get(clickedPos)?.selectedRadioButtonId = radioButtonId


                if (radioButtonId > 0) {
                    val rb = group.findViewById<RadioButton>(radioButtonId)
                    listSoal?.get(clickedPos)?.finalAnswer = rb.text.toString()
                    loadSharedPreferences(clickedPos)
                }

            }
        }

        listSoal?.get(position)?.selectedRadioButtonId?.let { holder.rgJawaban.check(it) }

        holder.btnSimpan?.setOnClickListener {

            holder.itemView.context.getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.DURASI_UJIAN_SIMPAN_HEADER, MODE_PRIVATE)
                    .edit()
                    .putInt(com.smkn9.semarang.sip_smk9.helper.Constant.JAM_UJIAN_SIMPAN, com.smkn9.semarang.sip_smk9.activities.MenuActivity.hour)
                    .putInt(com.smkn9.semarang.sip_smk9.helper.Constant.MENIT_UJIAN_SIMPAN, com.smkn9.semarang.sip_smk9.activities.MenuActivity.minute)
                    .putInt(com.smkn9.semarang.sip_smk9.helper.Constant.DETIK_UJIAN_SIMPAN, com.smkn9.semarang.sip_smk9.activities.MenuActivity.second)
                    .apply()

//            holder.itemView.context.doAsync {
//                holder.itemView.context.database.use {
//                    val nomorSoal = listSoal?.get(position)!!.noSoal
//                    update(Constant.TABLE_SOAL,Constant.COLUMN_JAWABAN_FINAL to listSoal?.get(position)?.finalAnswer)
//                            .whereArgs(Constant.COLUMN_NO_SOAL+" = {noSoal}", "noSoal" to nomorSoal)
//                            .exec()
//                }
//            }

            holder.itemView.context.getSharedPreferences(com.smkn9.semarang.sip_smk9.helper.Constant.LIST_JAWABAN_HEADER, MODE_PRIVATE)
                    .edit()
                    .putString(com.smkn9.semarang.sip_smk9.helper.Constant.LIST_JAWABAN + position, listSoal?.get(position)?.finalAnswer)
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
        return jumlahSoal!!
    }



    companion object {
        private val RECOVERY_REQUEST = 1
    }


}


