package com.example.well_being

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ListActivity : AppCompatActivity() {

    private lateinit var listPressureListView: ListView
    private val arrayPressure :MutableList<String> = mutableListOf()
    private lateinit var adapter : ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_activiy)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayPressure)
        listPressureListView =findViewById(R.id.listPressureListView)
        listPressureListView.setOnItemClickListener { adapterView, view, i, l ->
            val text = listPressureListView.getItemAtPosition(i).toString()
            adapter.remove(text)
            Toast.makeText(this, "Удалена запись: $text", Toast.LENGTH_LONG).show()
        }
        listPressureListView.adapter = adapter
        val pressureEditTextNumber:String =""
        val r:Boolean=false
        adapter.insert(pressureEditTextNumber.toString(),0)
        val dto:DTO= DTO(pressureEditTextNumber.toString(),r)
    }
}