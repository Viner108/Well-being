package com.example.well_being

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var sentImageButton: ImageButton
    private lateinit var headAcheEditTextNumber: EditText
    private lateinit var sentTextView: TextView
    private lateinit var pressureEditTextNumber: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        sentImageButton = findViewById(R.id.sentImageButton)
        pressureEditTextNumber = findViewById(R.id.pressureEditTextNumber)
        headAcheEditTextNumber = findViewById(R.id.headAcheEditTextNumber)
    }

    fun sendData(view: View) {
        val thread = Thread(Runnable {
            try {
                val currentDate = Date()
                val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val dateText: String = dateFormat.format(currentDate)
                val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeText: String = timeFormat.format(currentDate)
                val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                builder.connectTimeout(60, TimeUnit.SECONDS); builder.readTimeout(
                    60,
                    TimeUnit.SECONDS
                );
                builder.writeTimeout(60, TimeUnit.SECONDS);
                val client = builder.build();
                val mediaType = "application/json".toMediaTypeOrNull()
                pressureEditTextNumber=findViewById(R.id.pressureEditTextNumber)
                val dto:DTO= DTO(1,pressureEditTextNumber.text.toString(),headAcheEditTextNumber.text.toString())
                val jo = JSONObject()
                jo.put("pressure",pressureEditTextNumber.text.toString())
                jo.put("headAche",headAcheEditTextNumber.text.toString())
                var body = RequestBody.create(
                    mediaType,
                    jo.toString()
                )
                val request = Request.Builder()
                    .url("http://192.168.1.102:8080/android/postDTO")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "insomnia/8.3.0")
                    .build()
                val response = client.newCall(request).execute()
                val body2:String = response.body.toString()
                runOnUiThread(Runnable {
                    sentTextView = findViewById(R.id.sentTextView)
                    sentTextView.setText("Отправлено")
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    fun getSchedule(view: View) {
        val intent: Intent = Intent(this, ScheduleActivity::class.java)
        val message = pressureEditTextNumber.text.toString()
        intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)
    }
    fun getListActivity(view: View) {
        val intent: Intent = Intent(this, ListActivity::class.java)
        val message = pressureEditTextNumber.text.toString()
        intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)
    }

}

