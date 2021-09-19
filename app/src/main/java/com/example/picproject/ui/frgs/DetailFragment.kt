package com.example.picproject.ui.frgs

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.picproject.R
import com.example.picproject.SaveImage
import com.example.picproject.calculate
import com.example.picproject.data.Resource
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.databinding.DetailsFragmentBinding
import com.example.picproject.db.PhotoDao
import com.example.picproject.ui.DEFAULT_LIST_KEY
import com.example.picproject.ui.DEFAULT_LIST_TYPE
import com.example.picproject.ui.vm.DetailViewModel
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.details_fragment.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.details_fragment) {

    @Inject
    lateinit var photoDao: PhotoDao

    private val handler = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Main).launch {
            Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "error in download image: ${throwable.localizedMessage}")
        }
    }

    private val viewModel by viewModels<DetailViewModel>()

    private val args: DetailFragmentArgs by navArgs()

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private var isSaved = false

    private var bitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = DetailsFragmentBinding.bind(view)

        val photo = args.photo

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics);
        val width = displayMetrics.widthPixels

        val ratio = (photo.height.toDouble() / photo.width.toDouble())
        val height = (width * ratio).roundToInt()

        viewModel.getPhotoFromDB(photo.id).observe(viewLifecycleOwner, {
            if (it == null) {
                isSaved = false
                binding.saveImage.setImageResource(R.drawable.ic_bookmark_border)
            } else {
                isSaved = true
                binding.saveImage.setImageResource(R.drawable.ic_bookmark)
            }
        })

        binding.apply {
            setDimensions(mainImageView, width, height)

            // Profile pic
            Glide
                .with(view)
                .load(photo.user.profile_image.small)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(user_profile)

            // Main photo
            Glide
                .with(view)
                .asBitmap()
                .load(photo.urls.regular)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                        mainImageView.setImageBitmap(bitmap)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.d(TAG, "onLoadCleared: ")
                    }

                })
            username_txt.text = photo.user.name

            save_image.setOnClickListener {
                if (!isSaved)
                    viewModel.savePhoto(photo)
                else
                    viewModel.removePhoto(photo.id)
            }

            instagramFeed.setOnClickListener {
                createInstagramIntent(getImageUri(bitmap!!))
            }

            userPart.setOnClickListener {
                DEFAULT_LIST_KEY = photo.user.username
                DEFAULT_LIST_TYPE = ListType.USER

                val action =
                    DetailFragmentDirections.actionDetailFragmentToListFragment(
                        photo.user.username,
                        ListType.USER, photo.user.name
                    )
                findNavController().navigate(action)
            }

        }

        viewModel.status.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandledOrReturnNull()?.let { event ->
                showToast(event)
            }
        })

        download_photo.setOnClickListener {
            Permissions.check(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        selectSizeDialog(view, photo)
                    }
                })
        }

        set_wallpaper.setOnClickListener {

        }

        viewModel.getPhotoByID(photo.id).observe(viewLifecycleOwner, { pic ->
            when (pic) {
                is Resource.Success -> {
                    if (pic.data != null) {
                        binding.apply {
                            moreInfoSection.visibility = View.VISIBLE

                            visibilityManager(
                                value = pic.data.description,
                                views = arrayOf(lineAboveCamera, descTextview)
                            )

                            descTextview.text = pic.data.description

                            views.text = calculateNumbers(pic.data.views)
                            likes.text = calculateNumbers(pic.data.likes)
                            downloads.text = calculateNumbers(pic.data.downloads)

                            resolution.text = "(${pic.data.width}*${pic.data.height})"

                            visibilityManager(pic.data.exif.make, makeSection)
                            visibilityManager(pic.data.exif.model, modelSection)
                            visibilityManager(pic.data.exif.exposure_time, exposureSection)
                            visibilityManager(pic.data.exif.aperture, apertureSection)
                            visibilityManager(pic.data.exif.focal_length, focalSection)
                            visibilityManager(pic.data.exif.iso.toString(), isoSection)

                            make.text = pic.data.exif.make
                            model.text = pic.data.exif.model
                            exposureTime.text = pic.data.exif.exposure_time
                            aperture.text = pic.data.exif.aperture
                            focalLength.text = pic.data.exif.focal_length
                            iso.text = pic.data.exif.iso.toString()

                            visibilityManager(pic.data.location.name, nameSection)
                            visibilityManager(pic.data.location.city, citySection)
                            visibilityManager(pic.data.location.country, countrySection)
                            visibilityManager(pic.data.location.position.latitude.toString(), map)

                            if (pic.data.location.name.isNullOrEmpty()
                                && pic.data.location.city.isNullOrEmpty()
                                && pic.data.location.country.isNullOrEmpty()
                            ) {
                                lineAboveLocation.visibility = View.GONE
                                locationSection.visibility = View.GONE
                                map.visibility = View.GONE
                            }

                            name.text = pic.data.location.name
                            city.text = pic.data.location.city
                            country.text = pic.data.location.country

                            map.setOnClickListener {
                                openGMaps(
                                    pic.data.location.position.latitude,
                                    pic.data.location.position.longitude
                                )
                            }

                        }
                    }
                }
            }
        })
    }

    private fun selectSizeDialog(view: View, photo: UnsplashPhoto) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Size:")
        val sizes = arrayOf(
            "Thumb",
            "Small",
            "Regular",
            "Full   ",
            "Raw (${photo.width}*${photo.height})"
        )

        builder.setItems(sizes) { dialog, which ->
            when (which) {
                0 -> {
                    downloadImage(view, photo.id + "_thumb.jpg", photo.urls.thumb)
                    dialog.dismiss()
                }
                1 -> {
                    downloadImage(view, photo.id + "_small.jpg", photo.urls.small)
                    dialog.dismiss()
                }
                2 -> {
                    downloadImage(view, photo.id + "_regular.jpg", photo.urls.regular)
                    dialog.dismiss()
                }
                3 -> {
                    downloadImage(view, photo.id + "_full.jpg", photo.urls.full)
                    dialog.dismiss()
                }
                4 -> {
                    downloadImage(view, photo.id + "_raw.jpg", photo.urls.raw)
                    dialog.dismiss()
                }
            }
        }

        val dialog = builder.create()
        dialog.show()

    }

    private fun downloadImage(view: View, title: String, link: String) {
        CoroutineScope(IO + handler).launch {
            try {
                val url = URL(link)
                val image: Bitmap =
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                SaveImage().saveImageToStorage(
                    requireContext(),
                    image,
                    title,
                    "image/jpeg"
                )

            } catch (e: IOException) {
                Log.d(TAG, "error: ${e.localizedMessage}")
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun createInstagramIntent(uri: Uri) {
        // Create the new Intent using the 'Send' action.
        val share = Intent(Intent.ACTION_SEND)

        // Set the MIME type
        share.type = "image/*"


        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri)

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"))
    }

    private fun getImageUri(src: Bitmap): Uri {
        val os = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, os)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            src,
            "title",
            null
        )
        return Uri.parse(path)
    }

    private fun setDimensions(view: View, width: Int, height: Int) {
        val params = view.layoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
    }

    private fun calculateNumbers(number: Int): String {
        var out = ""
        when (number) {
            in 0..999 -> out = number.toString()
            in 1000..999_999 -> out = (number / 1000.0).calculate() + "K"
            in 1_000_000..999_999_999 -> out = (number / 1_000_000.0).calculate() + "M"
            in 1_000_000_000..999_999_999_999 -> out =
                (number / 1_000_000_000.0).calculate() + "B"
        }
        return out
    }

    private fun visibilityManager(value: String, vararg views: View) {
        for (v in views) {
            if (value.isNullOrEmpty())
                v.visibility = View.GONE
            else {
                if (value != "0")
                    v.visibility = View.VISIBLE
                else
                    v.visibility = View.GONE
            }
        }
    }

    private fun openGMaps(lat: Double, lon: Double) {
        val gmmIntentUri = Uri.parse("geo:$lat,$lon")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        mapIntent.resolveActivity(requireContext().packageManager)?.let {
        }
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }
}