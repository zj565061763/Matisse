package com.zhihu.matisse.sunday.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.sunday.callback.OnResultCallback;
import com.zhihu.matisse.ui.MatisseActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ResultFragment extends OnActivityResultFragment {
    private static final int REQUEST_CODE = 110;

    private OnResultCallback mOnResultCallback;

    public static void start(@NonNull FragmentActivity activity, @NonNull OnResultCallback callback) {
        if (activity.isFinishing()) {
            callback.onCancel();
            return;
        }

        final ResultFragment fragment = new ResultFragment();
        fragment.mOnResultCallback = callback;
        fragment.attachTo(activity);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(ResultFragment.class.getSimpleName(), "onAttach callback:" + mOnResultCallback + " " + this);
        final Intent intent = new Intent(getActivity(), MatisseActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected int getRequestCode() {
        return REQUEST_CODE;
    }

    @Override
    protected void onResult(@Nullable Intent data, @NonNull FragmentActivity fragmentActivity) {
        List<Uri> list = null;
        if (data != null) {
            list = Matisse.obtainResult(data);
        }

        if (list == null) {
            list = Collections.emptyList();
        }

        Log.i(ResultFragment.class.getSimpleName(), "onResult:" + (Arrays.toString(list.toArray())) + " " + this);
        if (mOnResultCallback != null) {
            mOnResultCallback.onResult(list);
        }
    }

    @Override
    protected void onCancel() {
        Log.i(ResultFragment.class.getSimpleName(), "onCancel" + " " + this);
        if (mOnResultCallback != null) {
            mOnResultCallback.onCancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(ResultFragment.class.getSimpleName(), "onDestroy" + " " + this);
        mOnResultCallback = null;
    }
}