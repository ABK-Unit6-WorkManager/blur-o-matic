package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Interpolator
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import com.example.background.TAG_OUTPUT
import java.lang.IllegalArgumentException

private val TAG = BlurWorker::class.java.simpleName
class BlurWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {

        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        // Display a status notification using the function,
        // makeStatusNotification to notify the user about blurring the image.
        makeStatusNotification("Blurring image", appContext)

        sleep()

        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            // Get a blurred version of the bitmap
            val blurredBitmap = blurBitmap(picture, appContext)

            // Write the blurred bitmap to a temporary file
            writeBitmapToFile(appContext, blurredBitmap)

            // Make a notification displaying the URI
            makeStatusNotification("Displaying the URI", appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to blurredBitmap.toString())

            return Result.success(outputData)
        } catch (e: Exception) {
            Log.e(TAG_OUTPUT, "Error applying blur")
            e.printStackTrace()
            return Result.failure()
        }
    }
}