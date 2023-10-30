package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class FriendRequest(
    val nom: String,
    val prenom: String
)
class FriendRequestAdapter(private val friendRequests: List<FriendRequest>) :
    RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views in the item layout and initialize them here
        val nameTextView: TextView = itemView.findViewById(R.id.requesterNameTextView)
        // Add other views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_request_item, parent, false)
        return FriendRequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val friendRequest = friendRequests[position]
        holder.nameTextView.text = "${friendRequest.nom} ${friendRequest.prenom}"
        // Bind other data to views
    }

    override fun getItemCount(): Int {
        return friendRequests.size
    }
}

