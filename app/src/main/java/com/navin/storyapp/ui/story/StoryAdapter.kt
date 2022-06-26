package com.navin.storyapp.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.navin.storyapp.R
import com.navin.storyapp.api.StoryResponse
import com.navin.storyapp.databinding.ItemStoryBinding
import com.navin.storyapp.model.StoryModel
import com.navin.storyapp.ui.main.MainActivity
import java.util.ArrayList

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val listStory = ArrayList<StoryResponse.Story>()

    fun setListStory(listStory: List<StoryResponse.Story>) {
        this.listStory.clear()
        this.listStory.addAll(listStory)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryResponse.Story) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.ic_baseline_photo_24)
                    .error(R.drawable.ic_baseline_photo_24)
                    .into(ivPhoto)

                tvUsername.text = story.name
                tvDes.text = story.description


                itemView.setOnClickListener {
                    val storyModel = StoryModel()
                    storyModel.name = story.name
                    storyModel.description = story.description
                    storyModel.photoUrl = story.photoUrl

                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(MainActivity.KEY_STORY, storyModel)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(tvUsername, "name"),
                            Pair(tvDes, "description"),
                            Pair(ivPhoto, "photo")
                        )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }
}