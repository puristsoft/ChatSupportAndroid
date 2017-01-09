package puristit.com.server_request;

/**
 * Created by Anas Bakez on 9/10/2015.
 */
public abstract class ResponseListener {

    public abstract void onSuccessResponse(ServerResponse response);
    public void onFailedResponse(ServerResponse response){}
}