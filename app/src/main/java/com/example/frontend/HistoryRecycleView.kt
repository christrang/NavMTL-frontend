package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Address

class HistoryRecycleView(private val listeHistory: Array<History>) :
    RecyclerView.Adapter<HistoryRecycleView.ViewHolder>() {
    class ViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val addressTextView: TextView = view.findViewById(R.id.historyName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context)
            .inflate(R.layout.history,parent,false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listeHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.historyName).text = listeHistory[position].address
    }

}