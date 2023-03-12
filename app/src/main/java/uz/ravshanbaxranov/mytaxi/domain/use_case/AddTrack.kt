package uz.ravshanbaxranov.mytaxi.domain.use_case

import uz.ravshanbaxranov.mytaxi.data.model.Track
import uz.ravshanbaxranov.mytaxi.domain.repository.TrackingRepository

class AddTrack(
    private val trackingRepository: TrackingRepository
) {

    suspend operator fun invoke(track: Track) {
        if (track.latitude != 0.0) {
            trackingRepository.addTrack(track)
        }
    }

}