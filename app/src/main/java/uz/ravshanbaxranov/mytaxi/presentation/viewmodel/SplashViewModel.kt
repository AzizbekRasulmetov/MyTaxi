package uz.ravshanbaxranov.mytaxi.presentation.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.mytaxi.util.PreferencesKeys.IS_FIRST_TIME
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _onboardingChannel = Channel<Unit>()
    val onboardingFlow: Flow<Unit> = _onboardingChannel.receiveAsFlow()

    private val _mapScreenChannel = Channel<Unit>()
    val mapScreenFlow: Flow<Unit> = _mapScreenChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            delay(2000L)
            dataStore.data.collect {
                val isFirstTime = it[IS_FIRST_TIME] ?: true
                if (isFirstTime) {
                    _onboardingChannel.send(Unit)
                } else {
                    _mapScreenChannel.send(Unit)
                }
            }
        }
    }

}
