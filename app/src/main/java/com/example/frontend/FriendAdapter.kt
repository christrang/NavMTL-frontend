package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Friend(
    val ami_nom: String,
    val ami_prenom: String
)

class FriendAdapter(
    private val friendList: List<Friend>,
    private val isPendingRequests: Boolean = false
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendNameTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_list_item, parent, false)
        return FriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]
        holder.friendNameTextView.text = if (isPendingRequests) {
            // Handle pending requests data binding
            "Pending Request: ${friend.ami_nom} ${friend.ami_prenom}"
        } else {
            // Handle friend list data binding
            "${friend.ami_nom} ${friend.ami_prenom}"
        }
    }

    override fun getItemCount(): Int = friendList.size
}
