package com.jrrobo.juniorrobo.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jrrobo.juniorrobo.data.questionitem.QuestionItem
import com.jrrobo.juniorrobo.databinding.QuestionItemBinding

class QuestionItemAdapter(
    private val questionItemClickListener: OnQuestionItemClickListener
) : PagingDataAdapter<QuestionItem, QuestionItemAdapter.QuestionViewHolder>(QUESTION_COMPARATOR) {

    private val TAG: String = javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding =
            QuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class QuestionViewHolder(
        private val binding: QuestionItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.textButtonQuestionItemAnswer.setOnClickListener {
                val position = bindingAdapterPosition
                Log.d(TAG, position.toString())
                if (position != RecyclerView.NO_POSITION) {
                    val questionItem = getItem(position)
                    if (questionItem != null) {
                        Log.d(TAG, questionItem.question)
                        questionItemClickListener.onItemClick(questionItem)
                    }
                }
            }
        }

        fun bind(currentItem: QuestionItem?) {
            binding.apply {
                textViewQuestionItemQuestion.text = currentItem?.question
                textViewQuestionItemDescription.text = currentItem?.questionSubtext
                textViewQuestionItemStudentName.text = currentItem?.pkQuestionId.toString()
            }
        }
    }

    interface OnQuestionItemClickListener {
        fun onItemClick(questionItem: QuestionItem)
    }

    companion object {
        private val QUESTION_COMPARATOR = object : DiffUtil.ItemCallback<QuestionItem>() {
            override fun areItemsTheSame(oldItem: QuestionItem, newItem: QuestionItem): Boolean =
                oldItem.pkQuestionId == newItem.pkQuestionId

            override fun areContentsTheSame(oldItem: QuestionItem, newItem: QuestionItem): Boolean =
                oldItem == newItem
        }
    }
}