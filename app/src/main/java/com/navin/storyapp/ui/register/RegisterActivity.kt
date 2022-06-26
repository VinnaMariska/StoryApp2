package com.navin.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.navin.storyapp.R
import com.navin.storyapp.databinding.ActivityRegisterBinding
import com.navin.storyapp.ui.login.LoginActivity
import com.navin.storyapp.ui.story.isValidEmail


class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
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

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()

                    registerViewModel.registerUser(name, email, password)

                    registerViewModel.isLoading.observe(this) {
                        it.getContentIfNotHandled()?.let { state ->
                            isLoading(state)
                        }
                    }

                    registerViewModel.isFailed.observe(this) {
                        it.getContentIfNotHandled()?.let {
                            isFailed()
                        }
                    }

                    registerViewModel.isRegisterSuccess.observe(this) {
                        it.getContentIfNotHandled()?.let { state ->
                            isRegisterSuccess(state)
                        }
                    }
                }

        binding.etSignin.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setMyButtonEnable() {
        val resultPass = binding.etPass.text
        val resultEmail = binding.etEmail.text

        binding.btnRegister.isEnabled = resultPass != null && resultEmail != null &&
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

        val textReg = ObjectAnimator.ofFloat(binding.textReg, View.ALPHA, 1f).setDuration(500)
        val etName = ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.etPass, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val textRegister = ObjectAnimator.ofFloat(binding.etSignin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                textReg,
                etName,
                etEmail,
                etPassword,
                btnRegister,
                textRegister
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

    private fun isRegisterSuccess(success: Boolean) {
        if (success) {
            AlertDialog.Builder(this).apply {
                setCancelable(false)
                setTitle(getString(R.string.title_info))
                setMessage(getString(R.string.message_registersuccess))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
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
                setCancelable(false)
                setTitle(getString(R.string.title_info))
                setMessage(getString(R.string.message_already_registered))
                setPositiveButton(getString(R.string.continue_)) { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        }
    }
}