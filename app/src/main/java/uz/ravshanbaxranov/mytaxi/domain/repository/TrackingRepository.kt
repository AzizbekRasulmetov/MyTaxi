package uz.ravshanbaxranov.mytaxi.domain.repository

import uz.ravshanbaxranov.mytaxi.data.model.Track

interface TrackingRepository {

    suspend fun addTrack(track: Track)


}