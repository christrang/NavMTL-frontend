package com.example.frontend

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_tokens")
data class AuthToken(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val token: String
)
