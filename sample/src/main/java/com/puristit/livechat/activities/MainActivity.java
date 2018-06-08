package com.puristit.livechat.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.puristit.livechat.BuildConfig;
import com.puristit.livechat.R;
import com.puristit.livechat.utils.MyUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import puristit.com.entities.ChatMsgSkin;
import puristit.com.library.ChatActivity;
import puristit.com.library.PurisitChat;
import puristit.com.listeners.ResponseListener;
import puristit.com.server_request.ServerResponse;

import static com.puristit.livechat.fcm.MyFirebaseInstanceIDService.REGISTRATION_COMPLETE;
import static com.puristit.livechat.fcm.MyFirebaseInstanceIDService.TOKEN;


public class MainActivity extends AppCompatActivity {


    private boolean isReceiverRegistered;
    private ProgressDialog pdLoadingView;


    private String token = "";



    private TextView tvVersion;
    private long roomID = -1;
    private Button btnChat;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChat = (Button) findViewById(R.id.btnChat);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        if (MyUtils.getSharedPrefString(this, "UserName", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "Password", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "PuristKey", "").isEmpty()) {

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        if (getIntent().hasExtra("Room_ID")) {
            roomID = getIntent().getLongExtra("Room_ID", -1);
//            btnChat.setVisibility(View.GONE);
//            btnLogin.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.toolbar_menu_settings:
//                startActivity(new Intent(this, LoginActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }



    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            token = intent.getStringExtra(TOKEN);
            if (roomID > -1){
                onChatClicked(null);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    vRemoveProgressDialog();
                }
            }, 300);


        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        getGCM_ID();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }



    public void onLoginClicked(View view){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }







    public void onChatClicked(View view) {
        if (!MyUtils.haveNetworkConnection(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        String userName = MyUtils.getSharedPrefString(this, "UserName", "");
        String pass = MyUtils.getSharedPrefString(this, "Password", "");
        String key = MyUtils.getSharedPrefString(this, "PuristKey", "");


        PurisitChat.ChatActivityOptions options = new PurisitChat.ChatActivityOptions();
        options.setOthersMsgSkin(new ChatMsgSkin().setBackgroundColor(Color.parseColor("#FFFFFF"))
                .setTextColor(Color.parseColor("#000000")));
        PurisitChat.startChatActivity(MainActivity.this, key, userName, pass, 0, 0, token, options, roomID);
        roomID = -1;
    }









    public void vShowProgressDialog(String sMessage) {
        if (pdLoadingView != null && pdLoadingView.isShowing()){
            return;
        }
        pdLoadingView = new ProgressDialog(MainActivity.this);
        pdLoadingView.setMessage(sMessage);
        pdLoadingView.setCancelable(false);
        pdLoadingView.show();
    }


    public void vRemoveProgressDialog() {
        if (pdLoadingView != null && pdLoadingView.isShowing()) {
            pdLoadingView.dismiss();
        }
    }


    //---------------------GCM Methods ---------------------------------
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }


    private void getGCM_ID(){
        if (FirebaseInstanceId.getInstance().getToken() == null) {
            vShowProgressDialog("Please wait, getting Google Device Id");
        } else {
            token = FirebaseInstanceId.getInstance().getToken();
            if (roomID > -1){
                onChatClicked(null);
            }
        }
    }



}
