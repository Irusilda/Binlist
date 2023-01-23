package com.example.bin1.room
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    fun insertItem(item: Item)
    @Query("""SELECT DISTINCT user_number 
              FROM items
              ORDER BY id DESC
              LIMIT 10
           """)
    fun getAllItems(): Flow<List<Item>>
}