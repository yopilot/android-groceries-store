package com.hieuwu.groceriesstore.presentation.onboarding

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.hieuwu.groceriesstore.domain.usecases.RefreshAppDataUseCase
import com.hieuwu.groceriesstore.presentation.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val refreshAppDataUseCase: RefreshAppDataUseCase,
    private val sharedPreferences: SharedPreferences
) : ObservableViewModel() {

    private val _isSyncedSuccessful = MutableStateFlow(false)
    val isSyncedSuccessful: StateFlow<Boolean>
        get() = _isSyncedSuccessful.asStateFlow()

    init {
        val isSyncedSuccessfully = sharedPreferences.getBoolean("PRODUCT_SYNC_SUCCESS", false)
        if (isSyncedSuccessfully) {
            _isSyncedSuccessful.value = true
        } else {
            try {
                viewModelScope.launch {
                    refreshAppDataUseCase.execute(Unit)
                    with(sharedPreferences.edit()) {
                        putBoolean("PRODUCT_SYNC_SUCCESS", true)
                        apply()
                        _isSyncedSuccessful.value = true
                    }
                }
            } catch (e: Exception) {
                _isSyncedSuccessful.value = false
            }
        }
    }
}
