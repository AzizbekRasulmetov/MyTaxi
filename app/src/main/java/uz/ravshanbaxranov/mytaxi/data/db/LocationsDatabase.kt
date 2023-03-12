package uz.ravshanbaxranov.mytaxi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.ravshanbaxranov.mytaxi.data.model.Track


@Database(entities = [Track::class], version = 1)
abstract class LocationsDatabase : RoomDatabase() {

    abstract val dao: TrackingDao

}