package com.example.frontend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class FriendRequest(
    val nom: String,
    val prenom: String,
    val userID: Int,
    val destinataireID: Int,
    val demandeID: Int,
    val expediteurID: Int
)

class FriendRequestAdapter(private val friendRequests: List<FriendRequest>) :
    RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    var onAcceptButtonClickListener: OnAcceptButtonClickListener? = null
    var onDeclineButtonClickListener: OnDeclineButtonClickListener? = null

    interface OnAcceptButtonClickListener {
        fun onAcceptButtonClick(friendRequest: FriendRequest)
    }

    interface OnDeclineButtonClickListener {
        fun onDeclineButtonClick(friendRequest: FriendRequest)
    }

    class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.requesterNameTextView)
        val acceptButton: Button = itemView.findViewById(R.id.acceptRequestButton)
        val declineButton: Button = itemView.findViewById(R.id.declineRequestButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pending_request_item, parent, false)
        return FriendRequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        val friendRequest = friendRequests[position]

        holder.nameTextView.text = "${friendRequest.nom} ${friendRequest.prenom}"

        holder.acceptButton.setOnClickListener {
            onAcceptButtonClickListener?.onAcceptButtonClick(friendRequest)
        }

        holder.declineButton.setOnClickListener {
            onDeclineButtonClickListener?.onDeclineButtonClick(friendRequest)
        }
    }

    override fun getItemCount(): Int {
        return friendRequests.size
    }
}
