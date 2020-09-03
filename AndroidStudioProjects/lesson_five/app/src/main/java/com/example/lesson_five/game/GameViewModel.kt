package com.example.lesson_five.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 60000L

    }

    private val timer: CountDownTimer
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime


    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score
    var word = MutableLiveData<String>()
    private lateinit var wordList: MutableList<String>

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    init {
        _score.value = 0
        resetList()
        nextWord()
        _eventGameFinish.value = false

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinish.value = true
            }

        }
        timer.start()
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "Game View Model Clear")
    }

    private fun resetList() {
        wordList = mutableListOf(
            "Cow",
            "Goat",
            "Cat",
            "Dog",
            "Horse",
            "Home",
            "Flower"
        )
        wordList.shuffle()
    }

    private fun nextWord() {
        if (wordList.isEmpty()) {
            resetList()
            _eventGameFinish.value = true
        }
        word.value = wordList.removeAt(0)


    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun hasFinishComplete() {
        _eventGameFinish.value = false
    }
}