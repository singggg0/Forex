package com.project.samsin.forex.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.project.samsin.forex.databinding.ViewTextUnderIconBinding

class TextUnderIconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    val binding: ViewTextUnderIconBinding

    init {
        binding = ViewTextUnderIconBinding.inflate(LayoutInflater.from(context), this, true)
    }
}