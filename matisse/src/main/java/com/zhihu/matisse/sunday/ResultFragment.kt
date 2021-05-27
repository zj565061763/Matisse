package com.zhihu.matisse.sunday

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.sunday.callback.OnResultCallback
import com.zhihu.matisse.ui.MatisseActivity

class ResultFragment : OnActivityResultFragment() {
    private val REQUEST_CODE = 110
    private var _callback: OnResultCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(ResultFragment::class.java.simpleName, "onAttach callback:${_callback} ${this}")
        val intent = Intent(activity, MatisseActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun getRequestCode(): Int {
        return REQUEST_CODE
    }

    override fun onResult(data: Intent?, fragmentActivity: FragmentActivity) {
        val listUri = if (data != null) {
            Matisse.obtainResult(data) ?: listOf()
        } else {
            listOf()
        }
        Log.i(ResultFragment::class.java.simpleName, "onResult ${listUri} ${this}")
        _callback?.onResult(listUri)
    }

    override fun onCancel() {
        Log.i(ResultFragment::class.java.simpleName, "onCancel ${this}")
        _callback?.onCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(ResultFragment::class.java.simpleName, "onDestroy ${this}")
        _callback = null
    }

    companion object {
        @JvmStatic
        internal fun attach(activity: FragmentActivity, callback: OnResultCallback) {
            if (activity.isFinishing) {
                callback.onResult(listOf())
                return
            }

            val fragment = ResultFragment().apply {
                this._callback = callback
            }
            fragment.attachTo(activity)
        }
    }
}