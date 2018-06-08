package puristit.com.library;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
//import com.puristit.livechat.activities.fragments.ChatFrag;
//import com.puristit.livechat.activities.fragments.RoomsFrag;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import puristit.com.entities.LoginObject;
import puristit.com.entities.MessageObject;
import puristit.com.entities.RoomObject;
import puristit.com.listeners.ChatConnectionListener;
import puristit.com.listeners.ResponseListener;
import puristit.com.server_request.ServerResponse;
import puristit.com.utilities.Utils;


/**
 * Created by Anas on 12/12/2016.
 */

public class ChatActivity extends AppCompatActivity implements RoomsFrag.roomsInteractionListener, ChatFrag.chatFragInteractionListener, View.OnClickListener {



    private String userName;
    private String password;
    private String API_KEY;
    private String regID;
    private double loc_lat;
    private double loc_long;



        private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;

    private Uri mCapturedImageURI = null;
    private String mCameraPhotoPath;


    private ProgressDialog pdLoadingView;
    private WebSocketClient mWebSocketClient;
    private PurisitChat purisitChat;
    private LoginObject loginObject;
    private RoomsFrag roomsFrag;
    private ChatFrag chatFrag;
    private Button purist_btn_purist_retry;
    private TextView purist_tv_chat_error_msg;
    private View purist_ll_chat_error;
    private int retryCount = 0;
    private long roomID = -1;
    private FullViewFrag fullViewFrag;
    private NotificationManager notificationManager;
    private long notificationLastTime = 0;
    private long mediaSendConversatonID = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        purist_ll_chat_error = (View) findViewById(R.id.purist_ll_chat_error);
        purist_tv_chat_error_msg = (TextView) findViewById(R.id.purist_tv_chat_error_msg);
        purist_btn_purist_retry = (Button) findViewById(R.id.purist_btn_purist_retry);
        purist_btn_purist_retry.setOnClickListener(this);

        API_KEY = getIntent().getStringExtra("API_KEY");
        userName = getIntent().getStringExtra("userName");
        password = getIntent().getStringExtra("password");
        loc_lat = getIntent().getDoubleExtra("loc_lat",0);
        loc_long = getIntent().getDoubleExtra("loc_long", 0);
        regID = getIntent().getStringExtra("regID");

        if (getIntent().hasExtra("roomID")){
            roomID = getIntent().getLongExtra("roomID", -1);
        }

        roomsFrag = new RoomsFrag();
        replaceFragment(roomsFrag, false);


