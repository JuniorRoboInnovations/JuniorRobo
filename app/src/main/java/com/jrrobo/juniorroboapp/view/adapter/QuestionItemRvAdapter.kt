package com.jrrobo.juniorroboapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorroboapp.R
import com.jrrobo.juniorroboapp.data.questionitem.QuestionItem
import com.jrrobo.juniorroboapp.databinding.QuestionItemBinding
import com.jrrobo.juniorroboapp.network.EndPoints

class QuestionItemRvAdapter(private val listener:(QuestionItem)->Unit) : ListAdapter<QuestionItem, QuestionItemRvAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: QuestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.invoke(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: QuestionItem){
            binding.apply {
                if(item.question == ""){
                    textViewQuestionItemQuestion.text = "Can you please answer this?"
                }
                else{
                    textViewQuestionItemQuestion.text = item.question
                }

                textViewQuestionItemDescription.text = item.question_sub_text
//                textViewQuestionItemStudentName.text = item.id.toString()
            }
            binding.imageViewQuestionItemImage.setImageDrawable(null)
            binding.imageViewQuestionItemImage.visibility = View.GONE

            if(item.image !=null) {
                binding.imageViewQuestionItemImage.visibility = View.VISIBLE


                Glide.with(binding.root)
                    .load(EndPoints.GET_IMAGE + "/question/" + item.image)
                    .error(R.drawable.ic_image_black)
                    .into(binding.imageViewQuestionItemImage)
            }

            /*
            binding.imageViewQuestionItemImage.setOnClickListener {

                var dialogImagePreview: AlertDialog? = null

                val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
                val customLayout: View = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.questionimage_layout_dialog, null)

                val imageView = customLayout.findViewById<ImageView>(R.id.questionImageView)
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(EndPoints.GET_IMAGE + "/question/" + item.image)
                        .into(imageView)
                }
                builder.setView(customLayout)

                builder.setPositiveButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        dialogImagePreview!!.dismiss()
                    }
                })
                dialogImagePreview = builder.create()

                dialogImagePreview.show()
            }*/
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
