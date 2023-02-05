package com.smkn9.semarang.sip_smk9.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.smkn9.semarang.sip_smk9.R
import com.smkn9.semarang.sip_smk9.model.QuestionNumberModel

import io.reactivex.disposables.Disposable

/**
 * Created by pertambangan on 24/02/18.
 */

class NoSoalAdapter
//public int noSoalTerpilih = 1;

(private val context: Context, private val listNo: List<QuestionNumberModel>) : androidx.recyclerview.widget.RecyclerView.Adapter<com.smkn9.semarang.sip_smk9.adapter.NoSoalAdapter.NoSoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.smkn9.semarang.sip_smk9.adapter.NoSoalAdapter.NoSoalViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_question_number, parent, false)
        return NoSoalViewHolder(v)
    }

    inner class NoSoalViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        internal var tvQuestionNumber: TextView

        init {
            tvQuestionNumber = itemView.findViewById<View>(R.id.tv_item_question_number) as TextView
        }
    }


    override fun onBindViewHolder(holder: com.smkn9.semarang.sip_smk9.adapter.NoSoalAdapter.NoSoalViewHolder, position: Int) {
        holder.tvQuestionNumber.text = listNo[position].questionNumber.toString()

        val sp = context.getSharedPreferences("jawaban", Context.MODE_PRIVATE)
        val rxSharedPreferences = RxSharedPreferences.create(sp)
        //Boolean soal = sp.getBoolean("soal"+position,false);


        //        Observer<Boolean> tvObserver = new Observer<Boolean>() {
        //            @Override
        //            public void onChanged(@Nullable Boolean aBoolean) {
        //
        //            }
        //        };

        val soal = rxSharedPreferences.getBoolean("soal$position", false)

        soal.asObservable().subscribe(object : io.reactivex.Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onNext(aBoolean: Boolean?) {
                if (aBoolean == true) {
                    holder.tvQuestionNumber.setBackgroundColor(Color.BLUE)
                } else {
                    holder.tvQuestionNumber.setBackgroundColor(Color.RED)
                }
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }
        })
        // if(soal){
        //                holder.tvQuestionNumber.setBackgroundColor(Color.BLUE);
        //            }else{
        //                holder.tvQuestionNumber.setBackgroundColor(Color.RED);
        //            }

    }

    override fun getItemCount(): Int {
        return listNo.size
    }

    //    public int posisiSoal(){
    //        return noSoalTerpilih;
    //    }


}
