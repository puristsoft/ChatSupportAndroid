package com.puristit.livechat.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.puristit.livechat.R;
import com.puristit.livechat.utils.MyUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_UserName;
    private EditText et_Password;
    private EditText et_NickName;
    private EditText et_PuristKey;
    private Button btnSave;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
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
        et_NickName = (EditText)findViewById(R.id.et_NickName);
        et_PuristKey = (EditText)findViewById(R.id.et_PuristKey);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);



    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                if (!et_NickName.getText().toString().isEmpty() && !et_UserName.getText().toString().isEmpty()
                        && !et_Password.getText().toString().isEmpty() && !et_PuristKey.getText().toString().isEmpty()) {


                    //Getting Old key to compare it with the new one
                    String oldKey = MyUtils.getSharedPrefString(this, "PuristKey", "");



                    MyUtils.setSharedPrefString(this, "UserName", et_UserName.getText().toString());
                    MyUtils.setSharedPrefString(this, "Password", et_Password.getText().toString());
                    MyUtils.setSharedPrefString(this, "NickName", et_NickName.getText().toString());
                    MyUtils.setSharedPrefString(this, "PuristKey", et_PuristKey.getText().toString());


                    if (!oldKey.isEmpty() && !oldKey.equals(et_PuristKey.getText().toString())) {
                        // when changing the PuristKey the app must be restarted to take effect
                        showDialog();
                    } else {
                        finish();
                    }

                } else {
                    Toast.makeText(this, "Please fill in all Fields", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }




    public void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Restart App")
                .setMessage("In order to change the settings the app must restart, please restart it now.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}
