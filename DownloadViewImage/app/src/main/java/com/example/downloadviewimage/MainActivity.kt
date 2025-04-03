package com.example.downloadviewimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUrl: EditText
    private lateinit var buttonDownload: Button
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ các thành phần UI
        editTextUrl = findViewById(R.id.editTextUrl)
        buttonDownload = findViewById(R.id.buttonDownload)
        imageView = findViewById(R.id.imageView)
        progressBar = findViewById(R.id.progressBar)

        // Xử lý sự kiện click nút tải ảnh
        buttonDownload.setOnClickListener {
            val imageUrl = editTextUrl.text.toString().trim()
            if (imageUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập URL ảnh", Toast.LENGTH_SHORT).show()
            } else {
                // Thực hiện tải ảnh bằng AsyncTask
                ImageDownloadTask().execute(imageUrl)
            }
        }
    }

    // AsyncTask để tải ảnh từ URL
    private inner class ImageDownloadTask : AsyncTask<String, Void, Bitmap?>() {

        override fun onPreExecute() {
            // Hiển thị progress bar trước khi bắt đầu tải
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): Bitmap? {
            val imageUrl = params[0]
            var bitmap: Bitmap? = null
            try {
                // Tạo kết nối đến URL
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                // Đọc dữ liệu từ stream
                val input: InputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
                input.close()
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            // Ẩn progress bar sau khi tải xong
            progressBar.visibility = View.GONE

            // Hiển thị ảnh nếu tải thành công, hiện thông báo nếu tải thất bại
            if (result != null) {
                imageView.setImageBitmap(result)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Không thể tải ảnh. Vui lòng kiểm tra URL và thử lại.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}