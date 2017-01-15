package puristit.com.library;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import cz.msebera.android.httpclient.Header;
import puristit.com.listeners.ResponseListener;
import puristit.com.server_request.ServerResponse;

/**
 * Created by Anas Bakez on 9/9/2015.
 * <p>
 * this class will handle HTTP requests to server
 */
public class RequestManager {

    private static RequestManager mInstance;
    private static Context mContext;
    private AsyncHttpClient client;
    private SyncHttpClient SyncClient;
    private String API_KEY = "";

    private String TAG = "Purist it request";
    private final String serverURL = "https://api.puristit.com";

    private RequestManager(Context context, String API_KEY) {
        mContext = context;
        this.API_KEY = API_KEY;

        ///. asynchronous call on ui thread
        client = new AsyncHttpClient();
        client.setURLEncodingEnabled(false);
        client.setMaxRetriesAndTimeout(3, 500); // this is the timeout between the retries
        client.setConnectTimeout(1000);
        client.setResponseTimeout(80000);
        ///.
        SyncClient = new SyncHttpClient();
        SyncClient.setURLEncodingEnabled(false);
        SyncClient.setMaxRetriesAndTimeout(3, 500); // this is the timeout between the retries
        SyncClient.setConnectTimeout(1000);
        SyncClient.setResponseTimeout(80000);

    }

    protected static synchronized RequestManager getInstance(Context context, String API_KEY) {
        if (mInstance == null) {
            mInstance = new RequestManager(context, API_KEY);
        }
        return mInstance;
    }



    private String getURL(String apiName) {
        return serverURL + apiName;
    }


    // ---------------- Requests -----------------------------------------------
    protected void register(String userName, String password, String nickName, double loc_lat, double loc_long, final ResponseListener listener) {
        String apiName = "/register";
        try{
            RequestParams params = new RequestParams();
            params.add("username", userName);
            params.add("password", password);
            params.add("name", nickName);
            params.add("loc_lat", String.valueOf(loc_lat));
            params.add("loc_Long", String.valueOf(loc_long));
            params.add("platform", "ANDROID");

            client.setBasicAuth(API_KEY, "");
            POST_Request(getURL(apiName), params, new ResponseListener() {
                @Override
                public void onSuccessResponse(ServerResponse response) {
                    if (listener != null) {
                        listener.onSuccessResponse(response);
                    }
                }

                @Override
                public void onFailedResponse(ServerResponse response) {
                    super.onFailedResponse(response);
                    if (listener != null) {
                        listener.onFailedResponse(response);
                    }
                }
            });
        } catch (Exception e){
            if (listener != null){
                ServerResponse response = new ServerResponse();
                response.setErroCode(ServerResponse.ErrorTypes.GeneralError);
                response.setErrorMsg(e.getMessage());
                listener.onFailedResponse(response);
            }
        }



    }



    protected void initialize(String userName, String password, int validity, String regID, double loc_lat, double loc_long, String language, final ResponseListener listener) {
        String apiName = "/initialize";
        try{
            RequestParams params = new RequestParams();
            params.add("p_username", userName);
            params.add("platform", "ANDROID");
            params.add("validity", String.valueOf(validity));
            params.add("registration_id", regID);
            params.add("loc_lat", String.valueOf(loc_lat));
            params.add("loc_Long", String.valueOf(loc_long));
            params.add("language", language);

            client.setBasicAuth(API_KEY, password);
            POST_Request(getURL(apiName), params, new ResponseListener() {
                @Override
                public void onSuccessResponse(ServerResponse response) {
                    if (listener != null) {
                        listener.onSuccessResponse(response);
                    }
                }

                @Override
                public void onFailedResponse(ServerResponse response) {
                    super.onFailedResponse(response);
                    if (listener != null) {
                        listener.onFailedResponse(response);
                    }
                }
            });
        } catch (Exception e){
            if (listener != null){
                ServerResponse response = new ServerResponse();
                response.setErroCode(ServerResponse.ErrorTypes.GeneralError);
                response.setErrorMsg(e.getMessage());
                listener.onFailedResponse(response);
            }
        }
    }










