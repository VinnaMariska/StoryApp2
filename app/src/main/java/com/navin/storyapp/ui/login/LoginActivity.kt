package com.navin.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.navin.storyapp.R
import com.navin.storyapp.ViewModelFactory
import com.navin.storyapp.databinding.ActivityLoginBinding
import com.navin.storyapp.model.UserPreference
import com.navin.storyapp.ui.main.MainActivity
import com.navin.storyapp.ui.register.RegisterActivity
import com.navin.storyapp.ui.story.isValidEmail
import com.navin.storyapp.ui.story.isValidPass


class LoginActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAnimation()
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.etPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()

                    loginViewModel.login(email, password)

                    loginViewModel.isLoading.observe(this) {
                        it.getContentIfNotHandled()?.let { state ->
                            isLoading(state)
                        }
                    }

                    loginViewModel.isFailed.observe(this) {
                        it.getContentIfNotHandled()?.let {
                            isFailed()
                        }
                    }

                    loginViewModel.isSuccess.observe(this) {
                        it.getContentIfNotHandled()?.let { state ->
                            isSuccess(state)
                        }
                    }
                }


        binding.etLogin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setMyButtonEnable() {
        val resultPass = binding.etPass.text
        val resultEmail = binding.etEmail.text

        binding.btnLogin.isEnabled = resultPass != null && resultEmail != null &&
                binding.etPass.text.toString().length >= 6 &&
                isValidEmail(binding.etEmail.text.toString())
    }


    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun setupAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val textLog = ObjectAnimator.ofFloat(binding.textLog, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.etPass, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val textLogin = ObjectAnimator.ofFloat(binding.etLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                textLog,
                etEmail,
                etPassword,
                btnLogin,
                textLogin
            )
            startDelay = 500
        }.start()
    }

    private fun isFailed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_warning))
            setMessage(getString(R.string.message_conection))
            setPositiveButton(getString(R.string.ok_)) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun isSuccess(success: Boolean) {
        if (success) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_info))
                setMessage(getString(R.string.message_login_success))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }

        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_warning))
                setMessage(getString(R.string.message_emailpass_invalid))
                setPositiveButton(getString(R.string.ok_)) { dialog, _ -> dialog.dismiss() }
                create()
                show()
            }
        }
    }
}