package com.navin.storyapp.ui.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.navin.storyapp.R
import com.navin.storyapp.databinding.ActivityDetailBinding
import com.navin.storyapp.model.StoryModel
import com.navin.storyapp.ui.main.MainActivity

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupData()
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<StoryModel>(MainActivity.KEY_STORY) as StoryModel
        with(binding){
            tvName.text = story.name
            tvDescription.text = story.description
            binding.tvPost.text = story.createdAt
        }


        Glide.with(applicationContext)
            .load(story.photoUrl)
            .error(R.drawable.ic_baseline_photo_24)
            .into(binding.imagePhoto)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}