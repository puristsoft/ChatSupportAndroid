package com.puristit.livechat.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.puristit.livechat.BuildConfig;
import com.puristit.livechat.R;
import com.puristit.livechat.gcm.RegistrationIntentService;
import com.puristit.livechat.utils.MyUtils;

import org.json.JSONObject;

import puristit.com.library.PurisitChat;
import puristit.com.server_request.ResponseListener;
import puristit.com.server_request.ServerResponse;

import static com.puristit.livechat.gcm.RegistrationIntentService.TOKEN;


public class MainActivity extends AppCompatActivity {

    private String userName;
    private String pass;
    private PurisitChat purisitChat;
    private boolean isReceiverRegistered;
    private ProgressDialog pdLoadingView;
    private String token = "";
    private String chatUrl = "";





    private Button btnRegister;
    private Button btnInitialize;
    private Button btnChat;
    private CheckBox chSelectDepartment;
    private TextView tvVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnInitialize = (Button)findViewById(R.id.btnInitialize);
        btnChat = (Button)findViewById(R.id.btnChat);
        chSelectDepartment = (CheckBox)findViewById(R.id.chSelectDepartment);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        if (MyUtils.getSharedPrefString(this, "UserName", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "Password", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "NickName", "").isEmpty() ||
                MyUtils.getSharedPrefString(this, "PuristKey", "").isEmpty()) {

            startActivity(new Intent(this, SettingsActivity.class));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            token = intent.getStringExtra(TOKEN);
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



    public void onRegisterClicked(View view){
        if (!MyUtils.haveNetworkConnection(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        purisitChat = PurisitChat.getInstance(this, MyUtils.getSharedPrefString(this, "PuristKey", ""));
        vShowProgressDialog("Registering, Please wait...");
        purisitChat.register(MyUtils.getSharedPrefString(this, "UserName", ""), MyUtils.getSharedPrefString(this, "Password", ""), MyUtils.getSharedPrefString(this, "NickName", ""), 0, 0, new ResponseListener() {
            @Override
            public void onSuccessResponse(ServerResponse response) {
                vRemoveProgressDialog();
                JSONObject result = ((JSONObject)response.getData()).optJSONObject("result");
                userName = result.optString("p_username");
                pass = result.optString("p_password");

                btnRegister.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tick_mark, 0, 0, 0);
            }

            @Override
            public void onFailedResponse(ServerResponse response) {
                super.onFailedResponse(response);
                vRemoveProgressDialog();
                Toast.makeText(MainActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void onInitializeClicked(View view){
        if (!MyUtils.haveNetworkConnection(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        if (purisitChat != null) {
            vShowProgressDialog("Initializing, Please wait...");
            purisitChat.initialize(userName, pass, token, 168, 0, 0, "en", new ResponseListener() {
                @Override
                public void onSuccessResponse(ServerResponse response) {
                    vRemoveProgressDialog();
                    JSONObject result = ((JSONObject) response.getData()).optJSONObject("result");
                    chatUrl = result.optString("chat_url");

                    MyUtils.setSharedPrefString(MainActivity.this, "ChatUrl", chatUrl);

                    btnInitialize.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tick_mark, 0, 0, 0);
                }

                @Override
                public void onFailedResponse(ServerResponse response) {
                    vRemoveProgressDialog();
                    super.onFailedResponse(response);
                    Toast.makeText(MainActivity.this, "Initializing Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




    public void onChatClicked(View view) {
        if (!MyUtils.haveNetworkConnection(this)){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        if (purisitChat != null) {
            Intent chatIntent = new Intent(this, ChatActivity.class);

            PurisitChat.URLBuilder builder = new PurisitChat.URLBuilder(chatUrl)
                    .setGCM_Id(token)
                    .setRoomlistEnabled(chSelectDepartment.isChecked())
                    .setRoom("Sales")
                    .setHeadercolor(getResources().getColor(R.color.colorPrimary));


            String url = builder.build();
            chatIntent.putExtra("ChatUrl", url);

            btnChat.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tick_mark, 0, 0, 0);
            startActivity(chatIntent);
            finish();
        }
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
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }


    private void getGCM_ID(){
        vShowProgressDialog("Please wait, getting Google Device Id");
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                vRemoveProgressDialog();
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST, new DialogInterface.OnCancelListener(){

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }).show();
            } else {
                Log.i("MainActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }









}
