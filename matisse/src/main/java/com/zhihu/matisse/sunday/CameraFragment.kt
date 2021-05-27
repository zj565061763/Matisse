package com.zhihu.matisse.sunday

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.utils.MediaStoreCompat
import com.zhihu.matisse.internal.utils.SingleMediaScanner
import com.zhihu.matisse.sunday.callback.CameraResultCallback

class CameraFragment : Fragment() {
    private val REQUEST_CODE_CAMERA = 413
    private lateinit var _mediaStore: MediaStoreCompat
    private var _callback: CameraResultCallback? = null
    private var _captureStrategy: CaptureStrategy? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(CameraFragment::class.java.simpleName, "onAttach callback:${_callback} ${this}")
        if (!this::_mediaStore.isInitialized) {
            _mediaStore = MediaStoreCompat(context as Activity, this).also {
                it.setCaptureStrategy(_captureStrategy!!)
                it.dispatchCaptureIntent(context, REQUEST_CODE_CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA) {
            val fragmentActivity = requireActivity()
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri: Uri = _mediaStore.currentPhotoUri
                    val filePath: String = _mediaStore.currentPhotoPath
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        fragmentActivity.revokeUriPermission(
                            fileUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                    SingleMediaScanner(fragmentActivity.applicationContext, filePath) {
                        Log.i(CameraFragment::class.java.simpleName, "scan finish!")
                    }

                    Log.i(CameraFragment::class.java.simpleName, "RESULT_OK ${fileUri} ${this}")
                    _callback?.onResult(fileUri)
                }
                Activity.RESULT_CANCELED -> {
                    Log.i(CameraFragment::class.java.simpleName, "RESULT_CANCELED ${this}")
                    _callback?.onCancel()
                }
            }

            fragmentActivity.supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNowAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(CameraFragment::class.java.simpleName, "onDestroy ${this}")
        _callback = null
    }

    companion object {
        @JvmStatic
        internal fun attach(captureStrategy: CaptureStrategy, activity: FragmentActivity, callback: CameraResultCallback) {
            if (activity.isFinishing) {
                callback.onResult(null)
                return
            }

            val fragment = CameraFragment().apply {
                this._captureStrategy = captureStrategy
                this._callback = callback
            }

            activity.supportFragmentManager
                .beginTransaction()
                .add(fragment, null)
                .commitNowAllowingStateLoss()
        }
    }
}