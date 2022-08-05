package com.jrrobo.juniorrobo.view.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.databinding.AnswerItemBinding
import com.jrrobo.juniorrobo.network.EndPoints
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
    }

    inner class AnswerViewHolder(
        private val binding: AnswerItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(item: AnswerItem){

            binding.answerText.text = item.answer
            GlobalScope.launch {

                Log.d(TAG, "populateAnswer: Glide called")
                Glide.with(binding.root)
                    .load(EndPoints.GET_IMAGE+ item.student_image)
                    .error(R.drawable.ic_baseline_file_copy_24)
                    .into(binding.answerImage)

            }
            binding.answerRating.rating = item.id.toFloat()
        }
    }

}