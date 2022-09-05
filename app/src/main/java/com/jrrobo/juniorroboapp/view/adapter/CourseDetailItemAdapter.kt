package com.jrrobo.juniorroboapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jrrobo.juniorroboapp.databinding.CourseDetailHighlightListItemBinding

class CourseDetailItemAdapter(val list: List<String>): RecyclerView.Adapter<CourseDetailItemAdapter.CourseDetailViewHolder>() {
    inner class CourseDetailViewHolder(private val binding : CourseDetailHighlightListItemBinding) :
       RecyclerView.ViewHolder(binding.root){
            fun bind(item: String){
                binding.highlightsTextView.text = item
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseDetailViewHolder {
        val binding =CourseDetailHighlightListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  CourseDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseDetailViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}