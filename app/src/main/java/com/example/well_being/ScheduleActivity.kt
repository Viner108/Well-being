package com.example.well_being

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.well_being.databinding.ActivityScheduleBinding
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
import com.example.well_being.ui.theme.WellbeingTheme
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

const val steps = 10

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendData()
        val pointList = getPointList()
        val getMax = getMax(pointList)
        val getMin = getMin(pointList)
        setContent {
            val xAxisData = AxisData.Builder()
                .axisStepSize(100.dp)
                .backgroundColor(Color.Transparent)
                .steps(pointList.size - 1)
                .labelData { i -> i.toString() }
                .labelAndAxisLinePadding(15.dp)
                .build()

            val yAxisData = AxisData.Builder()
                .steps(steps)
                .backgroundColor(Color.White)
                .labelAndAxisLinePadding(20.dp)
                .labelData { i ->
                    val yScale = (getMax - getMin) / steps.toFloat()
                    String.format("%.1f", ((i * yScale) + getMin))
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
    }


    fun sendData() {
        val thread = Thread(Runnable {
            try {
                val currentDate = Date()
                val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val dateText: String = dateFormat.format(currentDate)
                val timeFormat: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeText: String = timeFormat.format(currentDate)
                val builder: OkHttpClient.Builder = OkHttpClient.Builder()
                builder.connectTimeout(60, TimeUnit.SECONDS); builder.readTimeout(
                    60,
                    TimeUnit.SECONDS
                );
                builder.writeTimeout(60, TimeUnit.SECONDS);
                val client = builder.build();
                val mediaType = "application/json".toMediaTypeOrNull()

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
                    .url("http://192.168.1.102:3005/addresses/q2")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "insomnia/8.3.0")
                    .build()
                val response = client.newCall(request).execute()

                runOnUiThread(Runnable {
                    val body = response.body
                    val builder = GsonBuilder()
                    val listType = object : TypeToken<ArrayList<DTO?>?>() {}.type
                    val list: ArrayList<DTO> = builder.create().fromJson(body!!.string(), listType)
                    val pointList = getPointList()
                    val getMax = getMax(pointList)
                    val getMin = getMin(pointList)
                    setContent {
                        val xAxisData = AxisData.Builder()
                            .axisStepSize(100.dp)
                            .backgroundColor(Color.Transparent)
                            .steps(pointList.size - 1)
                            .labelData { i -> i.toString() }
                            .labelAndAxisLinePadding(15.dp)
                            .build()

                        val yAxisData = AxisData.Builder()
                            .steps(steps)
                            .backgroundColor(Color.White)
                            .labelAndAxisLinePadding(20.dp)
                            .labelData { i ->
                                val yScale = (getMax - getMin) / steps.toFloat()
                                String.format("%.1f", ((i * yScale) + getMin))
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

    fun getPointList(): List<Point> {
        val list = ArrayList<Point>()
        for (i in 0..31) {
            list.add(
                Point(i.toFloat(), Random.nextInt(50, 90).toFloat())
            )
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
        var min = 100F
        list.forEach { point ->
            if (min > point.y) min = point.y
        }
        return min
    }

}