package com.think.core.permission;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.think.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请工具类
 * @date : 2020-7-25
 * @author zzp
 */
public class PermissionUtil {

    private static final String TAG = "Permission";
    /**
     * 权限申请结果code
     */
    public static final int PERMISSION_REQUEST_CODE = 0x100;

    public static void request(Activity activity, String[] permissions, PermissionCallback callback){
        List<String> unGrantPermission = new ArrayList<>(5);
        for (int i = 0; i < permissions.length; i++) {
            if(!checkPermission(activity,permissions[i])){
                unGrantPermission.add(permissions[i]);
            }
        }
        // 权限已经申请过了
        if(unGrantPermission.isEmpty()){
            callback.onGrant(true);
            return;
        }
        // 小于6.0 不用动态申请
        if(Build.VERSION.SDK_INT < 23){
            ActivityCompat.requestPermissions(activity,permissions, PERMISSION_REQUEST_CODE);
            callback.onGrant(true);
            return;
        }

        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.add(new PermissionFragment(unGrantPermission.toArray(new String[0]),callback),TAG);
        fragmentTransaction.commit();

    }

    /**
     * 弹窗警告用户，授权失败
     * @param activity act
     * @param permission 申请失败的权限
     */
    public static void showDeniedAlert(final Activity activity, String permission){
        String string = activity.getResources().getString(R.string.alert_dialog_permission_request_denied_tip_message);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.alert_dialog_permission_request_tip_title)
                .setMessage(String.format(string,permission.substring(permission.lastIndexOf("."))))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSetting(activity);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    /**
     * 打开应用设置
     */
    public static void startSetting(Activity activity){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:"+activity.getPackageName()));
        activity.startActivity(intent);
    }

    private static boolean checkPermission(Context context,String permission){
        return ContextCompat.checkSelfPermission(context,permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
