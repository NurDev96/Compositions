package com.example.compositions.domain.usecase

import com.example.compositions.domain.entity.Question
import com.example.compositions.domain.repository.GameRepository

class GenerateQuestionUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(maxSumValue: Int): Question {
        return repository.generateQuestions(maxSumValue, COUNT_OF_OPTIONS)
    }

    companion object {

        private const val COUNT_OF_OPTIONS = 6
    }
}