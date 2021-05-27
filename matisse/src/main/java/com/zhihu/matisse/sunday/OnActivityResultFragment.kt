package com.zhihu.matisse.sunday

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

abstract class OnActivityResultFragment : Fragment() {

    final override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == getRequestCode()) {
            val fragmentActivity = requireActivity()
            when (resultCode) {
                Activity.RESULT_OK -> {
                    onResult(data, fragmentActivity)
                }
                Activity.RESULT_CANCELED -> {
                    onCancel()
                }
                else -> {
                    onResult(null, fragmentActivity)
                }
            }

            fragmentActivity.supportFragmentManager
                .beginTransaction()
                .remove(this)
                .commitNowAllowingStateLoss()
        }
    }

    fun attachTo(activity: FragmentActivity) {
        if (isAdded) return
        activity.supportFragmentManager
            .beginTransaction()
            .add(this, null)
            .commitNowAllowingStateLoss()
    }

    protected abstract fun getRequestCode(): Int

    protected abstract fun onResult(data: Intent?, fragmentActivity: FragmentActivity)

    protected abstract fun onCancel()
}