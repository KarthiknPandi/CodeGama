package com.example.newsapp.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.FragmentProfileBinding
import com.example.newsapp.ui.auth.LoginActivity
import com.example.newsapp.utils.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var prefManager: PrefManager? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.btnUpdatePhoto.setOnClickListener {
            showImagePickerDialog()
        }

        loadGoogleProfile()
        loadStoredProfileImage()
        fetchLocation()

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    prefManager?.saveProfileImageUri(requireContext(), uri.toString())
                    Glide.with(this).load(uri).into(binding.imageProfile)
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadGoogleProfile() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            binding.textName.text = user.displayName ?: "No Name"
            binding.textEmail.text = user.email ?: "No Email"
        } else {
            binding.textName.text = "Guest"
            binding.textEmail.text = "Not Email"
        }
    }


    private fun loadStoredProfileImage() {
        val imageUri = prefManager?.getProfileImageUri(requireContext())
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .into(binding.imageProfile)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    binding.textLatitude.text = "Latitude: ${location.latitude}"
                    binding.textLongitude.text = "Longitude: ${location.longitude}"
                } else {
                    binding.textLatitude.text = "Latitude: Not available"
                    binding.textLongitude.text = "Longitude: Not available"
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(LOCATION_PERMISSION), 100)
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> checkGalleryPermission()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA_PERMISSION), REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestPermissions(arrayOf(permission), REQUEST_IMAGE_PICK)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    val uri = saveImageToInternalStorage(photo)
                    updateProfileImage(uri)
                    prefManager?.saveProfileImageUri(requireContext(), uri.toString())
                }
                REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        updateProfileImage(imageUri)
                    }
                }
            }
        }
    }

    private fun updateProfileImage(uri: Uri) {
        binding.imageProfile.setImageURI(uri)
        viewModel.updateProfileImage(uri)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val filename = "profile_picture_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, filename)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return Uri.fromFile(file)
    }
}
