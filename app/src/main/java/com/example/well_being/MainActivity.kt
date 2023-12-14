package com.example.well_being

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.CheckBox
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
    private lateinit var imageButton: ImageButton
    private lateinit var editText: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var textView: TextView
    private lateinit var pressureEditTextNumber: EditText
    var is_started: Boolean = false
    val EXTRA_MESSAGE = "com.example.well_being.MESSAGE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        imageButton = findViewById(R.id.sentImageButton)
        editText = findViewById(R.id.pressureEditTextNumber)
        checkBox = findViewById(R.id.headAcheCheckBox)
    }

    fun sendData(view: View) {
        is_started = true
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
                var j = JSONObject(mapOf("chat" to mapOf("a" to "get_history"))).toString()
                val mediaType = "application/json".toMediaTypeOrNull()
                pressureEditTextNumber=findViewById(R.id.pressureEditTextNumber)
                var r = if (checkBox.isChecked) {
                    "yes"
                } else {
                    "no"
                }
                val dto:DTO= DTO(pressureEditTextNumber.text.toString(),r)
                var body = RequestBody.create(
                    mediaType,
                    JSONObject(
                        mapOf(
                            "data " + "${dateText}" to mapOf(
                                "time " + "${timeText}" to "pressure " + "${editText.text}",
                                "headache" to r
                            )
                        )
                    ).toString()
                )
                val request = Request.Builder()
                    .url("http://192.168.1.102:3005/addresses/q1/2")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "insomnia/8.3.0")
                    .build()
                val response = client.newCall(request).execute()

                runOnUiThread(Runnable {
                    textView = findViewById(R.id.sentTextView)
                    textView.setText("Отправлено")
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    fun getSchedule(view: View) {
        val intent: Intent = Intent(this, ScheduleActivity::class.java)
        val message = editText.text.toString()
        intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)
    }

}

