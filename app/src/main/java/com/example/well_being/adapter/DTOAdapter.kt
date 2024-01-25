package com.example.well_being.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.well_being.R
import com.example.well_being.activity.userId
import com.example.well_being.entity.UserHealthDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

class DTOAdapter(context: Context) : BaseAdapter() {
    private val context: Context
    private var inflater: LayoutInflater? = null
    var list: MutableList<UserHealthDto>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.context = context
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    fun Button(convertView: View?): Button {
        val vi: View = if (convertView == null) {
            inflater!!.inflate(R.layout.health_data_item, null)
        } else {
            convertView
        }
        val updateButton = vi.findViewById<View>(R.id.updateButton) as Button
        return updateButton
    }

    override fun getItem(position: Int): UserHealthDto? {
        return list?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vi: View = if (convertView == null) {
            inflater!!.inflate(R.layout.health_data_item, null)
        } else {
            convertView
        }
        val pressureText = vi.findViewById<View>(R.id.pressureTextView) as TextView
        val headAcheText = vi.findViewById<View>(R.id.headAcheTextView) as TextView
        val drowsiness = vi.findViewById<View>(R.id.drowsinessTextView) as TextView
        val deteleButton = vi.findViewById<View>(R.id.deleteButton) as Button
        val updateButton = vi.findViewById<View>(R.id.updateButton) as Button
        deteleButton.setOnClickListener {
            val thread = Thread(Runnable {
                try {
                    val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                    val client = builder.build();
                    val request = Request.Builder()
                        .url("http://192.168.1.102:8080/android/${list!![position].id}")
                        .delete()
                        .build()
                    val response = client.newCall(request).execute()
                    list!!.removeIf {
                        it.id == java.lang.Long.valueOf(list!![position].id.toString())
                    }
                    (context as Activity).runOnUiThread(Runnable {
                        notifyDataSetChanged()
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })
            thread.start()
        }
        updateButton.setOnClickListener {
            val li = LayoutInflater.from(this.getContext())
            val dialogView: View = li.inflate(R.layout.dialog_about_update_data, null)
            val alertDialogBuilder = AlertDialog.Builder(
                getContext()
            )
            alertDialogBuilder.setView(dialogView)
            val pressureUpdate = dialogView.findViewById(R.id.pressureUpdateEditText) as EditText
            val headAcheUpdate = dialogView.findViewById(R.id.headAcheUpdateEditText) as EditText
            val drowsiness = dialogView.findViewById(R.id.drowsinessEditText) as EditText
            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    val thread2 = Thread(Runnable {
                        try {
                            val mediaType = "application/json".toMediaTypeOrNull()
                            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                            val client = builder.build();
                            val jo = JSONObject()
                            jo.put("userId", userId.toString())
                            jo.put("pressure", pressureUpdate.text.toString())
                            jo.put("headAche", headAcheUpdate.text.toString())
                            jo.put("date", list!![position].date)
                            jo.put("drowsiness", drowsiness.text.toString())
                            var body = RequestBody.create(
                                mediaType,
                                jo.toString()
                            )
                            val request = Request.Builder()
                                .url("http://192.168.1.102:8080/android/putUpdateTable/${list!![position].id}")
                                .put(body)
                                .build()
                            val response = client.newCall(request).execute()
                            list!!.find {
                                it.id == java.lang.Long.valueOf(list!![position].id.toString())
                            }.also {
                                it?.pressure = pressureUpdate.text.toString()
                                it?.headAche = headAcheUpdate.text.toString()
                            }
                            (context as Activity).runOnUiThread(Runnable {
                                notifyDataSetChanged()
                            })
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    })
                    thread2.start()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, id -> dialog.cancel() }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        drowsiness.setText(list!![position].drowsiness)
        pressureText.setText(list!![position].pressure)
        headAcheText.setText(list!![position].headAche)
        return vi;
    }

    private fun getContext(): Context {
        return context
    }

}