package uz.ravshanbaxranov.mytaxi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class Track(
    @PrimaryKey
    val id: Int? = null,
    val latitude: Double,
    val longitude: Double,
    val date: Long = System.currentTimeMillis()
)
