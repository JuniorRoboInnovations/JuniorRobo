package com.jrrobo.juniorroboapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jrrobo.juniorroboapp.databinding.SubjectsListItemBinding
import com.jrrobo.juniorroboapp.view.fragments.FragmentClassroomSubjectsDirections

class SubjectsRvAdapter (private val list: List<String>) :
RecyclerView.Adapter<SubjectsRvAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: SubjectsListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: String){
            binding.apply {
                subjectName.text = item
                subjectsListCard.setOnClickListener {
                    Navigation.findNavController(itemView).navigate(FragmentClassroomSubjectsDirections.actionFragmentClassroomSubjectsToFragmentClassroomChapters())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SubjectsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}