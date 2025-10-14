package com.example.flickrview.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil3.load
import coil3.request.crossfade
import com.example.flickrview.databinding.FragmentPhotoDetailBinding
import com.example.flickrview.domain.model.Photo

class PhotoDetailFragment : Fragment() {

    private var _binding: FragmentPhotoDetailBinding? = null
    private val binding get() = _binding!!

    private var photo: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            arguments?.getParcelable(ARG_PHOTO, Photo::class.java)
        } else {
            // Android 12 и ниже
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_PHOTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photo?.let { setupUI(it) } ?: parentFragmentManager.popBackStack()
    }

    private fun setupUI(photo: Photo) {
        // Настраиваем toolbar
        binding.toolbar.title = photo.title
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Загружаем большое изображение с Coil
        binding.imageView.load(photo.largeImageUrl) {
            crossfade(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PHOTO = "photo"

        fun newInstance(photo: Photo) = PhotoDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_PHOTO, photo)
            }
        }
    }
}