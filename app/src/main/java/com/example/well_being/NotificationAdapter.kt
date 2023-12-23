package com.example.well_being

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView


class NotificationAdapter(context: Context,
                          data: MutableList<UserHealthDto>,
                          config: AsyncDifferConfig<UserHealthDto>
) :
    PagedListAdapter<UserHealthDto, NotificationAdapter.MainViewHolder>(config) {
    private val context: Context
    private var inflater: LayoutInflater? = null
    var list: MutableList<UserHealthDto>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.context = context
    }

    override fun getItem(position: Int): UserHealthDto? {
        return list?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.MainViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.health_data_item, parent, false)
        return NotificationAdapter.MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.bindTo(getItem(position)!!);
    }

    public class MainViewHolder private constructor(private val binding: ActivityMainListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): MainViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ActivityMainListItemBinding.inflate(inflater, parent, false)
                return MainViewHolder(binding)
            }
        }

        fun bind(item: Todo, clickListener: MainListClickListener) {
            binding.todoItem = item
            binding.clickListener = clickListener
        }
    }

}