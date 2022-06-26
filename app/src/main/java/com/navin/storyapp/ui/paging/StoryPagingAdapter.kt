package com.navin.storyapp.ui.paging

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.navin.storyapp.R
import com.navin.storyapp.api.StoryResponse
import com.navin.storyapp.databinding.ItemStoryBinding
import com.navin.storyapp.helper.DateFormatter
import com.navin.storyapp.model.StoryModel
import com.navin.storyapp.ui.main.MainActivity
import com.navin.storyapp.ui.story.DetailStoryActivity
import java.util.*

class StoryPagingAdapter :
    PagingDataAdapter<StoryResponse.Story, StoryPagingAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(story: StoryResponse.Story) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.ic_baseline_photo_24)
                    .error(R.drawable.ic_baseline_photo_24)
                    .into(ivPhoto)

                tvUsername.text = story.name
                tvDes.text = story.description
                tvPost.text = DateFormatter.formatDate(story.createdAt, TimeZone.getDefault().id)


                itemView.setOnClickListener {
                    val storyModel = StoryModel()
                    storyModel.name = story.name
                    storyModel.description = story.description
                    storyModel.photoUrl = story.photoUrl
                    storyModel.createdAt =
                        DateFormatter.formatDate(story.createdAt, TimeZone.getDefault().id)

                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(MainActivity.KEY_STORY, storyModel)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            androidx.core.util.Pair(tvUsername, "name"),
                            androidx.core.util.Pair(tvDes, "description"),
                            androidx.core.util.Pair(ivPhoto,"photo")
                        )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponse.Story>() {
            override fun areItemsTheSame(
                oldItem: StoryResponse.Story,
                newItem: StoryResponse.Story
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryResponse.Story,
                newItem: StoryResponse.Story
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}