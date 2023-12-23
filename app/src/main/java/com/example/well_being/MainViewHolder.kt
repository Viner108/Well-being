package com.example.well_being

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ParcelCreator")
class MainViewHolder (itemView: View) :
    RecyclerView.ViewHolder( itemView), Parcelable {

    companion object {
        fun from(parent: ViewGroup): NotificationAdapter.MainViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ActivityMainListItemBinding.inflate(inflater, parent, false)
            return MainViewHolder(binding)
        }
    }

    constructor(parcel: Parcel) : this() {
    }

    fun bind(item: Todo, clickListener: MainListClickListener) {
        binding.todoItem = item
        binding.clickListener = clickListener
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainViewHolder> {
        override fun createFromParcel(parcel: Parcel): MainViewHolder {
            return MainViewHolder(parcel)
        }

        override fun newArray(size: Int): Array<MainViewHolder?> {
            return arrayOfNulls(size)
        }
    }
}