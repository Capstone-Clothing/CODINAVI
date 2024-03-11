package com.example.capstonecodinavi

import android.util.Size

interface ConnectionCallback {
    fun onPreviewSizeChosen(size: Size, cameraRotation: Int)
}