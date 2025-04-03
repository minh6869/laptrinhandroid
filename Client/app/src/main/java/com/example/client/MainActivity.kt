package com.example.client




import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var etServerIP: EditText
    private lateinit var etServerPort: EditText
    private lateinit var etMessage: EditText
    private lateinit var tvLog: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnSend: Button
    private lateinit var btnDisconnect: Button


    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private var clientSocket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etServerIP = findViewById(R.id.etServerIP)
        etServerPort = findViewById(R.id.etServerPort)
        etMessage = findViewById(R.id.etMessage)
        tvLog = findViewById(R.id.tvLog)
        btnConnect = findViewById(R.id.btnConnect)
        btnSend = findViewById(R.id.btnSend)
        btnDisconnect = findViewById(R.id.btnDisconnect)
    }

    private fun setupListeners() {
        btnConnect.setOnClickListener {
            val serverIP = etServerIP.text.toString().trim()
            val serverPortStr = etServerPort.text.toString().trim()

            if (serverIP.isEmpty() || serverPortStr.isEmpty()) {
                showToast("Please enter both server IP and port")
                return@setOnClickListener
            }

            try {
                val serverPort = serverPortStr.toInt()
                connectToServer(serverIP, serverPort)
            } catch (e: NumberFormatException) {
                showToast("Invalid port number")
            }
        }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isEmpty()) {
                showToast("Please enter a message to send")
                return@setOnClickListener
            }

            sendMessage(message)
            etMessage.text.clear()
        }

        btnDisconnect.setOnClickListener {
            disconnectFromServer()
        }
    }

    private fun connectToServer(serverIP: String, serverPort: Int) {
        appendToLog("Attempting to connect to $serverIP:$serverPort...")

        executorService.execute {
            try {
                clientSocket = Socket(serverIP, serverPort)
                writer = PrintWriter(clientSocket!!.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))

                handler.post {
                    btnConnect.isEnabled = false
                    btnSend.isEnabled = true
                    btnDisconnect.isEnabled = true
                    appendToLog("Connected to server")
                    showToast("Connected to server")
                }

                // Start listening for server responses
                startMessageListener()
            } catch (e: IOException) {
                handler.post {
                    appendToLog("Connection error: ${e.message}")
                    showToast("Failed to connect: ${e.message}")
                }
            }
        }
    }

    private fun startMessageListener() {
        executorService.execute {
            try {
                while (clientSocket != null && !clientSocket!!.isClosed) {
                    val response = reader?.readLine()
                    if (response != null) {
                        handler.post {
                            appendToLog("Server: $response")
                        }
                    } else {
                        // If readLine() returns null, the connection is closed
                        break
                    }
                }
            } catch (e: IOException) {
                if (clientSocket != null && !clientSocket!!.isClosed) {
                    handler.post {
                        appendToLog("Error reading from server: ${e.message}")
                    }
                }
            } finally {
                handler.post {
                    if (clientSocket != null) {
                        appendToLog("Connection closed")
                        resetConnectionState()
                    }
                }
            }
        }
    }

    private fun sendMessage(message: String) {
        executorService.execute {
            try {
                writer?.println(message)
                writer?.flush() // Đảm bảo dữ liệu được gửi ngay lập tức
                handler.post {
                    appendToLog("You: $message")
                    println("Sent message: $message") // Debug log
                }
            } catch (e: Exception) {
                handler.post {
                    appendToLog("Error sending message: ${e.message}")
                    showToast("Failed to send message")
                    println("Error sending message: ${e.message}") // Debug log
                }
            }
        }
    }


    private fun disconnectFromServer() {
        executorService.execute {
            try {
                reader?.close()
                writer?.close()
                clientSocket?.close()

                handler.post {
                    appendToLog("Disconnected from server")
                    resetConnectionState()
                    showToast("Disconnected from server")
                }
            } catch (e: IOException) {
                handler.post {
                    appendToLog("Error while disconnecting: ${e.message}")
                }
            }
        }
    }

    private fun resetConnectionState() {
        clientSocket = null
        writer = null
        reader = null
        btnConnect.isEnabled = true
        btnSend.isEnabled = false
        btnDisconnect.isEnabled = false
    }

    private fun appendToLog(message: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = "[$timeStamp] $message\n"
        tvLog.append(logEntry)

        // Auto-scroll to the bottom
        val scrollView = tvLog.parent as NestedScrollView
        scrollView.post {
            scrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        disconnectFromServer()
        executorService.shutdown()
        super.onDestroy()
    }
}