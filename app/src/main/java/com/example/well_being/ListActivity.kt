package com.example.well_being

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.internal.wait
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class ListActivity : AppCompatActivity() {

    private lateinit var listPressureListView: ListView
    private val arrayPressure: MutableList<UserHealthDto> = mutableListOf()
    private lateinit var adapter: DTOAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_activiy)
        adapter = DTOAdapter(this)
        listPressureListView = findViewById(R.id.listPressureListView)
//        listPressureListView.setOnItemClickListener { adapterView, view, i, l ->
        adapter.Button(null).setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this);
            builder.setTitle("Редактирование таблицы").setMessage("text")
            val dialog: AlertDialog = builder.create();
            dialog.show();
        }
//        }
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
                val urlBuilder: HttpUrl.Builder =
                    ("http://192.168.1.102:8080/android/findByUserId/${userId}").toHttpUrlOrNull()!!
                        .newBuilder()
//                urlBuilder.addQueryParameter("pressure", userHealthDto.pressure)
//                urlBuilder.addQueryParameter("headAche", userHealthDto.headAche)
                val url: String = urlBuilder.build().toString()
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                runOnUiThread(Runnable {
                    val body: String = response.peekBody(2097152).string()
                    val builder = GsonBuilder()
                    val listType = object : TypeToken<ArrayList<UserHealthDto?>>() {}.type
                    val list: ArrayList<UserHealthDto> = Gson().fromJson(body, listType)
                    adapter.list = list
                    adapter.notifyDataSetChanged()

                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
}