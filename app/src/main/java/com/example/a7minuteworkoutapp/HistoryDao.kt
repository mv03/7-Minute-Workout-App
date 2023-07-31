package com.example.a7minuteworkoutapp

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface HistoryDao {
    @Insert
    fun insert(historyEntity: HistoryEntity)
}