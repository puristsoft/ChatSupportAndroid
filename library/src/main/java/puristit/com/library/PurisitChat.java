package puristit.com.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import puristit.com.entities.ChatMsgSkin;
import puristit.com.entities.LoginObject;
import puristit.com.entities.MessageObject;
import puristit.com.entities.RoomObject;
import puristit.com.listeners.ChatConnectionListener;
import puristit.com.listeners.ResponseListener;
import puristit.com.server_request.ServerResponse;
import puristit.com.utilities.Utils;

/**
 * Created by Anas on 6/10/2016.
 * <p/>
 * This is a singleton class. it is the one responsible of Purist it Chat
 */
public class PurisitChat {

    private static PurisitChat instance;
    public static ChatActivityOptions chatUIOptions;

    private Context mContext;
    private RequestManager mRequestManager;
    private WebSocketClient mWebSocketClient;
    private Handler mHandler;
    private boolean isConnected = false;
    private LoginObject loginObject;


    public synchronized static PurisitChat getInstance(Context context, String API_KEY) {
        if (instance == null) {
            instance = new PurisitChat(context, API_KEY);
        }
        return instance;
    }


    private PurisitChat(Context context, String API_KEY) {
        this.mContext = context;
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new RuntimeException(new Exception("API_KEY must not be null or Empty"));
        }
        this.mRequestManager = RequestManager.getInstance(mContext, API_KEY);
        mHandler = new Handler();
    }


