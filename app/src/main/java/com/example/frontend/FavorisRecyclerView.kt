package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavorisRecyclerView(val listeFavoris: Array<Favorite>):
    RecyclerView.Adapter<FavorisRecyclerView.RecyclerViewViewHolder>() {
    class RecyclerViewViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favoris, parent, false) as View
        return RecyclerViewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listeFavoris.size
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.favoriName).text = listeFavoris[position].address
    }

}