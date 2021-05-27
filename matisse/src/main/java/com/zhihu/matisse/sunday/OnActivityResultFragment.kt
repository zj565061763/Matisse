package com.zhihu.matisse.sunday

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

abstract class OnActivityResultFragment : Fragment() {
    private var _requestCode: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (_requestCode == null) {
            _requestCode = getRequestCode()
        }
    }

    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == _requestCode!!) {
            val fragmentActivity = requireActivity()
            when (resultCode) {
                Activity.RESULT_OK -> {
                    onResult(data, fragmentActivity)
                }
                Activity.RESULT_CANCELED -> {
                    onCancel()
                }
            }

            fragmentActivity.supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNowAllowingStateLoss()
        }
    }

    protected abstract fun getRequestCode(): Int

    protected abstract fun onResult(data: Intent?, fragmentActivity: FragmentActivity)

    protected abstract fun onCancel()
}