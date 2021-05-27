package com.zhihu.matisse.sunday.callback;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

public interface OnResultCallback {
    void onResult(@NonNull List<Uri> list);

    void onCancel();
}