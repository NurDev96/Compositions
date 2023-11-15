package com.example.compositions.domain.entity

data class Question(
    val sum: Int,
    val visibleOfNumbers: Int,
    val options: List<Int>
) {
    val rightAnswers: Int
        get() = sum - visibleOfNumbers
}