package ru.dima.aston_intensiv_2

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget

class MainActivity : AppCompatActivity() {

    private lateinit var resetButton: Button
    private lateinit var rainbowDrumView: RainbowDrumView
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resetButton = findViewById(R.id.resetButton)
        rainbowDrumView = findViewById(R.id.rainbowDrumView)

        setClickListeners()

        rainbowDrumView.setupDrum(
            angle = viewModel.state.currentAngle,
            isSpinning = viewModel.state.isSpinning,
            bitmap = viewModel.state.picture,
            text = viewModel.state.text
        )
    }

    override fun onPause() {
        super.onPause()

        viewModel.updateCurrentAngle(rainbowDrumView.currentAngle)
        viewModel.updateIsSpinning(rainbowDrumView.spinning)
    }

    private fun setClickListeners() {
        rainbowDrumView.setOnClickListener {
            preparePicture { bitmap ->
                rainbowDrumView.reset()

                rainbowDrumView.startSpinning(
                    pictureToDraw = bitmap,
                    textToDraw = "ТЕКСТ",
                    onText = {
                        viewModel.updateText(it)
                        viewModel.updatePicture(null)
                    },
                    onPicture = {
                        viewModel.updatePicture(it)
                        viewModel.updateText(null)
                    }
                )
            }
        }

        resetButton.setOnClickListener()
        {
            rainbowDrumView.reset()
            viewModel.updateState(newState = MainViewModel.MainState())
        }
    }

    private fun preparePicture(onPrepared: (Bitmap) -> Unit) {
        Glide.with(this)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load("https://loremflickr.com/640/360")
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    onPrepared(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}