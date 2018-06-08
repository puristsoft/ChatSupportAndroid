package com.puristit.livechat.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.puristit.livechat.R;
import com.puristit.livechat.utils.MyUtils;
import com.puristit.livechat.utils.PermissionUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginActivity extends AppCompatActivity{

    private EditText et_UserName;
    private EditText et_Password;
    private EditText et_PuristKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        PermissionUtil mPermissionUtil = new PermissionUtil(LoginActivity.this);
        if (!mPermissionUtil.checkIfAllPermissionsGranted(PermissionUtil.GPermissions.STORAGE_PERMISSION)){
            mPermissionUtil.grantPermission(LoginActivity.this, false, new PermissionUtil.onPermissionResultListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied() {

                }
            }, PermissionUtil.GPermissions.STORAGE_PERMISSION);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void initViews(){
        et_UserName = (EditText)findViewById(R.id.et_UserName);
        et_Password = (EditText)findViewById(R.id.et_Password);
        et_PuristKey = (EditText)findViewById(R.id.et_PuristKey);



        et_UserName.setText(MyUtils.getSharedPrefString(this, "UserName", ""));
        et_Password.setText(MyUtils.getSharedPrefString(this, "Password", ""));
        et_PuristKey.setText(MyUtils.getSharedPrefString(this, "PuristKey", ""));


        if (et_PuristKey.getText().toString().isEmpty()) {
            // Just for testing
            et_UserName.setText("anas@telinsight.com");
            et_Password.setText("12341234");
            et_PuristKey.setText("aiXaPQEEePKxAfo7fEsORe4AsnIo4Pr");
        }

    }






    public void onSaveClicked(View view){
        if (!MyUtils.haveNetworkConnection(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        if (et_UserName.getText().toString().isEmpty() || et_Password.getText().toString().isEmpty() || et_PuristKey.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in all Fields", Toast.LENGTH_SHORT).show();
            return;
        }


        MyUtils.setSharedPrefString(this, "UserName", et_UserName.getText().toString());
        MyUtils.setSharedPrefString(this, "Password", et_Password.getText().toString());
        MyUtils.setSharedPrefString(this, "PuristKey", et_PuristKey.getText().toString());

        onBackPressed();
    }



//    public void vShowProgressDialog(String sMessage) {
//        if (pdLoadingView != null && pdLoadingView.isShowing()){
//            return;
//        }
//        pdLoadingView = new ProgressDialog(LoginActivity.this);
//        pdLoadingView.setMessage(sMessage);
//        pdLoadingView.setCancelable(false);
//        pdLoadingView.show();
//    }
//
//
//    public void vRemoveProgressDialog() {
//        if (pdLoadingView != null && pdLoadingView.isShowing()) {
//            pdLoadingView.dismiss();
//        }
//    }




//    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            token = intent.getStringExtra(TOKEN);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    vRemoveProgressDialog();
//                }
//            }, 300);
//
//
//        }
//    };


//
//    @Override
//    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        isReceiverRegistered = false;
//        super.onPause();
//    }



//    //---------------------GCM Methods ---------------------------------
//    private void registerReceiver(){
//        if(!isReceiverRegistered) {
//            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(REGISTRATION_COMPLETE));
//            isReceiverRegistered = true;
//        }
//    }
//
//
//    private void getGCM_ID(){
//        if (FirebaseInstanceId.getInstance().getToken() == null) {
//            vShowProgressDialog("Please wait, getting Google Device Id");
//        } else {
//            token = FirebaseInstanceId.getInstance().getToken();
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!MyUtils.getSharedPrefString(this, "UserName", "").isEmpty() &&
                !MyUtils.getSharedPrefString(this, "Password", "").isEmpty() &&
                !MyUtils.getSharedPrefString(this, "PuristKey", "").isEmpty()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }
    }








}
