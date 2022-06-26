package com.navin.storyapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.navin.storyapp.R
import com.navin.storyapp.databinding.ActivityLandingBinding
import com.navin.storyapp.ui.login.LoginActivity

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_info))
            setMessage(getString(R.string.message_logout))
            setPositiveButton(getString(R.string.yes_)) { _, _ ->
                super.onBackPressed()
                finish()
            }
            setNegativeButton(getString(R.string.cancel_)) { dialog, _ -> dialog.cancel() }
            create()
            show()
        }
    }
}