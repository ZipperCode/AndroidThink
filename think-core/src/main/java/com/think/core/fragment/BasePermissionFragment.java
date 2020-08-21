package com.think.core.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.think.core.permission.PermissionCallback;
import com.think.core.permission.PermissionUtil;

public abstract class BasePermissionFragment extends BaseFragmentX implements PermissionCallback {

    private String[] mUnGrantPermission;

    public BasePermissionFragment(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnGrantPermission = getGrantPermission();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mUnGrantPermission != null && mUnGrantPermission.length > 0) {
            requestPermissions(mUnGrantPermission, PermissionUtil.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        permissions[i])) {
                    PermissionUtil.showDeniedAlert(requireActivity(), permissions[i]);
                } else {
                    onGrant(false);
                }
                return;
            }
        }
        onGrant(true);
    }

    protected abstract String[] getGrantPermission();
}
