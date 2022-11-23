package com.project.samsin.forex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.project.samsin.forex.databinding.ActivityMainBinding
import com.project.samsin.forex.watchlist.WatchlistFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            supportFragmentManager.beginTransaction().replace(fragmentContainer.id, WatchlistFragment()).commit()
        }
        setupBottomBar()
    }

    private fun setupBottomBar() {
        with(binding) {
            listOf(tuicItem1, tuicItem2, tuicItem3, tuicItem4).forEach {
                it.binding.tvText.text = when (it) {
                    tuicItem1 -> "Market"
                    else -> "Portfolio"
                }
                it.binding.tvText.setTextColor(when (it) {
                    tuicItem1 -> ContextCompat.getColor(this@MainActivity, R.color.white)
                    else -> ContextCompat.getColor(this@MainActivity, R.color.text1)
                })
            }
        }
    }
}