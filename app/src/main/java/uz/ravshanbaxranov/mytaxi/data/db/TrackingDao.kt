package uz.ravshanbaxranov.mytaxi.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.ravshanbaxranov.mytaxi.data.model.Track

@Dao
interface TrackingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordInfo(info: Track)

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): Flow<List<Track>>

}