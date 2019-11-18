package jp.techacademy.ryota.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var imageUriList = mutableListOf<Uri>()
    private var imageIndex = 0

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

        next_button.setOnClickListener {
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
}
