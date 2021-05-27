package com.zhihu.matisse.sunday.fragment;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

abstract class OnActivityResultFragment extends Fragment {
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getRequestCode()) {
            final FragmentActivity fragmentActivity = requireActivity();
            switch (resultCode) {
                case Activity.RESULT_OK:
                    onResult(data, fragmentActivity);
                    break;
                case Activity.RESULT_CANCELED:
                    onCancel();
                    break;
                default:
                    onResult(null, fragmentActivity);
                    break;
            }

            fragmentActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commitNowAllowingStateLoss();
        }
    }

    public void attachTo(FragmentActivity activity) {
        if (isAdded()) return;
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(this, null)
                .commitNowAllowingStateLoss();
    }

    protected abstract int getRequestCode();

    protected abstract void onResult(@Nullable Intent data, @NonNull FragmentActivity fragmentActivity);

    protected abstract void onCancel();
}
