package com.zhihu.matisse.sunday.callback

import android.net.Uri

interface CameraResultCallback {
    fun onResult(uri: Uri?)

    fun onCancel()
}