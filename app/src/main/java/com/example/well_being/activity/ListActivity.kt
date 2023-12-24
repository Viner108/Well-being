package com.example.well_being.activity

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.well_being.adapter.DTOAdapter
import com.example.well_being.R
import com.example.well_being.server.Server



class ListActivity : AppCompatActivity() {

    private lateinit var listPressureListView: ListView
    private lateinit var adapter: DTOAdapter
    private val server: Server = Server(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_activiy)
        adapter = DTOAdapter(this)
        listPressureListView = findViewById(R.id.listPressureListView)
        listPressureListView.adapter = adapter
        sendData()
    }

    fun sendData() {
       server.getRequestForListActivity(adapter)
    }
}