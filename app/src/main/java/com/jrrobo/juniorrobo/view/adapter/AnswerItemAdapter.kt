package com.jrrobo.juniorrobo.view.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.databinding.AnswerItemBinding
import com.jrrobo.juniorrobo.network.EndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AnswerItemAdapter(val list: List<AnswerItem>): RecyclerView.Adapter<AnswerItemAdapter.AnswerViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding =
            AnswerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(list[position])

        //Animation of Answer recycler view
        holder.itemView.startAnimation(AnimationUtils
            .loadAnimation(holder.itemView.context,R.anim.anim_one))
    }

    inner class AnswerViewHolder(
        private val binding: AnswerItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: AnswerItem){

            binding.answerText.text = item.answer
            if (item.student_image.isNullOrEmpty()) {
                binding.answerImage.visibility = View.GONE
            }
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    Log.d(TAG, "populateAnswer: Glide called")
                    Glide.with(binding.root)
                        .load(EndPoints.GET_IMAGE + item.student_image)
                        .error(R.drawable.ic_baseline_file_copy_24)
                        .into(binding.answerImage)
                }
            }
        }
    }

}