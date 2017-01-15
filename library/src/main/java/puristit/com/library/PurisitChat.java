package puristit.com.library;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.net.HttpURLConnection;

import cz.msebera.android.httpclient.Header;
import puristit.com.listeners.ResponseListener;
import puristit.com.listeners.URLValidationListener;
import puristit.com.widget.ChatView;

/**
 * Created by Anas on 6/10/2016.
 * <p/>
 * This is a singleton class. it is the one responsible of Purist it Chat
 */
public class PurisitChat {

    private static PurisitChat instance;

    private Context mContext;
    private RequestManager mRequestManager;


    public synchronized static PurisitChat getInstance(Context context, String API_KEY) {
        if (instance == null) {
            instance = new PurisitChat(context, API_KEY);
        }
        return instance;
    }


    public PurisitChat(Context context, String API_KEY) {
        this.mContext = context;
        if (API_KEY == null || API_KEY.isEmpty()){
            throw new RuntimeException(new Exception("API_KEY must not be null or Empty"));
        }
        this.mRequestManager = RequestManager.getInstance(mContext, API_KEY);
//        xmlResourceParser.getProperty()
    }




    /**
     * This Method is used to register user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void register(String userName, String password, ResponseListener listener) {
        register(userName, password, null, 0, 0, listener);
    }


    /**
     * This Method is used to register user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param nickName 15 Char nick name for the user, if null or empty @userName will be used
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void register(String userName, String password, String nickName, ResponseListener listener) {
        if (nickName == null || nickName.isEmpty()){
            nickName = userName;
        }
        mRequestManager.register(userName, password, nickName, 0, 0, listener);
    }

    /**
     * This Method is used to register user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param loc_lat  user Latitude location
     * @param loc_long user Longitude location
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void register(String userName, String password, double loc_lat, double loc_long, ResponseListener listener) {
        register(userName, password, null, loc_lat, loc_long, listener);
    }




    /**
     * This Method is used to register user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param nickName 15 Char nick name for the user, if null or empty @userName will be used
     * @param loc_lat  user Latitude location
     * @param loc_long user Longitude location
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void register(String userName, String password, String nickName, double loc_lat, double loc_long, ResponseListener listener) {
        if (nickName == null || nickName.isEmpty()){
            nickName = userName;
        }
        mRequestManager.register(userName, password, nickName, loc_lat, loc_long, listener);
    }







    /**
     * This Method is used to Initialize user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param regID TokenID for Google push notification
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void initialize(String userName, String password, String regID, ResponseListener listener) {
        initialize(userName, password, regID, 168, 0, 0, "en", listener);
    }


    /**
     * This Method is used to Initialize user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param regID TokenID for Google push notification
     * @param validity Validity of the chat URL in hours. Default is 168 hours (7 days). Maximum value for this field is 720 hours (30 days).
     *
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void initialize(String userName, String password, String regID, int validity, ResponseListener listener) {
        initialize(userName, password, regID, validity, 0, 0, "en", listener);
    }


    /**
     * This Method is used to Initialize user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param validity Validity of the chat URL in hours. Default is 168 hours (7 days). Maximum value for this field is 720 hours (30 days).
     * @param regID TokenID for Google push notification
     * @param loc_lat  user Latitude location
     * @param loc_long user Longitude location
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void initialize(String userName, String password, String regID, int validity, double loc_lat, double loc_long, ResponseListener listener) {
        initialize(userName, password, regID, validity, loc_lat, loc_long, "en", listener);
    }




    /**
     * This Method is used to Initialize user.
     *
     * @param userName the user name used to register
     * @param password the password of the user
     * @param validity Validity of the chat URL in hours. Default is 168 hours (7 days). Maximum value for this field is 720 hours (30 days).
     * @param regID TokenID for Google push notification
     * @param loc_lat  user Latitude location
     * @param loc_long user Longitude location
     * @param language ISO language ID, en=English ,fr=French
     *
     * @param listener Response listener, this listener will return the result of the action
     */
    public void initialize(String userName, String password, String regID, int validity, double loc_lat, double loc_long, String language, ResponseListener listener) {
        if (validity ==0 ){
            validity = 168; //default Value
        }
        if (validity > 720 ){
            validity = 720;
        }
        mRequestManager.initialize(userName, password, validity, regID, loc_lat, loc_long, language, listener);
    }





