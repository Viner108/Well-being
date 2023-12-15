package com.example.well_being

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class DTOAdapter(context: Context) : BaseAdapter() {
    private var inflater: LayoutInflater? = null
    var list: List<DTO>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return list?.size ?: 0
    }

    override fun getItem(position: Int): DTO? {
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
        pressureText.setText(list!![position].pressure);
        headAcheText.setText(list!![position].headAche.toString());
        return vi;
    }
}