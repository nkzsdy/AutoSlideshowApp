package jp.techacademy.ryota.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var imageUriList = mutableListOf<Uri>()
    private var imageIndex = 0

    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }

        prev_button.setOnClickListener {
            showPrevImage()
        }

        play_pause_button.setOnClickListener {
            togglePlayPause()
        }

        next_button.setOnClickListener {
            showNextImage()
        }

        permission_request_button.setOnClickListener {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()

                    if (permission_request_button.visibility == View.VISIBLE) {
                        permission_request_button.visibility = View.INVISIBLE
                    }
                } else {
                    if (permission_request_button.visibility == View.INVISIBLE) {
                        permission_request_button.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageUriList.add(imageUri)
            } while (cursor.moveToNext())

            imageView.setImageURI(imageUriList[imageIndex])
        }
        cursor.close()
    }

    private fun showPrevImage() {
        if (imageUriList.isNotEmpty()) {
            if (imageIndex == 0) {
                imageIndex = imageUriList.lastIndex
            } else {
                imageIndex--
            }
            Log.d("ANDROID", "imageIndex : $imageIndex")
            Log.d("ANDROID", "imageUri : ${imageUriList[imageIndex]}")
            imageView.setImageURI(imageUriList[imageIndex])
        }
    }

    private fun showNextImage() {
        if (imageUriList.isNotEmpty()) {
            if (imageIndex == imageUriList.lastIndex) {
                imageIndex = 0
            } else {
                imageIndex++
            }
            Log.d("ANDROID", "imageIndex : $imageIndex")
            Log.d("ANDROID", "imageUri : ${imageUriList[imageIndex]}")
            imageView.setImageURI(imageUriList[imageIndex])
        }
    }

    private fun togglePlayPause() {
        if (mTimer == null) {
            mTimer = Timer()

            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        showNextImage()
                    }
                }
            }, 2000, 2000)

            prev_button.visibility = View.INVISIBLE
            next_button.visibility = View.INVISIBLE
            play_pause_button.text = "停止"
        } else {
            mTimer!!.cancel()
            mTimer = null
            prev_button.visibility = View.VISIBLE
            next_button.visibility = View.VISIBLE
            play_pause_button.text = "再生"
        }
    }
}
