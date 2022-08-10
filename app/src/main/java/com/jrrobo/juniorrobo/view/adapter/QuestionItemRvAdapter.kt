package com.jrrobo.juniorrobo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.QuestionItemBinding
import com.jrrobo.juniorrobo.network.EndPoints

class QuestionItemRvAdapter(private val listener:(QuestionItem)->Unit) : ListAdapter<QuestionItem, QuestionItemRvAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: QuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.textButtonQuestionItemAnswer.setOnClickListener{
                listener.invoke(getItem(bindingAdapterPosition))
            }

        }
        fun bind(item: QuestionItem){
            binding.apply {
                textViewQuestionItemQuestion.text = item.question
                textViewQuestionItemDescription.text = item.question_sub_text
//                textViewQuestionItemStudentName.text = item.id.toString()
            }
            item.image.let {
                binding.textViewQuestionItemImageLabel.visibility = View.VISIBLE
                binding.textViewQuestionItemQuestion.visibility = View.VISIBLE

                Glide.with(binding.root)
                    .load(EndPoints.GET_IMAGE + "/question/" + item.image)
                    .error(R.drawable.ic_image_black)
                    .into(binding.imageViewQuestionItemImage)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuestionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffCallback: DiffUtil.ItemCallback<QuestionItem>(){
    override fun areItemsTheSame(oldItem: QuestionItem, newItem: QuestionItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: QuestionItem, newItem: QuestionItem): Boolean {
        return oldItem == newItem
    }
}
