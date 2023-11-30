package ru.dima.aston_intensiv_2

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var state = MainState()
        private set

    fun updateState(newState: MainState) {
        state = state.copy(
            currentAngle = newState.currentAngle,
            isSpinning = newState.isSpinning,
            picture = newState.picture,
            text = newState.text
        )
    }

    fun updateCurrentAngle(value: Float) {
        state = state.copy(currentAngle = value)
    }

    fun updateIsSpinning(value: Boolean) {
        state = state.copy(isSpinning = value)
    }

    fun updatePicture(value: Bitmap?) {
        state = state.copy(picture = value)
    }

    fun updateText(value: String?) {
        state = state.copy(text = value)
    }


    data class MainState(
        val currentAngle: Float = 0f,
        val isSpinning: Boolean = false,
        val picture: Bitmap? = null,
        val text: String? = null
    )
}