    /**
     * this is the POST (Async) Request method, this method is used to performe POST request and return the Data as Json structure (JsonObject/JsonArray)
     *
     * @param Url             this is the Url of the Request
     * @param params          this is the request Parameters that will be sent to the Backend API
     * @param listener        this is the response listener that is returned ater finishing the request
     */
    private void POST_Request(String Url, RequestParams params, final ResponseListener listener) {
        Log.i(TAG, "Request:: paramsList = " + String.valueOf(params));
        try {
            client.post(Url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        handleSuccessResponse(response, listener);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (listener != null) {
                        handleSuccessResponse(response, listener);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);
                    if (listener != null) {
                        handleSuccessResponse(responseString, listener);
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    handleFailedResponse(statusCode, throwable, listener);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    handleFailedResponse(statusCode, errorResponse, throwable, listener);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    handleFailedResponse(statusCode, throwable, listener);
                }
            });
        } catch (Exception e) {
            handleFailedResponse(-100, e, listener);
        }
    }








    /**
     * this method is used to handle the (Http success) response.
     * in this method we check the actual response from our server and determine if the response is success or failure
     *
     * @param data            the response data that is returned. it is of type object to handle every data type.
     * @param listener        this is the response listener that is returned ater finishing the request
     */
    private void handleSuccessResponse(Object data, ResponseListener listener) {

        ServerResponse response = new ServerResponse();

        if (isSuccess(data)) {
            response.setErroCode(ServerResponse.ErrorTypes.Success).setData(data);
            Log.i(TAG, "handleSuccessResponse:: Success :: data = " + String.valueOf(data));
            if (listener != null) {
                listener.onSuccessResponse(response);
            }
        } else {
            response.setErroCode(ServerResponse.ErrorTypes.ServerError).setData(((JSONObject) data));
            Log.i(TAG, "handleSuccessResponse:: Failed:: " + "data = " + String.valueOf(data));
            if (listener != null) {
                listener.onFailedResponse(response);
            }
        }

    }


    private void handleFailedResponse(int ErrorCode, Throwable throwable, ResponseListener listener) {
        handleFailedResponse(ErrorCode, null, null, throwable, listener);
    }

    private void handleFailedResponse(int ErrorCode, Object data, Throwable throwable, ResponseListener listener) {
        handleFailedResponse(ErrorCode, data, null, throwable, listener);
    }


    /**
     * this method is used to handle the Failed response (Http failed or Server failed)
     *
     * @param ErrorCode       the error code returned from the response (it is returned form jLoop httpAsync Library)
     * @param data            the response data that is returned. it is of type object to handle every data type.
     * @param Header          the response Header that is returned. it is of type object to handle every data type.
     * @param throwable       the throwable that cause the failure (it is returned form jLoop httpAsync Library)
     * @param listener        this is the response listener that is returned ater finishing the request
     */
    private void handleFailedResponse(int ErrorCode, Object data, Object Header, Throwable throwable, ResponseListener listener) {
        ServerResponse response = new ServerResponse();

        switch (ErrorCode) {
            case 0:
            case HttpURLConnection.HTTP_NOT_FOUND:
                response.setErroCode(ServerResponse.ErrorTypes.NetworkError).setErrorMsg(throwable.getMessage()).setData(data);
                break;
            case HttpURLConnection.HTTP_PAYMENT_REQUIRED:   // These are for Business Logic errors
            case HttpURLConnection.HTTP_BAD_REQUEST:
            case HttpURLConnection.HTTP_UNAUTHORIZED:
            case 422:
                response.setErroCode(ServerResponse.ErrorTypes.ServerError).setErrorMsg(throwable.getMessage()).setData(data);
                break;
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:    // To handle Session errors (session expired and session not correct)
                response.setErroCode(ServerResponse.ErrorTypes.SessionError).setErrorMsg(throwable.getMessage()).setData(data);
                break;
            default:
                response.setErroCode(ServerResponse.ErrorTypes.GeneralError).setErrorMsg(throwable.getMessage()).setData(data);
                break;
        }

        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement stack : throwable.getStackTrace()) {
            stackTrace.append(stack.toString()).append("\n");
        }
        String logSrt = "errorCode = " + ErrorCode + " , data = " + data + " , ErrorMsg = " + throwable.getMessage() + " , stacktrace = " + stackTrace.toString();
        Log.i(TAG, "handleFailedResponse:: Failed :: data = " + String.valueOf(logSrt));

        if (listener != null) {
            listener.onFailedResponse(response);
        }
    }


    private boolean isSuccess(Object data){
        try {
            if (((JSONObject)data).has("result")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}
