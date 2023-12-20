package com.example.compositions.presentation

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.compositions.R

@BindingAdapter("requiredAdapterss")
fun bindRequiredAnswers(textView2: TextView, count: Int) {
    textView2.text = String.format(
        textView2.context.getString(R.string.required_score),
        count
    )
}