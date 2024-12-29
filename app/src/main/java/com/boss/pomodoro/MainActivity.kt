package com.boss.pomodoro

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.boss.pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerViewModel.timeLeft.observe(this, Observer { timeLeft ->
            binding.timerTextView.text = timeLeft
        })

        binding.startButton.setOnClickListener {
            if (binding.startButton.text == "Start") {
                timerViewModel.startTimer() // 타이머 시작
                binding.startButton.text = "Stop" // 버튼 텍스트 변경
                binding.focusFailCard.visibility = android.view.View.GONE // 카드 숨김
                enableButtons(true) // 버튼 활성화
            } else {
                timerViewModel.stopTimer() // 타이머 중지
                binding.startButton.text = "Start" // 버튼 텍스트 변경
            }
        }

        binding.resetButton.setOnClickListener {
            timerViewModel.resetTimer() // 타이머 초기화
            binding.startButton.text = "Start" // 버튼 텍스트 초기화
            binding.focusFailCard.visibility = android.view.View.GONE // 카드 숨김

            // 타이머 텍스트 초기화
            binding.timerTextView.apply {
                paint.isStrikeThruText = false // 가로선 제거
                setTextColor(Color.BLACK) // 글자색 검정색으로 변경
            }
            enableButtons(true) // 버튼 활성화
        }

        // 화면 회전 시 상태 복원
        if (savedInstanceState != null) {
            val timeRemaining = savedInstanceState.getLong("timeRemaining", 1500)
            // 타이머를 시작하지 않도록 수정
            binding.timerTextView.text = timerViewModel.formatTime(timeRemaining) // 타이머 텍스트 업데이트
        }
    }

    override fun onPause() {
        super.onPause()
        binding.focusFailCard.visibility = android.view.View.VISIBLE // 카드 표시
        timerViewModel.stopTimer() // 타이머 중지
        binding.timerTextView.apply {
            paint.isStrikeThruText = true // 가로선 긋기
            setTextColor(Color.RED) // 글자색 빨간색으로 변경
        }

        // 집중 실패 메시지 업데이트
        val timeRemaining = timerViewModel.getTimeRemaining()
        val initialTime = timerViewModel.getInitialTime()
        val timeFailed = initialTime - timeRemaining // 초기 시간에서 남은 시간 차이

        // 메시지 포맷 변경
        val minutesFailed = timeFailed / 60
        val secondsFailed = timeFailed % 60
        if (timeFailed > 60) {
            binding.focusFailTextView.text = "${minutesFailed}분 ${secondsFailed}초 만에 집중에 실패하셨습니다." // 분 초 형식
        } else {
            binding.focusFailTextView.text = "${timeFailed}초 만에 집중에 실패하셨습니다." // 초만 표시
        }

        enableButtons(false) // 버튼 비활성화
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeRemaining", timerViewModel.getTimeRemaining()) // 남은 시간 저장
    }

    private fun enableButtons(enable: Boolean) {
        binding.startButton.isEnabled = enable
        binding.resetButton.isEnabled = true // Reset 버튼은 항상 활성화
    }
}