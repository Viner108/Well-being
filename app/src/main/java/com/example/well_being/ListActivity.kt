package com.example.well_being

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import co.yml.charts.common.model.Point
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ListActivity : AppCompatActivity() {

    private lateinit var listPressureListView: ListView
    private val arrayPressure: MutableList<DTO> = mutableListOf()
    private lateinit var adapter: DTOAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_activiy)
        adapter = DTOAdapter(this)
        listPressureListView = findViewById(R.id.listPressureListView)
        listPressureListView.setOnItemClickListener { adapterView, view, i, l ->
//            val text = listPressureListView.getItemAtPosition(i).toString()
//            adapter.remove(text)
 //           Toast.makeText(this, "Удалена запись: $text", Toast.LENGTH_LONG).show()
        }
        listPressureListView.adapter = adapter
        val pressureEditTextNumber: String = ""
        val r: Boolean = false
        sendData()
    }

    fun sendData() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val mediaType = "application/json".toMediaTypeOrNull()
        val thread = Thread(Runnable {
            try {
                val currentDate = Date()
                val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val dateText: String = dateFormat.format(currentDate)
                val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeText: String = timeFormat.format(currentDate)
                var body = RequestBody.create(
                    mediaType,
                    JSONObject(
                        mapOf(
                            "data " + "${dateText}" to mapOf(
                                "time " + "${timeText}" to "pressure " + ""
                            )
                        )
                    ).toString()
                )
                val request = Request.Builder()
                    .url("http://192.168.1.102:3001/addresses/q2/1")
                    .post(body)
                    .build()
                val response = client.newCall(request).execute()
                runOnUiThread(Runnable {
                    val body: String = response.peekBody(2048).string()
                    val builder = GsonBuilder()
                    val listType = object : TypeToken<ArrayList<DTO?>>() {}.type
                    val list: ArrayList<DTO> = Gson().fromJson(body, listType)
                    var pointList: MutableList<Point> = mutableListOf()
                    adapter.list=list
                    adapter.notifyDataSetChanged()

                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
}