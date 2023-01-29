package com.android.ngram_distance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.ngram_distance.databinding.ActivityMainBinding
import com.lion.ngram_string.NGram

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.btnCheck.setOnClickListener {
            checkDistance()
        }
    }

    private fun checkDistance() {
        val nGram = NGram(1)
        val distance = nGram.distance(
            binding.edtSource.text.toString().uppercase(),
            binding.edtTarget.text.toString().uppercase()
        )
        val similarity = (1 - distance) * 100
        binding.txtDistance.text = "$similarity %"
    }


}