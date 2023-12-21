package com.example.well_being

import android.os.Bundle
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

const val steps = 10

class ScheduleActivity : AppCompatActivity() {

    private lateinit var beforeDateEditText: EditText
    private lateinit var afterDateEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
//        val arguments = intent.extras.also {
//            beforeDateEditText= it.getString(beforeDateEditText)
//        }
        val bundle:Bundle = intent.extras!!
        val beforeDateEditText = bundle.getString("beforeDateEditText").toString()
        val afterDateEditText = bundle.getString("afterDateEditText").toString()
        sendData(beforeDateEditText,afterDateEditText)

    }


    fun sendData(beforeDateEditText:String,afterDateEditText:String) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val mediaType = "application/json".toMediaTypeOrNull()
        val thread = Thread(Runnable {
            try {
                val urlBuilder: HttpUrl.Builder =
                    ("http://192.168.1.102:8080/android/findByDate/${beforeDateEditText}/${beforeDateEditText}").toHttpUrlOrNull()!!
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
                    val body: String = response.peekBody(2048).string()
                    val builder = GsonBuilder()
                    val listType = object : TypeToken<ArrayList<UserHealthDto?>>() {}.type
                    val list: ArrayList<UserHealthDto> = Gson().fromJson(body, listType)
                    var pointList: MutableList<Point>  = mutableListOf()
                    pointList=getPointList(list)
                    val getMax = getMax(pointList)
                    val getMin = getMin(pointList)
                    setContent {
                        val xAxisData = AxisData.Builder()
                            .axisStepSize(100.dp)
                            .backgroundColor(Color.Transparent)
                            .steps(pointList.size)
                            .labelData { i -> (i+1).toString() }
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


    fun getPointList(listUserHealthDto: ArrayList<UserHealthDto>): MutableList<Point> {
        val list = ArrayList<Point>()

        listUserHealthDto.forEach{
            dto -> list.add(Point(dto.userId.toFloat(),dto.pressure.toFloat()))
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