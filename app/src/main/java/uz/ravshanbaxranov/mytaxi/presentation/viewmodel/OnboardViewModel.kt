package uz.ravshanbaxranov.mytaxi.presentation.viewmodel


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.ravshanbaxranov.mytaxi.util.PreferencesKeys.IS_FIRST_TIME
import javax.inject.Inject

@HiltViewModel
class OnboardViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _startChannel = Channel<Unit>()
    val startFlow = _startChannel.receiveAsFlow()


    fun start() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[IS_FIRST_TIME] = false
            }
            _startChannel.send(Unit)
        }
    }

}