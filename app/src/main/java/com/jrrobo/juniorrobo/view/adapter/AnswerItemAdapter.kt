package com.jrrobo.juniorrobo.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.data.profile.StudentProfileData
import com.jrrobo.juniorrobo.databinding.AnswerItemBinding
import com.jrrobo.juniorrobo.network.EndPoints

class AnswerItemAdapter(val list: List<AnswerItem>): RecyclerView.Adapter<AnswerItemAdapter.AnswerViewHolder>() {

    //private val studentImage = EndPoints.GET_IMAGE + StudentProfileData.UserImage

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding =
            AnswerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class AnswerViewHolder(
        private val binding: AnswerItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(answerItem: AnswerItem){

            binding.answerText.text = answerItem.answer
       /*     binding.apply {
                Glide.with(binding.root)
                        .load(EndPoints.GET_IMAGE + studentProfileData.UserImage)
                        .error(R.drawable.ic_default_avatar)
                        .into(binding.answerImage)
                }*/
            }
        }
    }