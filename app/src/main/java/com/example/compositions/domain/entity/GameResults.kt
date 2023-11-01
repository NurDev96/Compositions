package com.example.compositions.domain.entity

data class GameResults(
    val win: Boolean,
    val countOfRightAnswers: Int,
    val countOfQuestions: Int,
    val gameSettings: Int
)