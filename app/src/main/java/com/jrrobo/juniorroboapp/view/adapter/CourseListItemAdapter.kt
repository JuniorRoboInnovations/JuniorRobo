package com.jrrobo.juniorroboapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorroboapp.data.course.CourseListItem
import com.jrrobo.juniorroboapp.databinding.CourseListItemBinding
import com.jrrobo.juniorroboapp.network.EndPoints

class CourseListItemAdapter(private val listener: (CourseListItem)->Unit) : ListAdapter<CourseListItem, CourseListItemAdapter.ViewHolder>(CourseCategoryDiffCallback()){
    inner class ViewHolder(private val binding: CourseListItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.invoke(getItem(bindingAdapterPosition))
            }
        }

        fun bind(courseCategoryItem: CourseListItem){
            binding.apply {
                courseTitle.text = courseCategoryItem.title
                Glide.with(binding.root)
                    .load(EndPoints.GET_IMAGE + "/course/" + courseCategoryItem.image)
                    .into(binding.previewImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CourseListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class CourseCategoryDiffCallback: DiffUtil.ItemCallback<CourseListItem>(){
    override fun areItemsTheSame(oldItem: CourseListItem, newItem: CourseListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseListItem, newItem: CourseListItem): Boolean {
        return oldItem == newItem
    }
}