package com.jrrobo.juniorroboapp.view.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jrrobo.juniorroboapp.databinding.ChaptersListItemBinding

class RecordedChaptersRvAdapter  (private val list: List<String>):
    RecyclerView.Adapter<RecordedChaptersRvAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ChaptersListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String){
            binding.apply {
                chapterName.text = item
                chaptersListCard.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setPackage("com.google.android.youtube")
                    ContextCompat.startActivity(itemView.context, intent, null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordedChaptersRvAdapter.ViewHolder {
        val binding =
            ChaptersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()= list.size
}