        purisitChat = PurisitChat.getInstance(this, API_KEY);
        vShowProgressDialog(getString(R.string.purist_login_please_wait));
        login(userName, password, loc_lat, loc_long, regID);

    }



    private void login(String userName, String password, double loc_lat, double loc_long, String regID){
        login(userName, password, loc_lat, loc_long, regID,null);
    }


    private void login(String userName, String password, double loc_lat, double loc_long, String regID, final ChatConnectionListener listener){
        purist_ll_chat_error.setVisibility(View.GONE);
        purisitChat.login(userName, password, loc_lat, loc_long, regID, new ResponseListener() {
            @Override
            public void onSuccessResponse(ServerResponse response) {
                vRemoveProgressDialog();
                loginObject = ((LoginObject)response.getData());
                roomsFrag.updateRooms(loginObject.getRooms());

                purisitChat.connect(loginObject.getChatUrl(), loginObject.getAccess_token(), new ChatConnectionListener() {
                    @Override
                    public void onConnectionOpened() {
                        super.onConnectionOpened();
                        retryCount = 0;
                        if (listener != null){
                            listener.onConnectionOpened();
                        }
                    }

                    @Override
                    public void onConnectionClosed() {
                        super.onConnectionClosed();
                        reConnectIfNeeded();
                        if (listener != null){
                            listener.onConnectionClosed();
                        }
                    }

                    @Override
                    public void onSubscriptionConfirmed() {
                        super.onSubscriptionConfirmed();
                        if (listener != null){
                            listener.onSubscriptionConfirmed();
                        }

                        if (roomID > -1){
                            for (RoomObject roomObject : loginObject.getRooms()){
                                if (roomObject.getId() == roomID){
                                    onRoomSelected(roomObject);
                                    break;
                                }
                            }
                            roomID = -1;
                        }
                    }

                    @Override
                    public void onRoomJoined(long roomID, long conversationID, ArrayList<MessageObject> recentMessages) {
                        vRemoveProgressDialog();
                        Gson gson = new Gson();
                        String recentMessageJson = gson.toJson(recentMessages);
                        RoomObject joinedRoom = null;
                        for (RoomObject roomObject : loginObject.getRooms()){
                            if (roomObject.getId() == roomID){
                                joinedRoom = roomObject;
                                break;
                            }
                        }
                        String selectedRoomJson = gson.toJson(joinedRoom);

                        chatFrag = new ChatFrag();
                        Bundle bundle = new Bundle();
                        bundle.putLong("conversationID", conversationID);
                        bundle.putString("selectedRoom", selectedRoomJson);
                        bundle.putString("recentMessage", recentMessageJson);
                        chatFrag.setArguments(bundle);

                        replaceFragment(chatFrag, true);

                        if (listener != null){
                            listener.onRoomJoined(roomID, conversationID, recentMessages);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        reConnectIfNeeded();
                        if (listener != null){
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onTextMessageReceived(long roomID, MessageObject newMessage) {
                        if (chatFrag != null && chatFrag.getRoomID() == roomID){
                            chatFrag.newMessageReceived(roomID, newMessage);
                        } else {
                            roomsFrag.newMessageReceived(roomID, newMessage);
                        }

                        if (listener != null){
                            listener.onTextMessageReceived(roomID, newMessage);
                        }
                    }

                    @Override
                    public void onRoomStatusChanged(long roomID, String status) {
                        if (chatFrag != null && chatFrag.getRoomID() == roomID){
                            chatFrag.changeRoomStatus(status);
                        } else {
                            roomsFrag.changeRoomStatus(roomID, status);
                        }

                        if (listener != null){
                            listener.onRoomStatusChanged(roomID, status);
                        }
                    }
                });
            }

            @Override
            public void onFailedResponse(ServerResponse response) {
                super.onFailedResponse(response);
                vRemoveProgressDialog();
                purist_ll_chat_error.setVisibility(View.VISIBLE);
                purist_tv_chat_error_msg.setText(getString(R.string.purist_login_failed_msg));
                Toast.makeText(ChatActivity.this, getString(R.string.purist_login_failed_toast), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void reConnectIfNeeded(){
        if (isFinishing()){
            return;
        }
        if (isNetworkAvailable()){
            if (retryCount < 3) {
                Toast.makeText(ChatActivity.this, getString(R.string.purist_general_reconnecting),Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        retryCount++;
                        login(userName, password, loc_lat, loc_long, regID);
                    }
                }, retryCount * 500);

            } else {
                purist_ll_chat_error.setVisibility(View.VISIBLE);
                purist_tv_chat_error_msg.setText(getString(R.string.purist_login_general_error_msg));
            }
        } else{
            purist_ll_chat_error.setVisibility(View.VISIBLE);
            purist_tv_chat_error_msg.setText(getString(R.string.purist_login_no_connection_msg));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void replaceFragment(Fragment newFragment, boolean addToBackStack)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.purist_chat_container, newFragment,newFragment.getClass().toString());
        if (addToBackStack) {
            ft.addToBackStack(newFragment.getClass().toString());
        }
        ft.commit();
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




    //    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//    }
//
//    @Override
//    public void onPageFinished(WebView view, String url) {
//        vRemoveProgressDialog();
//    }
//
//    @Override
//    public void onChatViewDismiss() {
//        onBackPressed();
//    }
//


    private void onShowFileChooser() {
        // For Android 5.0
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("ChatActivity", "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }
//
//    @Override
//    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//        // openFileChooser for Android 3.0+
//        mUploadMessage = uploadMsg;
//        // Create AndroidExampleFolder at sdcard
//        // Create AndroidExampleFolder at sdcard
//
//        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
//
//        if (!imageStorageDir.exists()) {
//            // Create AndroidExampleFolder at sdcard
//            imageStorageDir.mkdirs();
//        }
//
//        // Create camera captured image file path and name
//        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//
//        mCapturedImageURI = Uri.fromFile(file);
//
//        // Camera capture image intent
//        final Intent captureIntent = new Intent(
//                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//
//        // Create file chooser intent
//        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//
//        // Set camera intent to file chooser
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS , new Parcelable[] { captureIntent });
//
//        // On select image call onActivityResult method of activity
//        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
//
//    }
//
//
//
//
//
//
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }



            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getDataString() == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        result = Uri.parse(mCameraPhotoPath);
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        result = Uri.parse(dataString);
                    }
                }
            }

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {
                try {
                    if (resultCode != RESULT_OK) {

                        result = null;

                    } else {

                        // retrieve from the private variable if the intent is null
                        result = data == null ? null : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
                }


            }
        }

        mCapturedImageURI = result;
        return;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mediaSendConversatonID > -1 && mCapturedImageURI != null){
            File file = null;
            boolean uploadeFile= true;
            try {
                file = new File(Utils.getPath(ChatActivity.this, mCapturedImageURI));
            } catch (URISyntaxException e) {
                Toast.makeText(ChatActivity.this, getString(R.string.purist_chat_uploading_attachment_failed), Toast.LENGTH_SHORT).show();
                uploadeFile = false;
            }

            long _1MB = 1024 * 1024;
            int maxFileSize = 10;
            if (file.length() > maxFileSize * _1MB){
                Toast.makeText(ChatActivity.this, String.format(getString(R.string.purist_chat_uploading_attachment_size_exceed), String.valueOf(maxFileSize)), Toast.LENGTH_SHORT).show();
                uploadeFile = false;
            }

            if (uploadeFile) {
                vShowProgressDialog(getString(R.string.purist_chat_uploading_attachment), true);
                purisitChat.sendMediaMessage(mediaSendConversatonID, file, new ResponseListener() {
                    @Override
                    public void onSuccessResponse(ServerResponse response) {
                        vRemoveProgressDialog();
                        Toast.makeText(ChatActivity.this, getString(R.string.purist_chat_uploading_attachment_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgressResponse(long percentage, long bytesWritten, long totalSize) {
                        super.onProgressResponse(percentage, bytesWritten, totalSize);
                        vUpdateProgressDialogPercetage((int) percentage);
                    }

                    @Override
                    public void onFailedResponse(ServerResponse response) {
                        super.onFailedResponse(response);
                        vRemoveProgressDialog();
                        Toast.makeText(ChatActivity.this, getString(R.string.purist_chat_uploading_attachment_failed), Toast.LENGTH_SHORT).show();
                    }
                });

                mediaSendConversatonID = -1;
            }
        }
    }

    public void vShowProgressDialog(String sMessage) {
       vShowProgressDialog(sMessage, false);
    }

    public void vShowProgressDialog(String sMessage, boolean isDeterminate) {
        if (pdLoadingView != null && pdLoadingView.isShowing()){
            return;
        }
        pdLoadingView = new ProgressDialog(this);
        pdLoadingView.setMessage(sMessage);
        pdLoadingView.setCancelable(false);

        if (isDeterminate) {
            pdLoadingView.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdLoadingView.setMax(100);
        }
        pdLoadingView.show();
    }

    public void vUpdateProgressDialogPercetage(int progress) {
        if (pdLoadingView != null && pdLoadingView.isShowing()){
            pdLoadingView.setProgress(progress);
        }
    }


    public void vRemoveProgressDialog() {
        if (pdLoadingView != null && pdLoadingView.isShowing()) {
            pdLoadingView.dismiss();
        }
    }


    @Override
    public void onRoomSelected(final RoomObject roomObject) {
        try {
            vShowProgressDialog(getString(R.string.purist_rooms_joining));
            boolean success = purisitChat.joinRoom(roomObject);
            if (!success){
                vRemoveProgressDialog();
                Toast.makeText(ChatActivity.this, getString(R.string.purist_rooms_join_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (!purisitChat.isConnected()){
                login(userName, password, loc_lat, loc_long, regID, new ChatConnectionListener() {

                    @Override
                    public void onSubscriptionConfirmed() {
                        super.onSubscriptionConfirmed();
                        try {
                            boolean success = purisitChat.joinRoom(roomObject);
                            if (!success){
                                vRemoveProgressDialog();
                                Toast.makeText(ChatActivity.this, getString(R.string.purist_rooms_join_failed), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e1) {
                            vRemoveProgressDialog();
                            Toast.makeText(ChatActivity.this, getString(R.string.purist_rooms_join_failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onRoomJoined(long roomID, long conversationID, ArrayList<MessageObject> recentMessages) {

                    }

                    @Override
                    public void onTextMessageReceived(long roomID, MessageObject newMessage) {

                    }

                    @Override
                    public void onRoomStatusChanged(long roomID, String status) {

                    }

                    @Override
                    public void onError(Exception e) {
                        vRemoveProgressDialog();
                        Toast.makeText(ChatActivity.this, getString(R.string.purist_rooms_join_failed), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                vRemoveProgressDialog();
                Toast.makeText(ChatActivity.this, getString(R.string.purist_rooms_join_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
            if (fullViewFrag != null){
                fullViewFrag = null;
            } else {
                chatFrag = null;
            }
        } else{
            super.onBackPressed();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        purisitChat.disconnect();
    }

    @Override
    public void onTextMessageSend(long conversationID, String message) {
        boolean success = false;
        try {
            success = purisitChat.sendTextMessage(conversationID, message);
            if (!success){
                Toast.makeText(ChatActivity.this, getString(R.string.purist_rooms_msg_sent_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (!purisitChat.isConnected()){
                login(userName, password, loc_lat, loc_long, regID, new ChatConnectionListener() {

                    @Override
                    public void onSubscriptionConfirmed() {
                        super.onSubscriptionConfirmed();
                        Toast.makeText(ChatActivity.this, getString(R.string.purist_chat_send_msg_hint), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRoomJoined(long roomID, long conversationID, ArrayList<MessageObject> recentMessages) {

                    }

                    @Override
                    public void onTextMessageReceived(long roomID, MessageObject newMessage) {

                    }

                    @Override
                    public void onRoomStatusChanged(long roomID, String status) {

                    }

                    @Override
                    public void onError(Exception e) {
                        vRemoveProgressDialog();
                        Toast.makeText(ChatActivity.this, getString(R.string.purist_chat_send_msg_hint), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(ChatActivity.this, getString(R.string.purist_chat_send_msg_hint), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onMediaMessageSend(long conversationID) {
        mediaSendConversatonID = conversationID;
        onShowFileChooser();
    }

    @Override
    public void onShowImageFullView(String fileURL) {
        fullViewFrag = new FullViewFrag();
        Bundle bundle = new Bundle();
        bundle.putString("fileURL", fileURL);
        fullViewFrag.setArguments(bundle);

        replaceFragment(fullViewFrag, true);
    }


    @Override
    public void onDownloadFile(String fileURL, final String fileName) {
      final int notifID = 1000;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.purist_general_downloading))
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentText(fileName)
                .setWhen(System.currentTimeMillis())
                .setProgress(100, 0,false);

        Notification oNotification = builder.build();
        notificationManager.notify(notifID, oNotification);
        
        purisitChat.downloadFile(fileURL, fileName, new ResponseListener() {
            @Override
            public void onSuccessResponse(ServerResponse response) {
                notificationManager.cancel(notifID);
                Toast.makeText(ChatActivity.this, getString(R.string.purist_general_downloading_completed), Toast.LENGTH_SHORT).show();

                Intent oIntent = new Intent();
                String filePath = ((File) response.getData()).getPath();
                String mime = "*/*";
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                if (mimeTypeMap.hasExtension(mimeTypeMap.getFileExtensionFromUrl(filePath))) {
                    mime = mimeTypeMap.getMimeTypeFromExtension(mimeTypeMap.getFileExtensionFromUrl(filePath));
                }
                oIntent.setAction(Intent.ACTION_VIEW);
                oIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri fileURI = Uri.fromFile((File) response.getData());
                oIntent.setDataAndType(fileURI, mime);


                PendingIntent oPendingIntent = PendingIntent.getActivity(ChatActivity.this, 1000, oIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatActivity.this);
                builder.setContentTitle(getString(R.string.purist_general_download_completed))
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentText(fileName)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(oPendingIntent);

                Notification oNotification = builder.build();
                oNotification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(10, oNotification);
            }

            @Override
            public void onFailedResponse(ServerResponse response) {
                super.onFailedResponse(response);
                notificationManager.cancel(notifID);
                Toast.makeText(ChatActivity.this, getString(R.string.purist_general_downloading_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressResponse(long percentage, long bytesWritten, long totalSize) {
                super.onProgressResponse(percentage, bytesWritten, totalSize);

                if (System.currentTimeMillis() - notificationLastTime > 400){
                    notificationLastTime = System.currentTimeMillis();

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatActivity.this);
                    builder.setContentTitle(getString(R.string.purist_general_downloading))
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setContentText(fileName)
                            .setWhen(System.currentTimeMillis())
                            .setProgress(100, (int) percentage,false);

                    Notification oNotification = builder.build();
                    notificationManager.notify(notifID, oNotification);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.purist_btn_purist_retry) {
            vShowProgressDialog(getString(R.string.purist_login_please_wait));
            login(userName, password, loc_lat, loc_long, regID);
        }
    }
}
