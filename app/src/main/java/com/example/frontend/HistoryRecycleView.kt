package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Address

class HistoryRecycleView(private val listeHistory: Array<History>) :
    RecyclerView.Adapter<HistoryRecycleView.RecyclerViewViewHolder>() {
    class RecyclerViewViewHolder(val view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val view =LayoutInflater.from(parent.context)
            .inflate(R.layout.history,parent,false) as View
        return RecyclerViewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listeHistory.size
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.historyName).text = listeHistory[position].address
    }

}