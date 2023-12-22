package com.example.well_being

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


class DTOAdapter(context: Context) : BaseAdapter() {
    private var inflater: LayoutInflater? = null
    var list: List<UserHealthDto>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): UserHealthDto? {
        return list?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vi: View = if (convertView == null) {
            inflater!!.inflate(R.layout.health_data_item, null)
        } else {
            convertView
        }
        val pressureText = vi.findViewById<View>(R.id.pressureTextView) as TextView
        val headAcheText = vi.findViewById<View>(R.id.headAcheTextView) as TextView
        val deteleBotton = vi.findViewById<View>(R.id.deleteButton) as Button
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val mediaType = "application/json".toMediaTypeOrNull()
        deteleBotton.setOnClickListener{
            val thread = Thread(Runnable {
                try {
                    val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                    val client = builder.build();
                    val request = Request.Builder()
                        .url("http://192.168.1.102:8080/android/${list!![position].id}")
                        .delete()
                        .build()
                    val response = client.newCall(request).execute()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })
            thread.start()
        }
        pressureText.setText(list!![position].pressure);
        headAcheText.setText(list!![position].headAche);
        return vi;
    }

}