package com.example.flickrview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.flickrview.databinding.ActivityMainBinding
import com.example.flickrview.presentation.PhotoListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Показываем список фото при первом запуске
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PhotoListFragment())
                .commit()
        }
    }
}