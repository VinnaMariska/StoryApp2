package com.navin.storyapp.ui.main

import androidx.activity.viewModels
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.navin.storyapp.ViewModelFactory
import com.navin.storyapp.model.UserPreference
import com.navin.storyapp.ui.story.AddStoryActivity
import com.navin.storyapp.ui.login.LoginActivity
import com.navin.storyapp.ui.splash.SplashViewModel
import com.navin.storyapp.R
import com.navin.storyapp.databinding.ActivityMainBinding
import com.navin.storyapp.ui.maps.MapsActivity
import com.navin.storyapp.ui.paging.LoadingStateAdapter
import com.navin.storyapp.ui.paging.StoryPagingAdapter
import com.navin.storyapp.ui.paging.StoryPagingViewModel
import com.navin.storyapp.ui.paging.ViewModelFactoryStoryPaging

class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryPagingAdapter
    private lateinit var storyPagingViewModel: StoryPagingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.setHasFixedSize(true)
        storyAdapter = StoryPagingAdapter()
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        setupViewModel()

        binding.addStory.setOnClickListener { view ->
            val intent = Intent(this, AddStoryActivity::class.java)

            startActivity(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
            )
        }
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                storyPagingViewModel = ViewModelProvider(
                    this,
                    ViewModelFactoryStoryPaging(user.token)
                )[StoryPagingViewModel::class.java]
                setStoryList()
                storyAdapter.refresh()
            }
        }
    }


    private fun setStoryList() {
        storyPagingViewModel.story.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun Logout() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_info))
            setMessage(getString(R.string.message_logout))
            setPositiveButton(getString(R.string.yes_)) { _, _ ->
                mainViewModel.logout()
            }
            setNegativeButton(getString(R.string.cancel_)) { dialog, _ -> dialog.cancel() }
            create()
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.languange -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                Logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar4.visibility = View.VISIBLE
            binding.rvStory.visibility = View.INVISIBLE
        } else {
            binding.progressBar4.visibility = View.INVISIBLE
            binding.rvStory.visibility = View.VISIBLE
        }
    }

    companion object {
        const val KEY_STORY = "story"
    }
}