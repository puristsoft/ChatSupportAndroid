package puristit.com.server_request;

import java.io.Serializable;

/**
 * Created by Anas Bakez on 9/10/2015.
 */
public class ServerResponse implements Serializable{
    private ErrorTypes ErrorCode;
    private String Msg;
    private Object Data;


    public enum ErrorTypes{
        Success(0),
        ServerError(100),
        SessionError(200),
        NetworkError(500),
        GeneralError(1000);

        ErrorTypes (int i)
        {
            this.value = i;
        }

        private int value;

        public int getValue()
        {
            return value;
        }
    }

    public ServerResponse setErroCode(ErrorTypes errorType){
        this.ErrorCode = errorType;
        return this;
    }
    public ServerResponse setErrorMsg (String Msg){
        this.Msg = Msg;
        return this;
    }
    public ServerResponse setData (Object data){
        this.Data = data;
        return this;
    }



    public ErrorTypes getErrorCode (){
        return this.ErrorCode;
    }
    public String getErrorMsg(){
        return this.Msg;
    }
    public Object getData(){
        return this.Data;
    }
}