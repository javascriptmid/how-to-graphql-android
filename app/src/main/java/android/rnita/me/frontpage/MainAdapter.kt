package android.rnita.me.frontpage

import android.rnita.me.frontpage.databinding.PostContentBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MainAdapter : RecyclerView.Adapter<MainAdapter.PostContentViewHolder>() {

    val feed = mutableListOf<FeedQuery.Link>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostContentViewHolder =
        PostContentViewHolder(PostContentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = feed.size

    override fun onBindViewHolder(holder: PostContentViewHolder, position: Int) {
        val link = feed[position]
        holder.binding.title.text = link.description
        holder.binding.name.text = "by ${link.postedBy?.name}"
        holder.binding.vote.text = "${link.votes.size} votes"
    }

    inner class PostContentViewHolder(val binding: PostContentBinding) : RecyclerView.ViewHolder(binding.root)
}