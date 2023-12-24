package com.example.well_being.activity

import android.os.Bundle
import android.util.JsonReader
import android.widget.EditText
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.well_being.R
import com.example.well_being.entity.UserHealthDto
import com.example.well_being.ui.theme.WellbeingTheme
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.io.StringReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter


const val steps = 10

class ScheduleActivity : AppCompatActivity() {

    private lateinit var beforeDateEditText: EditText
    private lateinit var afterDateEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        val bundle: Bundle = intent.extras!!
        val beforeDateEditText = bundle.getString("beforeDateEditText").toString()
        val afterDateEditText = bundle.getString("afterDateEditText").toString()
        sendData(beforeDateEditText, afterDateEditText)

    }


    fun sendData(beforeDateEditText: String, afterDateEditText: String) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val thread = Thread(Runnable {
            try {
                val urlBuilder: HttpUrl.Builder =
                    ("http://192.168.1.102:8080/android/findByDate/${beforeDateEditText}/${afterDateEditText}").toHttpUrlOrNull()!!
                        .newBuilder()
                val url: String = urlBuilder.build().toString()
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                runOnUiThread(Runnable {
                    val body: String = response.peekBody(524288).string()
                    val listType = object : TypeToken<ArrayList<UserHealthDto?>>() {}.type
                    val gson = Gson()
                    val list: ArrayList<UserHealthDto> = gson.fromJson(body, listType)
                    var pointList: MutableList<Point> = mutableListOf()
                    pointList = getPointList(list)
                    val getMax = getMax(pointList)
                    val getMin = getMin(pointList)
                    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val beforeDate = LocalDate.parse(beforeDateEditText, format)
                    val afterDate = LocalDate.parse(afterDateEditText, format)
                    val date0 = LocalDate.parse(list[0].date, format)
                    var count: Int = 0
                    setContent {
                        val xAxisData = AxisData.Builder()
                            .axisStepSize(50.dp)
                            .backgroundColor(Color.Transparent)
                            .steps(afterDate.dayOfYear - date0.dayOfYear)
                            .labelData { i ->
                                val month = if (i < list.size) LocalDate.parse(
                                    list[i].date,
                                    format
                                ) else LocalDate.parse(list[list.size - 1].date, format)
                                (if (month.month.value == date0.month.value) {
                                    String.format(
                                        "${month.month.value}.${month.dayOfMonth}",
                                        (i + 1)
                                    )
                                } else {
                                    String.format(
                                        "${month.month.value}.${i+date0.dayOfMonth-date0.lengthOfMonth()}",
                                        (i + 1)
                                    )
                                }).toString()
                            }
                            .labelAndAxisLinePadding(15.dp)
                            .build()

                        val yAxisData = AxisData.Builder()
                            .steps(steps)
                            .backgroundColor(Color.Transparent)
                            .labelAndAxisLinePadding(20.dp)
                            .labelData { i ->
                                val xScale = (getMax - getMin) / steps.toFloat()
                                String.format("%.1f", ((i * xScale) + getMin))
                            }.build()
                        WellbeingTheme {
                            val lineChartData = LineChartData(
                                linePlotData = LinePlotData(
                                    lines = listOf(
                                        Line(
                                            dataPoints = pointList,
                                            LineStyle(color = Color.Green, width = 2.0f),
                                            IntersectionPoint(color = Color.Blue, radius = 4.dp),
                                            SelectionHighlightPoint(color = Color.Red),
                                            ShadowUnderLine(color = Color.Gray),
                                            SelectionHighlightPopUp()
                                        )
                                    ),
                                ),
                                xAxisData = xAxisData,
                                yAxisData = yAxisData,
                                gridLines = GridLines(),
                                backgroundColor = Color.White
                            )
                            LineChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                lineChartData = lineChartData
                            )
                        }
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }


    fun getPointList(listUserHealthDto: ArrayList<UserHealthDto>): MutableList<Point> {
        val list = ArrayList<Point>()
        val listSortUserHealthDto = bubbleSort(listUserHealthDto)
        listSortUserHealthDto.forEach { dto ->
            val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(dto.date, format)
            val indexOfApostrophe = dto.pressure.indexOf('/')
            val firstPressure = dto.pressure.substring(0 until indexOfApostrophe)
            list.add(Point((date.dayOfYear + date.year).toFloat(), firstPressure.toFloat()))
        }
        return list
    }

    private fun getMax(list: List<Point>): Float {
        var max = 0F
        list.forEach { point ->
            if (max < point.y) max = point.y
        }
        return max
    }

    private fun getMin(list: List<Point>): Float {
        var min = 1000F
        list.forEach { point ->
            if (min > point.y) min = point.y
        }
        return min
    }

    fun bubbleSort(listUserHealthDto: ArrayList<UserHealthDto>): ArrayList<UserHealthDto> {
        val n = listUserHealthDto.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date1 = LocalDate.parse(listUserHealthDto[j].date, format)
                val date2 = LocalDate.parse(listUserHealthDto[j + 1].date, format)
                if (date1 > date2) {
                    // Swap the elements
                    val temp = listUserHealthDto[j]
                    listUserHealthDto[j] = listUserHealthDto[j + 1]
                    listUserHealthDto[j + 1] = temp
                }
            }
        }
        return listUserHealthDto
    }

}