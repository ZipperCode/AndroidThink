package com.think.core.permission;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.think.core.R;

/**
 * 权限申请Fragment
 * @date : 2020-7-25
 * @author zzp
 */
@SuppressLint("ValidFragment")
public class PermissionFragment extends Fragment {

    private String[] unGrantPermission;
    private PermissionCallback permissionCallback;

    @SuppressLint("ValidFragment")
    public PermissionFragment(String[] unGrantPermission, PermissionCallback permissionCallback){
        this.permissionCallback = permissionCallback;
        this.unGrantPermission = unGrantPermission;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(unGrantPermission, PermissionUtil.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++){
            if(grantResults[i] == -1){
                if(!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),permissions[i])){
                    showDeniedAlert(permissions[i]);
                }else{
                    permissionCallback.onGrant(false);
                    removeSelf();
                }
                return;
            }
        }
        permissionCallback.onGrant(true);
        removeSelf();
    }

    private void removeSelf(){
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void showDeniedAlert(String permission){
        String string = getActivity().getResources().getString(R.string.alert_dialog_permission_request_denied_tip_message);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.alert_dialog_permission_request_tip_title)
                .setMessage(String.format(string,permission.substring(permission.lastIndexOf("."))))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtil.startSetting(getActivity());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permissionCallback.onGrant(false);
                        removeSelf();
                        dialogInterface.cancel();
                    }
                }).show();
    }

}
