package uz.ravshanbaxranov.mytaxi.data.repository

import uz.ravshanbaxranov.mytaxi.data.db.TrackingDao
import uz.ravshanbaxranov.mytaxi.data.model.Track
import uz.ravshanbaxranov.mytaxi.domain.repository.TrackingRepository

class TrackingRepositoryImpl(private val trackingDao: TrackingDao) : TrackingRepository {

    override suspend fun addTrack(track: Track) {
        trackingDao.insertWordInfo(track)
    }
}