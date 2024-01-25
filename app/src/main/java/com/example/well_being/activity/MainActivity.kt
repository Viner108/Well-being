package com.example.well_being.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.well_being.R
import com.example.well_being.entity.UserHealthDto
import com.example.well_being.server.Server
import com.google.android.material.slider.Slider
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
const val userId = 2
class MainActivity : ComponentActivity() {
    private lateinit var sentImageButton: ImageButton
    private lateinit var headAcheEditTextNumber: EditText
    private lateinit var pressureEditTextNumber: EditText
    private lateinit var graphButton: Button
    private lateinit var sliderDrowsiness : Slider
    private val server: Server = Server(this)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        sentImageButton = findViewById(R.id.sentImageButton)
        pressureEditTextNumber = findViewById(R.id.pressureEditTextNumber)
        headAcheEditTextNumber = findViewById(R.id.headAcheEditTextNumber)
        graphButton=findViewById(R.id.graphButton)
        sliderDrowsiness=findViewById(R.id.sliderDrowsiness)
        graphButton.setOnClickListener{
            val intent: Intent = Intent(this, GetDateActivity::class.java)
            startActivity(intent)
        }
    }

    fun sendData(view: View) {
        server.postRequestForMainActivity(pressureEditTextNumber,headAcheEditTextNumber,sliderDrowsiness)
    }
    fun deleteByUserId(view: View) {
        server.deleteRequestForMainActivity()
    }
    fun getListActivity(view: View) {
        val intent: Intent = Intent(this, ListActivity::class.java)
        val message = pressureEditTextNumber.text.toString()
        intent.putExtra(EXTRA_MESSAGE, message)
        startActivity(intent)
    }

}

