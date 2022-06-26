package com.navin.storyapp.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.navin.storyapp.R
import com.navin.storyapp.ViewModelFactory
import com.navin.storyapp.databinding.ActivityAddStoryBinding
import com.navin.storyapp.model.UserPreference
import com.navin.storyapp.ui.main.MainActivity
import com.navin.storyapp.ui.main.MainViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var getFile: File? = null
    private var result: Bitmap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Float? = null
    private var lon: Float? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!cameraPermissionsGranted()) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_permission_denied))
                    setMessage(getString(R.string.message_camera_denied))
                    setPositiveButton(getString(R.string.ok_)) { dialog, _ -> dialog.dismiss() }
                    create()
                    show()
                }
            }
            if (cameraPermissionsGranted()) {
                startCameraX()
            }
        }
    }

    private fun cameraPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()

        val resultCode = intent.getIntExtra("resultCode", 0)
        if (resultCode == GALLERY_RESULT) {
            setImageFromGallery()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCameraX.setOnClickListener { accessCamera() }
        binding.btnGallery.setOnClickListener { startGalleryAndCrop() }
        binding.btnUpload.setOnClickListener { uploadStory() }
        binding.switch2.setOnClickListener { getMyLocation() }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        addStoryViewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
    }


    private fun accessCamera() {
        if (!cameraPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            startCameraX()
        }
    }


    private fun uploadStory() {
        val description = binding.etDescription.text.toString()

        when {
            getFile == null -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_warning))
                    setMessage(getString(R.string.message_photo))
                    setPositiveButton(getString(R.string.ok_)) { dialog, _ -> dialog.dismiss() }
                    create()
                    show()
                }
            }
            description.isEmpty() -> {
                binding.etDescription.error = getString(R.string.message_desc)
            }
            else -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_info))
                    setMessage(getString(R.string.message_upload))
                    setPositiveButton(getString(R.string.yes_)) { _, _ ->
                        val file = reduceFileImage(getFile as File)

                        val partDescription = description.toRequestBody("text/plain".toMediaType())
                        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            requestImageFile
                        )

                        mainViewModel.getUser().observe(this@AddStoryActivity) { user ->
                            addStoryViewModel.uploadStory(
                                user.token,
                                imageMultipart,
                                partDescription,
                                lat, lon
                            )
                        }

                        addStoryViewModel.isLoading.observe(this@AddStoryActivity) {
                            it.getContentIfNotHandled()?.let { state ->
                                isLoading(state)
                            }
                        }

                        addStoryViewModel.isFailed.observe(this@AddStoryActivity) {
                            it.getContentIfNotHandled()?.let {
                                isFailed()
                            }
                        }

                        addStoryViewModel.isUploadSuccess.observe(this@AddStoryActivity) {
                            it.getContentIfNotHandled()?.let {
                                isUploadSuccess()
                            }
                        }
                    }
                    setNegativeButton(getString(R.string.cancel_)) { dialog, _ -> dialog.cancel() }
                    create()
                    show()
                }
            }
        }
    }

    private fun startCameraX() {
        launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            result =
                rotateBitmap(
                    BitmapFactory.decodeFile(getFile?.path),
                    isBackCamera
                )
        }
        binding.ivPreview.setImageBitmap(result)
    }

    private fun startGalleryAndCrop() {
        launcherCropImage.launch(
            options {
                setImageSource(
                    includeCamera = false, includeGallery = true
                )
                setAspectRatio(aspectRatioX = 1, aspectRatioY = 1)
                setAllowRotation(allowRotation = true)
                setAllowFlipping(allowFlipping = true)
            }
        )
    }


    private val launcherCropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriImageCrop = result.uriContent

            val intent = Intent(this@AddStoryActivity, AddStoryActivity::class.java)
            intent.putExtra("uri", uriImageCrop)
            intent.putExtra("resultCode", AddStoryActivity.GALLERY_RESULT)
            startActivity(intent)
            finish()
        }
    }

    private fun setImageFromGallery() {
        val selectedImg = intent.getParcelableExtra<Uri>("uri") as Uri

        val myFile = uriToFile(selectedImg, this)
        getFile = myFile

        binding.ivPreview.setImageURI(selectedImg)
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                    Toast.makeText(
                        this,
                        getString(R.string.location_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION)
            (Manifest.permission.ACCESS_COARSE_LOCATION)

        }
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

    private fun isFailed() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_warning))
            setMessage(getString(R.string.message_conection))
            setPositiveButton(getString(R.string.ok_)) { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun isUploadSuccess() {
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(getString(R.string.title_info))
            setMessage(getString(R.string.message_story))
            setPositiveButton(getString(R.string.ok_)) { _, _ -> finish() }
            create()
            show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val GALLERY_RESULT = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}