package com.jrrobo.juniorroboapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jrrobo.juniorroboapp.data.course.CourseGradeListItem
import com.jrrobo.juniorroboapp.databinding.CourseGradeListItemBinding
import com.jrrobo.juniorroboapp.network.EndPoints

class CourseGradeListItemAdapter(private val listener: (CourseGradeListItem)->Unit) : ListAdapter<CourseGradeListItem, CourseGradeListItemAdapter.ViewHolder>(CourseGradeDiffCallback()){
    inner class ViewHolder(private val binding: CourseGradeListItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.invoke(getItem(bindingAdapterPosition))
            }
        }

        fun bind(courseGradeListItem: CourseGradeListItem){
            binding.apply {
                courseGradeListItemTitle.text = courseGradeListItem.title
                Glide.with(this.root)
                    .load(EndPoints.GET_IMAGE + "/course/" + courseGradeListItem.image)
                    .into(this.courseGradeListItemImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CourseGradeListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class CourseGradeDiffCallback: DiffUtil.ItemCallback<CourseGradeListItem>(){
    override fun areItemsTheSame(oldItem: CourseGradeListItem, newItem: CourseGradeListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseGradeListItem, newItem: CourseGradeListItem): Boolean {
        return oldItem == newItem
    }
}