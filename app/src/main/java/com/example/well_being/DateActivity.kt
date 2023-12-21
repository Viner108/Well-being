package com.example.well_being

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class DateActivity: AppCompatActivity() {
    private lateinit var beforeDateEditText: EditText
    private lateinit var afterDateEditText: EditText
    private lateinit var getGrahpic: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_adout_date)
        beforeDateEditText=findViewById(R.id.beforeDateEditText)
        afterDateEditText=findViewById(R.id.afterDateEditText)
    }
    fun getGrahpic(view: View) {
        val intent: Intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra("beforeDateEditText",beforeDateEditText.text.toString())
        intent.putExtra("afterDateEditText",afterDateEditText.text.toString())
        startActivity(intent)
    }
}