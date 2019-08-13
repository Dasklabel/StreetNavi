/**package com.example.doeuny.streetnavi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;



public class PermissionUtil
{
    public static int checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        if(permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
    }
    public static boolean checkPermissions(Activity activity, String permission) {
        int permissionResult = Activity.checkSelfPermission(activity, permission);
        if(permissionResult == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public static void requestPermissions(final @NonNull Activity activity, final @NonNull String[] permissions, final int requestCode) {
        if(Build.VERSION.SDK_INT >= 23) {
            ActivityCompatApi23.requestPermissions(activity, permissions, requestCode);
        }
        else if(activity instanceof OnRequestPermissionsResultCallback) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                final int[] grantResults = new int[permissions.length];

                PackageManager packageManager = activity.getPackageManager();
                String packageName = activity.getPackageName();

                final int permissionCount = permissions.length;
                for(int i = 0; i < permissionCount; i++) {
                    grantResults[i] = packageManager.checkPermission(permissions[i], packageName);
                }
                ((OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(requestCode, permissions, grantResults);
            });
        }
    }

    private static final int REQUEST_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void requestExternalPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PermissionUtil.REQUEST_STORAGE) {
            if(PermissionUtil.verifyPermission(grantResults)) {
                //요청한 권한을 얻었으니 원하는 메소드 사용
            }
            else {
                showRequestAgainDialog();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public static boolean verifyPermission(int[] grantresults) {
        if(grantresults.length < 1) {
            return false;
        }
        for(int result : grantresults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void showRequestAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("이 권한은 꼭 필요한 권한이므로, 설정에서 활성화 부탁드립니다.");
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                try {
                    Intent intet = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                catch(ActivityNotFoundException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        builder.setNavigativeButton("취소", new DialogInterface().onClickListener() {
            @Override
                    public void onClick(DialogInterface dialog, int which) {
                //취소했음
            }
        });
        builder.create();
    }


}
**/