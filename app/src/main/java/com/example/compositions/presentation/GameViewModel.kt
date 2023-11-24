package com.example.compositions.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compositions.R
import com.example.compositions.data.GameRepositoryImpl
import com.example.compositions.domain.entity.GameResults
import com.example.compositions.domain.entity.GameSettings
import com.example.compositions.domain.entity.Level
import com.example.compositions.domain.entity.Question
import com.example.compositions.domain.usecase.GenerateQuestionUseCase
import com.example.compositions.domain.usecase.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    // тут все сыллки
    private val repository = GameRepositoryImpl

    private lateinit var gameSettings: GameSettings

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var countOfRightAnswers = 0
    private var countOfQuestions = 0


    // LiveData`s

    private val _formattedTime = MutableLiveData<String>() // ЛайвДата для таймера
    val formattedTime: LiveData<String> // фрагмент может подписаться на этот таймер
        get() = _formattedTime

    private var timer: CountDownTimer? = null

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentsOfRightAnswers = MutableLiveData<Int>()
    val percentsOfRightAnswers: LiveData<Int>
        get() = _percentsOfRightAnswers


    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers


    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers


    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResults>()
    val gameResult: LiveData<GameResults>
        get() = _gameResult
    //**

    init {
        starGame()
    }

    fun starGame() {
        getGameSettings()
        startTimer()
        generateQuestions()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestions()
    }

    private fun updateProgress() {
        val percent = calculateOfRightAnswers()
        _percentsOfRightAnswers.value = percent
        // _percentsOfRightAnswers мнаган установка болады
        // ане мнаган емес percentsOfRightAnswers ?
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers
        _enoughCountOfRightAnswers.value =
            countOfRightAnswers >= gameSettings.minCountOfRightAnswers
    }

    private fun calculateOfRightAnswers(): Int {
        return if (countOfQuestions == 0) {
            0
        } else {
            ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
        }
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswers
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestions++
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    } // передаем настройки

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(milliSeconds: Long) {
                _formattedTime.value = formatTime(milliSeconds)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    } // запускаем таймер

    private fun generateQuestions() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun formatTime(milliSeconds: Long): String {
        val seconds = milliSeconds / MILLIS_IN_SECONDS // обшое кол секунд
        val minutes = seconds / SECONDS_IN_MINUTES // кол минут
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES) // ост секунды
        return String.format("%02d:%02d", minutes, leftSeconds)
    } // Секунды в нормальный формат

    private fun finishGame() {
        _gameResult.value = GameResults(
            enoughCount.value == true && enoughPercent.value == true,
            countOfRightAnswers,
            countOfQuestions,
            gameSettings
        )
    } // конец игры

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60L
    }
}
