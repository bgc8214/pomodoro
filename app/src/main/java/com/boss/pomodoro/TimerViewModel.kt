package com.boss.pomodoro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timeLeft = MutableLiveData<String>()
    val timeLeft: LiveData<String> get() = _timeLeft

    private var timerJob: Job? = null
    private var timeRemaining: Long = 1500 // 초기 시간 (25분)
    private var initialTime: Long = 1500 // 초기 시간 저장

    init {
        _timeLeft.value = formatTime(timeRemaining)
    }

    fun startTimer() {
        initialTime = timeRemaining // 타이머 시작 시 초기 시간 저장
        timerJob = viewModelScope.launch {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
                _timeLeft.value = formatTime(timeRemaining)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer() // 타이머 중지
        timeRemaining = 1500 // 초기화
        _timeLeft.value = formatTime(timeRemaining)
    }

    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun getTimeRemaining(): Long {
        return timeRemaining
    }

    fun getInitialTime(): Long {
        return initialTime
    }
} 