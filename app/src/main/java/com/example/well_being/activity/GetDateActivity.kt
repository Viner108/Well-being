package com.example.well_being.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.well_being.R


class GetDateActivity : AppCompatActivity() {
    private lateinit var beforeDateEditText: EditText
    private lateinit var afterDateEditText: EditText
    private lateinit var getGrahpic: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_adout_date)
        beforeDateEditText=findViewById(R.id.beforeDateEditText)
        afterDateEditText=findViewById(R.id.afterDateEditText)
        getGrahpic=findViewById(R.id.getGrahpic)
    }
    fun getGrahpic(view: View) {
        val intent: Intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra("beforeDateEditText",beforeDateEditText.text.toString())
        intent.putExtra("afterDateEditText",afterDateEditText.text.toString())
        startActivity(intent)
    }

}