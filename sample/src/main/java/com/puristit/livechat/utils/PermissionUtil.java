package com.puristit.livechat.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by a.bakez on 10/4/2016.
 */
public class PermissionUtil {


    private final Context mContext;
    private onPermissionResultListener mListener;

    //    //App Permissions request codes
    public static final int PermissionCODE = 5000;
    private ArrayList<String> checkedPermissions;
    private boolean showPermissionReason = false;


    public PermissionUtil(Context context){
        this.mContext = context;
    }


    public enum GPermissions {

        STORAGE_PERMISSION(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        private String[] permissionNames;

        GPermissions(String[] permissionNames) {
            this.permissionNames = permissionNames;
        }
        public ArrayList<String> getPerList() {
            ArrayList<String> permissions = new ArrayList<>();
            for (String permission : permissionNames){
                permissions.add(permission);
            }
            return permissions;
        }
    }

    public enum PermissionsStatus {
        GRANTED,
        DENIED,
        DONT_ASK_AGAIN;
    }




    /**
     * this method is used to check all the permissions, if all permissions are granted will return <Code>True</Code> otherwise will return <Code>False</Code>
     *
     * */
    public boolean checkIfAllPermissionsGranted(ArrayList<GPermissions> permissions) {
        PermissionUtil.GPermissions[] permissionsArray = new PermissionUtil.GPermissions[permissions.size()];
        return checkIfAllPermissionsGranted(permissions.toArray(permissionsArray));
    }

    /**
     * this method is used to check all the permissions, if all permissions are granted will return <Code>True</Code> otherwise will return <Code>False</Code>
     *
     * */
    public boolean checkIfAllPermissionsGranted(GPermissions... permissions) {
        for (GPermissions perm : permissions){
            if (!checkIfPermissionsGranted(perm.getPerList())){
                return false;
            }
        }
        return true;
    }



    /**
     * this method is used to check all the permissions, if all permissions are granted will return <Code>True</Code> otherwise will return <Code>False</Code>
     *
     * */
    private boolean checkIfPermissionsGranted(ArrayList<String> permissions) {
        for (String perm : permissions){
            if (!isPermissionGranted(perm)){
                return false;
            }
        }
        return true;
    }


    /**
     * this method is used to check if the required permission is granted or not.
     *
     * */
    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }






    /**
     * this method is used to Grant permission if it is not Granted.
     *
     * @param activity             activity calling
     * @param permissions         the permissions list needed
     * @param showPermissionReason whether to show dialog explaining why we need permission or not (if
     */
    public void grantPermission(Activity activity, boolean showPermissionReason, onPermissionResultListener listener, ArrayList<GPermissions> permissions){
        PermissionUtil.GPermissions[] permissionsArray = new PermissionUtil.GPermissions[permissions.size()];
        grantPermission(activity, showPermissionReason, listener, permissions.toArray(permissionsArray));
    }


    /**
     * this method is used to Grant permission if it is not Granted.
     *
     * @param activity             activity calling
     * @param permissions         the permissions list needed
     * @param showPermissionReason whether to show dialog explaining why we need permission or not (if
     */
    public void grantPermission(Activity activity, boolean showPermissionReason, onPermissionResultListener listener, GPermissions... permissions){
        ArrayList<String> allPermissions = new ArrayList<>();
        for (GPermissions permission : permissions){
            allPermissions.addAll(permission.getPerList());
        }
       grantPermission(activity, showPermissionReason, allPermissions, listener);
    }



    private void grantPermission(Activity activity, boolean showPermissionReason, ArrayList<String> permissions, onPermissionResultListener listener){
        this.mListener = listener;
        this.checkedPermissions = permissions;
        this.showPermissionReason = showPermissionReason;

        ArrayList<String> deniedPermissions = new ArrayList<>();

        for (String permission : permissions){
            PermissionsStatus status = getPermissionStatus(activity, permission);
            if (status == PermissionsStatus.DENIED){
                deniedPermissions.add(permission);
            }
        }


        String[] permissionsToRequest = new String[deniedPermissions.size()];
        deniedPermissions.toArray(permissionsToRequest);
        requestPermissions(activity, permissionsToRequest);
    }






    private PermissionsStatus getPermissionStatus(Activity activity, String permission) {
        if (isPermissionGranted(permission)){
            return PermissionsStatus.GRANTED;
        } else {
            return PermissionsStatus.DENIED;
        }
    }





    private void requestPermissions(Activity activity, String[] permissions){
        if (permissions == null ||permissions.length == 0){
            return;
        }
        ActivityCompat.requestPermissions(activity, permissions, PermissionCODE);
    }


    public void onRequestPermissionsResult(final Activity activity, int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionCODE) {
            boolean anyPermissionDenied = false;
            for (int grant : grantResults) {
                if (grant == PackageManager.PERMISSION_DENIED) {
                    anyPermissionDenied = true;
                }
            }

//            StorageUtil.restAll(); // to reset the storage paths , after grant permissions
//            CacheUtil.restAll();
            if (mListener != null){
                final boolean finalAnyPermissionDenied = anyPermissionDenied;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalAnyPermissionDenied){
                            if (showPermissionReason){
                                grantPermission(activity, showPermissionReason, checkedPermissions, mListener);
                            } else {
                                mListener.onPermissionDenied();
                            }
                        } else {
                            mListener.onPermissionGranted();
                        }
                    }
                },300); //this delay is to ensure that onResume for Activity/Fragment is called
            }
        }
    }






    public interface onPermissionResultListener{
        void onPermissionGranted();
        void onPermissionDenied();
    }
}
