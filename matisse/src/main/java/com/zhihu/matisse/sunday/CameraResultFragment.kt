package com.zhihu.matisse.sunday

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.utils.MediaStoreCompat
import com.zhihu.matisse.internal.utils.SingleMediaScanner
import com.zhihu.matisse.sunday.callback.OnResultCallback

class CameraResultFragment : OnActivityResultFragment() {
    private val REQUEST_CODE_CAMERA = 413
    private lateinit var _mediaStore: MediaStoreCompat
    private var _callback: OnResultCallback? = null
    private var _captureStrategy: CaptureStrategy? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(CameraResultFragment::class.java.simpleName, "onAttach callback:${_callback} ${this}")
        if (!this::_mediaStore.isInitialized) {
            _mediaStore = MediaStoreCompat(context as Activity, this).also {
                it.setCaptureStrategy(_captureStrategy!!)
                it.dispatchCaptureIntent(context, REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun getRequestCode(): Int {
        return REQUEST_CODE_CAMERA
    }

    override fun onResult(data: Intent?, fragmentActivity: FragmentActivity) {
        val fileUri: Uri = _mediaStore.currentPhotoUri
        val filePath: String = _mediaStore.currentPhotoPath
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fragmentActivity.revokeUriPermission(
                fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        SingleMediaScanner(fragmentActivity.applicationContext, filePath) {
            Log.i(CameraResultFragment::class.java.simpleName, "scan finish!")
        }

        Log.i(CameraResultFragment::class.java.simpleName, "onResult ${fileUri} ${this}")
        _callback?.onResult(listOf(fileUri))
    }

    override fun onCancel() {
        Log.i(CameraResultFragment::class.java.simpleName, "onCancel ${this}")
        _callback?.onCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(CameraResultFragment::class.java.simpleName, "onDestroy ${this}")
        _callback = null
    }

    companion object {
        @JvmStatic
        fun attach(captureStrategy: CaptureStrategy, activity: FragmentActivity, callback: OnResultCallback) {
            if (activity.isFinishing) {
                callback.onResult(listOf())
                return
            }

            val fragment = CameraResultFragment().apply {
                this._captureStrategy = captureStrategy
                this._callback = callback
            }
            fragment.attachTo(activity)
        }
    }
}