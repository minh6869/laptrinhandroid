package com.example.timercounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var seconds = 0L

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                seconds++
                updateTimerText()
                // Chạy lại Runnable sau 1 giây
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo views
        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        // Thiết lập sự kiện cho nút Bắt đầu
        startButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                // Bắt đầu đếm thời gian
                handler.post(runnable)
            }
        }

        // Thiết lập sự kiện cho nút Tạm dừng
        pauseButton.setOnClickListener {
            isRunning = false
            // Dừng đếm thời gian
            handler.removeCallbacks(runnable)
        }

        // Thiết lập sự kiện cho nút Đặt lại
        resetButton.setOnClickListener {
            isRunning = false
            seconds = 0
            // Dừng đếm thời gian và cập nhật UI
            handler.removeCallbacks(runnable)
            updateTimerText()
        }

        // Khởi tạo hiển thị thời gian
        updateTimerText()
    }

    private fun updateTimerText() {
        // Chuyển đổi tổng số giây thành định dạng HH:MM:SS
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60

        // Cập nhật TextView với thời gian đã định dạng
        timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Đảm bảo loại bỏ callback khi Activity bị hủy
        handler.removeCallbacks(runnable)
    }
}