    /**
     * Notify the host application that the Chat Url is not valid, a Initialize Api must be called
     * to generate a new Chat URL.
     *
     * @param chatUrl the chat url that will be checked for validation.
     * @param listener a listener will return the response of the validation action.
     *
     * */
    public void checkURLExpiration(String chatUrl, final URLValidationListener listener){
        //Check is the Chat Url is still valid
        AsyncHttpClient client = new AsyncHttpClient();
        client.setURLEncodingEnabled(false);
        client.setConnectTimeout(1000);
        client.get(chatUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == HttpURLConnection.HTTP_NOT_FOUND && listener != null) {
                    listener.onURLExpired();
                } else {
                    listener.onURLValid();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (listener != null){
                    listener.onURLValid();
                }
            }
        });
    }





    public static class URLBuilder {

        String url;
        String platform = "ANDROID";
        String GCM_Id = "";
        String room;
        boolean roomlistEnabled = false;
        boolean headerEnabled = true;
        String bgcolor;
        String fgcolor;
        String headercolor;


        public URLBuilder(String url) {
            this.url = url;
        }



        /**
         * set push notification ID as per the platform specficiations Google Firebase.
         *
         * @param GCM_Id the puch notification Id.
         * */
        public URLBuilder setGCM_Id(String GCM_Id) {
            this.GCM_Id = GCM_Id;
            return this;
        }


        /**
         * force user to one chat support department or support catagogries example: sales , billing ,
         * support case senstive as you have defined in your admin
         *
         * @param room the room that will be directed to
         * */
        public URLBuilder setRoom(String room) {
            this.room = room;
            return this;
        }


        /**
         * enable/disable rooms (support departments) drop list of support catagogries for user to select what he want
         *
         * @param roomlistEnabled enable/disable drop list of rooms.
         * */
        public URLBuilder setRoomlistEnabled(boolean roomlistEnabled) {
            this.roomlistEnabled = roomlistEnabled;
            return this;
        }


        /**
         * enable/disable header bar with Done button to close chat window
         *
         * @param headerEnabled enable/disable header bar.
         * */
        public URLBuilder setHeaderEnabled(boolean headerEnabled) {
            this.headerEnabled = headerEnabled;
            return this;
        }


        /**
         * Set background color of your chat window.
         *
         * @param bgcolor  background color
         * */
        public URLBuilder setBgcolor(int bgcolor) {
            this.bgcolor = Integer.toHexString(bgcolor);
            this.bgcolor = this.bgcolor.length() > 6 ? (this.bgcolor.substring(2, this.bgcolor.length())) : this.bgcolor;
            return this;
        }



        /**
         * Set foreground color of your chat window.
         *
         * @param fgcolor  foreground color
         * */
        public URLBuilder setFgcolor(int fgcolor) {
            this.fgcolor = Integer.toHexString(fgcolor);
            this.fgcolor = this.fgcolor.length() > 6 ? (this.fgcolor.substring(2, this.fgcolor.length())) : this.fgcolor;
            return this;
        }


        /**
         * Set header color of your chat window.
         *
         * @param headercolor  header color
         * */
        public URLBuilder setHeadercolor(int headercolor) {
            this.headercolor = Integer.toHexString(headercolor);
            this.headercolor = this.headercolor.length() > 6 ? (this.headercolor.substring(2, this.headercolor.length())) : this.headercolor;
            return this;
        }


        public String build(){
            StringBuilder chatUrl = new StringBuilder();
            chatUrl.append(url);
            chatUrl.append("?registration_id=").append(GCM_Id);
            chatUrl.append("&platform=").append(platform);
            chatUrl.append("&header=").append(headerEnabled ? 1 : 0);
            chatUrl.append("&roomlist=").append(roomlistEnabled ? 1 : 0);

            if (room != null && !room.isEmpty()){
                chatUrl.append("&room=").append(room);
            }

            if (bgcolor != null && !bgcolor.isEmpty()){
                chatUrl.append("&bgcolor=").append(bgcolor);
            }

            if (fgcolor != null && !fgcolor.isEmpty()){
                chatUrl.append("&fgcolor=").append(fgcolor);
            }

            if (headercolor != null && !headercolor.isEmpty()){
                chatUrl.append("&headercolor=").append(headercolor);
            }

            return chatUrl.toString();
        }
    }
}
