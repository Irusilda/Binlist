package com.example.bin1.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items", )
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var user_number: String
)
