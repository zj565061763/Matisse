package com.zhihu.matisse.sunday.callback

import android.net.Uri

interface OnResultCallback {
    fun onResult(list: List<Uri>)

    fun onCancel()
}