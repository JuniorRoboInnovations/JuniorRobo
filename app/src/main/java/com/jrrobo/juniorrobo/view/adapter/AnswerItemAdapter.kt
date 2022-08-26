package com.jrrobo.juniorrobo.view.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.jrrobo.juniorrobo.R
import com.jrrobo.juniorrobo.data.answer.AnswerItem
import com.jrrobo.juniorrobo.databinding.AnswerItemBinding
import com.jrrobo.juniorrobo.network.EndPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.http.Url

class AnswerItemAdapter(val list: List<AnswerItem>): RecyclerView.Adapter<AnswerItemAdapter.AnswerViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding =
            AnswerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(list[position])

//        //Animation of Answer recycler view
//        holder.itemView.startAnimation(AnimationUtils
//            .loadAnimation(holder.itemView.context,R.anim.anim_one))
    }

    inner class AnswerViewHolder(
        private val binding: AnswerItemBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(item: AnswerItem){

            val imageString = EndPoints.GET_IMAGE + "/answer/" +item.answer_image

            binding.answerText.text = item.answer

            if (item.answer_image.isNullOrEmpty()) {
                binding.answerImage.visibility = View.GONE
            }
            else {
                binding.answerImage.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(imageString)
                        .into(binding.answerImage)
                }
            }

            binding.nameTextView.text = item.student

            if (item.student_image.isNullOrEmpty()) {
                binding.userImage.visibility = View.GONE
            }
            else {
                binding.userImage.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(EndPoints.GET_IMAGE + "/student/" +item.student_image)
                        .error(R.drawable.ic_default_avatar)
                        .into(binding.userImage)
                }
            }

            binding.answerImage.setOnClickListener {

                var dialogImagePreview: AlertDialog? = null

                val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
                val customLayout: View = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.answerimage_layout_dialog, null)

                val imageView = customLayout.findViewById<ImageView>(R.id.answerImageView)
                GlobalScope.launch(Dispatchers.Main) {
                    Glide.with(binding.root)
                        .load(imageString)
                        .into(imageView)
                }
                builder.setView(customLayout)

                val cancelButton = customLayout.findViewById<ImageView>(R.id.cancelImageView)
                cancelButton.setOnClickListener {
                    dialogImagePreview?.dismiss()
                }

                dialogImagePreview = builder.create()

                dialogImagePreview.show()
            }

            binding.answerRating.rating = item.id.toFloat()

        }




    }


}