package com.example.well_being.server

import android.app.Activity
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.well_being.activity.userId
import com.example.well_being.adapter.DTOAdapter
import com.example.well_being.entity.UserHealthDto
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
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

const val BASE_ADDRESS="http://192.168.1.102:8080/android/"
class Server(context: Context) {
    private val context: Context

    init {
        this.context = context
    }

    fun client() : OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return client
    }
    fun url(requesturl:String):String{
        val urlBuilder: HttpUrl.Builder =
            (BASE_ADDRESS+requesturl).toHttpUrlOrNull()!!
                .newBuilder()
        return  urlBuilder.build().toString()
    }

    fun getRequestForListActivity(adapter: DTOAdapter) {
        val client = client()
        val thread = Thread(Runnable {
            try {
                val url: String = url("findByUserId/$userId")
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                (context as Activity).runOnUiThread(Runnable {
                    val listType = object : TypeToken<ArrayList<UserHealthDto?>>() {}.type
                    val body: String = response.peekBody(2097152).string()
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

    fun postRequestForMainActivity(pressureEditTextNumber: EditText,headAcheEditTextNumber: EditText) {
        val client =client()
        val thread = Thread(Runnable {
            try {
                val currentDate = Date()
                val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateText: String = dateFormat.format(currentDate)
                val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeText: String = timeFormat.format(currentDate)
                val mediaType = "application/json".toMediaTypeOrNull()
                val jo = JSONObject()
                jo.put("userId",userId)
                jo.put("pressure",pressureEditTextNumber.text.toString())
                jo.put("headAche",headAcheEditTextNumber.text.toString())
                jo.put("date",dateText)
                var body = RequestBody.create(
                    mediaType,
                    jo.toString()
                )
                val url: String = url("postDTO")
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()
                val response = client.newCall(request).execute()
                (context as Activity).runOnUiThread(Runnable {
                    showText("Отправлено")
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    fun deleteRequestForMainActivity() {
        val client =client()
        val thread = Thread(Runnable {
            try {
                val url: String = url("deleteById/$userId")
                val request = Request.Builder()
                    .url(url)
                    .delete()
                    .build()
                val response = client.newCall(request).execute()
                (context as Activity).runOnUiThread(Runnable {
                    showText("Удалены все записи данного пользователя")
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }
    fun showText(string: String){
        Toast.makeText(context as Activity,string, Toast.LENGTH_LONG).show()
    }
}
