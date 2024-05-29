package com.example.capstonecodinavi.Camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import kotlin.math.min

class ObjectDetectorHelper(context: Context) {

    private val interpreter: Interpreter
    private val labels: List<String>
    private val imageSize = 416
    private var viewWidth = 0f
    private var viewHeight = 0f
    private val threshold = 0.5f
    private val minBoxSize = 20f

    private val intValues = IntArray(imageSize * imageSize)
    private val imgData: ByteBuffer = ByteBuffer.allocateDirect(1 * imageSize * imageSize * 3 * 4).apply {
        order(ByteOrder.nativeOrder())
    }

    init {
        val model: MappedByteBuffer = FileUtil.loadMappedFile(context, "best-fp16.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        interpreter = Interpreter(model, options)
        labels = FileUtil.loadLabels(context, "labels.txt")
        Log.d("ObjectDetectorHelper", "Labels loaded: ${labels.size} labels")
        labels.forEachIndexed { index, label ->
            Log.d("ObjectDetectorHelper", "Label[$index]: $label")
        }
    }

    fun setOverlayViewSize(width: Float, height: Float) {
        viewWidth = width
        viewHeight = height
    }

    fun detect(bitmap: Bitmap): List<DetectionResult> {
        Log.d("ObjectDetectorHelper", "Original bitmap size: ${bitmap.width}x${bitmap.height}")

        // 이미지 전처리 과정
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val tensorImage = preprocessImage(resizedBitmap)

        val input = tensorImage.tensorBuffer
        val output = TensorBuffer.createFixedSize(intArrayOf(1, 10647, 13), DataType.FLOAT32)
        interpreter.run(input.buffer, output.buffer.rewind())
        val outputArray = output.floatArray

        val detectionResults = processOutput(output)
        detectionResults.forEach { result ->
            Log.d("ObjectDetectorHelper", "Detected object: ${result.label}, Confidence: ${result.confidence}, BoundingBox: ${result.boundingBox}")
        }

        return detectionResults
    }

    private fun preprocessImage(bitmap: Bitmap): TensorImage {
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        imgData.rewind()
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val pixelValue = intValues[i * imageSize + j]
                imgData.putFloat(((pixelValue shr 16 and 0xFF) - 0f) / 255.0f)
                imgData.putFloat(((pixelValue shr 8 and 0xFF) - 0f) / 255.0f)
                imgData.putFloat(((pixelValue and 0xFF) - 0f) / 255.0f)
            }
        }

        val inputShape = intArrayOf(1, imageSize, imageSize, 3)
        val tensorBuffer = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        tensorBuffer.loadBuffer(imgData)

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(tensorBuffer)

        return tensorImage
    }

    private fun processOutput(output: TensorBuffer): List<DetectionResult> {
        val results = mutableListOf<DetectionResult>()
        val outputArray = output.floatArray
        val gridWidth = 10647
        val numClasses = labels.size

        if (outputArray.size != gridWidth * (5 + numClasses)) {
            throw IllegalArgumentException("Output array size mismatch. Expected ${gridWidth * (5 + numClasses)} but got ${outputArray.size}")
        }

        var maxConfidence = 0f
        var bestResult: DetectionResult? = null

        for (i in 0 until gridWidth) {
            val offset = i * (5 + numClasses)
            val confidence = outputArray[offset + 4]

            if (confidence >= threshold && confidence > maxConfidence) {
                val xCenterNorm = outputArray[offset]
                val yCenterNorm = outputArray[offset + 1]
                val widthNorm = outputArray[offset + 2]
                val heightNorm = outputArray[offset + 3]

                val xCenter = xCenterNorm * viewWidth
                val yCenter = yCenterNorm * viewHeight
                val width = widthNorm * viewWidth
                val height = heightNorm * viewHeight

                val x1 = xCenter - width / 2
                val y1 = yCenter - height / 2
                val x2 = xCenter + width / 2
                val y2 = yCenter + height / 2

                if (width > minBoxSize && height > minBoxSize && x1 >= 0 && y1 >= 0 && x2 <= viewWidth && y2 <= viewHeight) {
                    var maxClass = -1
                    var maxClassProb = 0f
                    for (c in 0 until numClasses) {
                        val classProb = outputArray[offset + 5 + c]
                        if (classProb > maxClassProb) {
                            maxClassProb = classProb
                            maxClass = c
                        }
                    }

                    if (maxClass != -1 && maxClass < labels.size) {
                        val boundingBox = RectF(x1, y1, x2, y2)
                        val label = labels[maxClass]
                        bestResult = DetectionResult(boundingBox, confidence, label)
                        maxConfidence = confidence
                        Log.d("ObjectDetectorHelper", "Best detection updated - Class: $maxClass, Label: $label, BoundingBox: $boundingBox, Confidence: $confidence")
                    }
                } else {
                    Log.d("ObjectDetectorHelper", "Filtered out invalid bounding box - Size: ${width}x${height}, Coordinates: (${x1}, ${y1}, ${x2}, ${y2})")
                }
            }
        }

        bestResult?.let { results.add(it) }
        return results
    }
}

data class DetectionResult(
    val boundingBox: RectF,
    val confidence: Float,
    val label: String
)
