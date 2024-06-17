package com.example.capstonecodinavi.Camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import androidx.core.os.HandlerCompat.postDelayed


class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    private var detections: List<DetectionResult> = emptyList()
    private val paint = Paint().apply {
        color = android.graphics.Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    init {
        surfaceTextureListener = this
    }

    fun setResults(detections: List<DetectionResult>) {
        this.detections = detections

        detections.forEach {
            }
        if (isAvailable) {
            drawDetections()
        } else {
            postDelayed({ setResults(detections) }, 100)
        }
    }

    private fun drawDetections() {
        val canvas: Canvas? = lockCanvas()
        if (canvas != null) {
            try {
                synchronized(this) {
                    canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
                    detections.forEach { detection ->
                        canvas.drawRect(detection.boundingBox, paint)
                    }
                }
            } finally {
                unlockCanvasAndPost(canvas)
            }
        } else {
            postDelayed({ drawDetections() }, 100)
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (detections.isNotEmpty()) {
            drawDetections()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        if (detections.isNotEmpty()) {
            drawDetections()
        }
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // Do nothing
    }
}