//    /**
//     * This Method is used to register user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     *
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void register(String userName, String password, ResponseListener listener) {
//        register(userName, password, null, 0, 0, listener);
//    }
//
//
//    /**
//     * This Method is used to register user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param nickName 15 Char nick name for the user, if null or empty @userName will be used
//     *
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void register(String userName, String password, String nickName, ResponseListener listener) {
//        if (nickName == null || nickName.isEmpty()){
//            nickName = userName;
//        }
//        mRequestManager.register(userName, password, nickName, 0, 0, listener);
//    }
//
//    /**
//     * This Method is used to register user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param loc_lat  user Latitude location
//     * @param loc_long user Longitude location
//     *
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void register(String userName, String password, double loc_lat, double loc_long, ResponseListener listener) {
//        register(userName, password, null, loc_lat, loc_long, listener);
//    }
//
//
//
//
//    /**
//     * This Method is used to register user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param nickName 15 Char nick name for the user, if null or empty @userName will be used
//     * @param loc_lat  user Latitude location
//     * @param loc_long user Longitude location
//     *
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void register(String userName, String password, String nickName, double loc_lat, double loc_long, ResponseListener listener) {
//        if (nickName == null || nickName.isEmpty()){
//            nickName = userName;
//        }
//        mRequestManager.register(userName, password, nickName, loc_lat, loc_long, listener);
//    }


    /**
     * This Method is used to start Chat activity, it will do all the magic or you, just call this method and put some customizations
     * using Builder and enjoy.
     *
     * @param API_KEY  Purist API KEY
     * @param userName the user name used to login
     * @param password the password of the user
     * @param loc_lat  user Latitude location
     * @param loc_long user Longitude location
     * @param regID    TokenID for Google push notification
     */
    public static void startChatActivity(Context context, String API_KEY, String userName, String password, double loc_lat, double loc_long, String regID) {
        startChatActivity(context, API_KEY, userName, password, loc_lat, loc_long, regID, null, -1);
    }


    /**
     * This Method is used to start Chat activity, it will do all the magic or you, just call this method and put some customizations
     * using Builder and enjoy.
     *
     * @param API_KEY             Purist API KEY
     * @param userName            the user name used to login
     * @param password            the password of the user
     * @param loc_lat             user Latitude location
     * @param loc_long            user Longitude location
     * @param regID               TokenID for Google push notification
     * @param chatActivityOptions UI Options for chat activity
     */
    public static void startChatActivity(Context context, String API_KEY, String userName, String password, double loc_lat, double loc_long, String regID, ChatActivityOptions chatActivityOptions) {
        startChatActivity(context, API_KEY, userName, password, loc_lat, loc_long, regID, chatActivityOptions, -1);
    }


    /**
     * This Method is used to start Chat activity, it will do all the magic or you, just call this method and put some customizations
     * using Builder and enjoy.
     *
     * @param API_KEY             Purist API KEY
     * @param userName            the user name used to login
     * @param password            the password of the user
     * @param loc_lat             user Latitude location
     * @param loc_long            user Longitude location
     * @param regID               TokenID for Google push notification
     * @param roomID              the ID of the room to open chat in
     */
    public static void startChatActivity(Context context, String API_KEY, String userName, String password, double loc_lat, double loc_long, String regID, long roomID) {
        startChatActivity(context, API_KEY, userName, password, loc_lat, loc_long, regID, null, roomID);
    }
    /**
     * This Method is used to start Chat activity, it will do all the magic or you, just call this method and put some customizations
     * using Builder and enjoy.
     *
     * @param API_KEY             Purist API KEY
     * @param userName            the user name used to login
     * @param password            the password of the user
     * @param loc_lat             user Latitude location
     * @param loc_long            user Longitude location
     * @param regID               TokenID for Google push notification
     * @param chatActivityOptions UI Options for chat activity
     * @param roomID              the ID of the room to open chat in
     */
    public static void startChatActivity(Context context, String API_KEY, String userName, String password, double loc_lat, double loc_long, String regID, ChatActivityOptions chatActivityOptions, long roomID) {
        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.putExtra("API_KEY", API_KEY);
        chatIntent.putExtra("userName", userName);
        chatIntent.putExtra("password", password);
        chatIntent.putExtra("loc_lat", loc_lat);
        chatIntent.putExtra("loc_long", loc_long);
        chatIntent.putExtra("regID", regID);
        if (roomID > -1) {
            chatIntent.putExtra("roomID", roomID);
        }
        chatUIOptions = chatActivityOptions;

        context.startActivity(chatIntent);
    }


    /**
     * This Method is used to login user.
     *
     * @param userName the user name used to login
     * @param password the password of the user
     * @param loc_lat  user Latitude location
     * @param loc_long user Longitude location
     * @param regID    TokenID for Google push notification
     * @param listener Response listener, this listener will return the result of the action
     */
    public void login(String userName, String password, double loc_lat, double loc_long, String regID, final ResponseListener listener) {
        mRequestManager.login(userName, password, loc_lat, loc_long, regID, new ResponseListener() {
            @Override
            public void onSuccessResponse(final ServerResponse response) {

                try {
                    loginObject = Utils.ResponseParser((JSONObject) response.getData(), LoginObject.class);
                    if (loginObject != null) {
                        response.setData(loginObject);
                        if (listener != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onSuccessResponse(response);
                                }
                            });
                        }
                    } else {
                        throw new RuntimeException("error parsing Login object");
                    }
                } catch (Exception e) {
                    ServerResponse serverResponse = new ServerResponse();
                    serverResponse.setErroCode(ServerResponse.ErrorTypes.GeneralError);
                    serverResponse.setErrorMsg(e.getMessage());
                    onFailedResponse(serverResponse);
                }
            }

            @Override
            public void onFailedResponse(final ServerResponse response) {
                super.onFailedResponse(response);
                if (listener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailedResponse(response);
                        }
                    });
                }
            }
        });
    }


    public void connect(String chatUrl, String accessToken, ChatConnectionListener listener) {
        connect(chatUrl, accessToken, true, listener);
    }

    public void connect(String chatUrl, String accessToken, final boolean writeLogs, final ChatConnectionListener listener) {

        if (isConnected()) {
            if (listener != null) {
                listener.onConnectionOpened();
                listener.onSubscriptionConfirmed();
            }
            return;
        }

        URI uri;
        try {
            StringBuilder url = new StringBuilder();
            url.append(chatUrl);
            url.append("?access_token=").append(accessToken);
            uri = new URI(url.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e);
            }
            return;
        }
        Map<String, String> headers = new HashMap<>();
        mWebSocketClient = new WebSocketClient(uri, new Draft_6455(), headers, 0) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                isConnected = true;
                if (writeLogs) {
                    Log.d("Websocket", "Opened");
                }

                if (listener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onConnectionOpened();
                        }
                    });
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("command", "subscribe");
                    jsonObject.put("identifier", "{\"channel\":\"ChatChannel\"}");
                    mWebSocketClient.send(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String s) {
                handleMessage(s, writeLogs, listener);
            }

            @Override
            public void onClose(int code, String s, boolean b) {
                isConnected = false;
                if (writeLogs) {
                    Log.i("Websocket", code + ": Closed " + s);
                }

                if (listener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onConnectionClosed();
                        }
                    });
                }
            }

            @Override
            public void onError(final Exception e) {
                isConnected = false;
                if (writeLogs) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
                if (listener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        };
        try {
            mWebSocketClient.connect();
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e);
            }
        }

    }

    private void handleMessage(String msg, boolean writeLogs, final ChatConnectionListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String type = jsonObject.optString("type");
            if (!(type == null || type.isEmpty())) {
                if (type.equals("ping")) {
                    if (listener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPing();
                            }
                        });
                    }
                } else {
                    if (writeLogs) {
                        Log.i("Websocket", "onMessage" + msg);
                    }
                }

                if (type.equals("confirm_subscription")) {
                    if (listener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSubscriptionConfirmed();
                            }
                        });
                    }
                }
            }

            final JSONObject message = jsonObject.optJSONObject("message");
            if (message != null) {
                Log.i("Websocket", "onMessage" + msg);
                String event = message.optString("event");
                if (event.equals("latest_as_bulk")) {
                    if (listener != null) {
                        JSONArray messages = message.getJSONArray("messages");
                        final ArrayList<MessageObject> recentMessages = Utils.ResponseParserAsArrayList(messages, MessageObject.class);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onRoomJoined(message.optLong("room_id"), message.optLong("conversation_id"), recentMessages);
                            }
                        });
                    }
                }
                if (event.equals("message")) {
                    if (listener != null) {
                        final MessageObject messageObject = Utils.ResponseParser(message, MessageObject.class);
                        if (messageObject.getBody() != null && !messageObject.getBody().isEmpty()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onTextMessageReceived(message.optLong("room_id"), messageObject);
                                }
                            });
                        }
                    }
                }
                if (event.equals("status") && listener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRoomStatusChanged(message.optLong("status_room_id"), message.optString("status"));
                        }
                    });

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * this method is used to join into certain room
     *
     * @param roomObject the room you wish to join in
     * @return <Code>true</Code> if Join room requested successfully, <Code>false</Code> if not
     * @throws Exception if there is problem in the connection
     */
    public boolean joinRoom(RoomObject roomObject) throws Exception {
        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("command", "message");
            sendJsonObject.put("identifier", "{\"channel\":\"ChatChannel\"}");
            JSONObject data = new JSONObject();
            data.put("action", "join_room");
            data.put("room_id", roomObject.getId());
            sendJsonObject.put("data", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        if (isConnected) {
            try {
                mWebSocketClient.send(sendJsonObject.toString());
                return true;
            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                throw new Exception("Connection is closed,  you might consider reconnect or login again");
            }
        } else {
            throw new Exception("Connection is closed,  you might consider reconnect or login again");
        }
    }


    /**
     * this method is used to send Text chat message
     *
     * @param conversationID the converation which the message will be sent to
     * @param message        the message text
     * @return <Code>true</Code> if message is sent successfully, <Code>false</Code> if not
     * @throws Exception if there is problem in the connection
     */
    public boolean sendTextMessage(long conversationID, String message) throws Exception {
        JSONObject sendJsonObject = new JSONObject();
        try {
            sendJsonObject.put("command", "message");
            sendJsonObject.put("identifier", "{\"channel\":\"ChatChannel\"}");
            JSONObject data = new JSONObject();
            data.put("action", "post_message");
            data.put("conversation_id", conversationID);
            data.put("body", message);
            sendJsonObject.put("data", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        if (isConnected) {
            try {
                mWebSocketClient.send(sendJsonObject.toString());
                return true;
            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                throw new Exception("Connection is closed,  you might consider reconnect or login again");
            }
        } else {
            throw new Exception("Connection is closed,  you might consider reconnect or login again");
        }
    }


    /**
     * this method is used to send Text chat message
     *
     * @param conversationID the converation which the message will be sent to
     * @param message        the message text
     * @return <Code>true</Code> if message is sent successfully, <Code>false</Code> if not
     * @throws Exception if there is problem in the connection
     */
    public void sendMediaMessage(long conversationID, File file, ResponseListener listener){
        if (isConnected && loginObject != null) {
            mRequestManager.uploadFile(loginObject.getAccess_token(), conversationID, file, listener);
        } else {
            listener.onFailedResponse(new ServerResponse().setErrorMsg("Connection is closed,  you might consider reconnect or login again"));
        }
    }


    public void disconnect() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "unsubscribe");
            jsonObject.put("identifier", "{\"channel\":\"ChatChannel\"}");
            if (mWebSocketClient != null && isConnected()) {
                mWebSocketClient.send(jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }


    protected void downloadFile(String url, String fileName, final ResponseListener listener) {
        mRequestManager.downloadFile(url, fileName, listener);
    }

    //    /**
//     * This Method is used to Initialize user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param regID    TokenID for Google push notification
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void initialize(String userName, String password, String regID, ResponseListener listener) {
//        initialize(userName, password, regID, 168, 0, 0, "en", listener);
//    }
//
//
//    /**
//     * This Method is used to Initialize user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param regID    TokenID for Google push notification
//     * @param validity Validity of the chat URL in hours. Default is 168 hours (7 days). Maximum value for this field is 720 hours (30 days).
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void initialize(String userName, String password, String regID, int validity, ResponseListener listener) {
//        initialize(userName, password, regID, validity, 0, 0, "en", listener);
//    }
//
//
//    /**
//     * This Method is used to Initialize user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param validity Validity of the chat URL in hours. Default is 168 hours (7 days). Maximum value for this field is 720 hours (30 days).
//     * @param regID    TokenID for Google push notification
//     * @param loc_lat  user Latitude location
//     * @param loc_long user Longitude location
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void initialize(String userName, String password, String regID, int validity, double loc_lat, double loc_long, ResponseListener listener) {
//        initialize(userName, password, regID, validity, loc_lat, loc_long, "en", listener);
//    }
//
//
//    /**
//     * This Method is used to Initialize user.
//     *
//     * @param userName the user name used to register
//     * @param password the password of the user
//     * @param validity Validity of the chat URL in hours. Default is 168 hours (7 days). Maximum value for this field is 720 hours (30 days).
//     * @param regID    TokenID for Google push notification
//     * @param loc_lat  user Latitude location
//     * @param loc_long user Longitude location
//     * @param language ISO language ID, en=English ,fr=French
//     * @param listener Response listener, this listener will return the result of the action
//     */
//    public void initialize(String userName, String password, String regID, int validity, double loc_lat, double loc_long, String language, ResponseListener listener) {
//        if (validity == 0) {
//            validity = 168; //default Value
//        }
//        if (validity > 720) {
//            validity = 720;
//        }
//        mRequestManager.initialize(userName, password, validity, regID, loc_lat, loc_long, language, listener);
//    }
//
//
//    /**
//     * Notify the host application that the Chat Url is not valid, a Initialize Api must be called
//     * to generate a new Chat URL.
//     *
//     * @param chatUrl  the chat url that will be checked for validation.
//     * @param listener a listener will return the response of the validation action.
//     */
//    public void checkURLExpiration(String chatUrl, final URLValidationListener listener) {
//        //Check is the Chat Url is still valid
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.setURLEncodingEnabled(false);
//        client.setConnectTimeout(1000);
//
//        //TODO as discussed with Emran, this code is committed to test with FACT
//        //TODO it must be removed on release
//        client.setSSLSocketFactory(
//                new SSLSocketFactory(getSslContext(),
//                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
//
//        client.get(chatUrl, new TextHttpResponseHandler() {
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == HttpURLConnection.HTTP_NOT_FOUND && listener != null) {
//                    listener.onURLExpired();
//                } else {
//                    listener.onURLValid();
//                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                if (listener != null) {
//                    listener.onURLValid();
//                }
//            }
//        });
//    }
//
//
//    public SSLContext getSslContext() {
//
//        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
//            public X509Certificate[] getAcceptedIssuers() {
//                return new X509Certificate[0];
//            }
//
//            public void checkClientTrusted(X509Certificate[] chain, String authType) {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain, String authType) {
//            }
//        }};
//
//        SSLContext sslContext = null;
//
//        try {
//            sslContext = SSLContext.getInstance("TLS");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        try {
//            sslContext.init(null, byPassTrustManagers, new SecureRandom());
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//
//        return sslContext;
//    }


    public static class ChatActivityOptions {

        int backgroundColor = Color.parseColor("#f4f8ff");
        Drawable backgroundDrawable = null;
        ChatMsgSkin myMsgSkin = new ChatMsgSkin();
        ChatMsgSkin OthersMsgSkin = new ChatMsgSkin();
        int chatPageFooterBackgroundColor = Color.parseColor("#EEEEEE");


        int getBackgroundColor() {
            return backgroundColor;
        }

        Drawable getBackgroundDrawable() {
            return backgroundDrawable;
        }

        ChatMsgSkin getMyMsgSkin() {
            return myMsgSkin;
        }

        ChatMsgSkin getOthersMsgSkin() {
            return OthersMsgSkin;
        }

        int getChatPageFooterBackgroundColor() {
            return chatPageFooterBackgroundColor;
        }

        /**
         * this method used to change the background color of chat activity, default color is {@<code>f4f8ff</code>}
         * if {@link #setBackgroundDrawable(Drawable)}} is set then this method will be useless
         *
         * @param backgroundColor the color of the background
         */
        public ChatActivityOptions setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * this method used to change the background drawable of chat activity
         * this method will override the changes in {@link #setBackgroundColor(int)}
         *
         * @param backgroundDrawable the drawable of the background
         */
        public ChatActivityOptions setBackgroundDrawable(Drawable backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
            return this;
        }

        /**
         * this method is used to change the skin and UI of the messages send from the current user
         *
         * @param myMsgSkin the skin of the message
         */
        public ChatActivityOptions setMyMsgSkin(ChatMsgSkin myMsgSkin) {
            this.myMsgSkin = myMsgSkin;
            return this;
        }

        /**
         * this method is used to change the skin and UI of the messages send from the other users or
         * operators
         *
         * @param othersMsgSkin the skin of the message
         */
        public ChatActivityOptions setOthersMsgSkin(ChatMsgSkin othersMsgSkin) {
            OthersMsgSkin = othersMsgSkin;
            return this;
        }


        /**
         * this method used to change the background color of chat page footer, default color is {@<code>EEEEEE</code>}
         *
         * @param chatPageFooterBackgroundColor the color of the background
         */
        public ChatActivityOptions setChatPageFooterBackgroundColor(int chatPageFooterBackgroundColor) {
            this.chatPageFooterBackgroundColor = chatPageFooterBackgroundColor;
            return this;
        }


        //        public String build() {
//
//            ChatActivityBuilder chatActivityBuilder = new ChatActivityBuilder();
//
//            Gson gson = new Gson();
//
//            JSONObject builder = new JSONObject();
//            try {
//                builder.put("backgroundColor", backgroundColor);
//                builder.put("backgroundDrawableResID", backgroundDrawableResID);
//                builder.put("myMsgSkin", gson.toJson(myMsgSkin));
//                builder.put("OthersMsgSkin", gson.toJson(OthersMsgSkin));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return builder.toString();
//        }
    }
}
