package uz.ravshanbaxranov.mytaxi.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.mytaxi.data.model.Track
import uz.ravshanbaxranov.mytaxi.domain.use_case.TrackUseCases
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val trackUseCases: TrackUseCases
) : ViewModel() {


    fun addTrackToDatabase(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            trackUseCases.addTrack(track)
        }
    }

}
