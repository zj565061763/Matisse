package com.zhihu.matisse.sunday.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.utils.MediaStoreCompat;
import com.zhihu.matisse.internal.utils.SingleMediaScanner;
import com.zhihu.matisse.sunday.callback.OnResultCallback;

import java.util.Collections;

public class CameraResultFragment extends OnActivityResultFragment {
    private static final int REQUEST_CODE = 115;

    private MediaStoreCompat mMediaStoreCompat;
    private CaptureStrategy mCaptureStrategy;
    private OnResultCallback mOnResultCallback;

    public static void start(@NonNull FragmentActivity activity, @NonNull CaptureStrategy captureStrategy, @NonNull OnResultCallback callback) {
        if (activity.isFinishing()) {
            callback.onCancel();
            return;
        }

        final CameraResultFragment fragment = new CameraResultFragment();
        fragment.mCaptureStrategy = captureStrategy;
        fragment.mOnResultCallback = callback;
        fragment.attachTo(activity);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mMediaStoreCompat == null) {
            mMediaStoreCompat = new MediaStoreCompat(getActivity(), this);
            mMediaStoreCompat.setCaptureStrategy(mCaptureStrategy);
            mMediaStoreCompat.dispatchCaptureIntent(context, REQUEST_CODE);
        }
    }

    @Override
    protected int getRequestCode() {
        return REQUEST_CODE;
    }

    @Override
    protected void onResult(@Nullable Intent data, @NonNull FragmentActivity fragmentActivity) {
        final Uri fileUri = mMediaStoreCompat.getCurrentPhotoUri();
        final String filePath = mMediaStoreCompat.getCurrentPhotoPath();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fragmentActivity.revokeUriPermission(fileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        new SingleMediaScanner(fragmentActivity.getApplicationContext(), filePath, new SingleMediaScanner.ScanListener() {
            @Override
            public void onScanFinish() {
                Log.i(CameraResultFragment.class.getSimpleName(), "scan finish!");
            }
        });

        Log.i(CameraResultFragment.class.getSimpleName(), "onResult:" + fileUri + " " + this);
        if (mOnResultCallback != null) {
            mOnResultCallback.onResult(Collections.singletonList(fileUri));
        }
    }

    @Override
    protected void onCancel() {
        Log.i(CameraResultFragment.class.getSimpleName(), "onCancel" + " " + this);
        if (mOnResultCallback != null) {
            mOnResultCallback.onCancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(CameraResultFragment.class.getSimpleName(), "onDestroy" + " " + this);
        mOnResultCallback = null;
    }
}
