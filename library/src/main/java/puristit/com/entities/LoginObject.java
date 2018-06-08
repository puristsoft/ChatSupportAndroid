package puristit.com.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Anas on 2/25/2018.
 */

public class LoginObject {


    private String access_token = "";
    @SerializedName("url")
    private String chatUrl = "";
    private String token_type = "";
    private String name = "";
    private long token_timeout;
    private ArrayList<RoomObject> rooms = new ArrayList<>();


    public String getAccess_token() {
        return access_token;
    }

    public LoginObject setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public LoginObject setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
        return this;
    }

    public ArrayList<RoomObject> getRooms() {
        return rooms;
    }

    public LoginObject setRooms(ArrayList<RoomObject> rooms) {
        this.rooms = rooms;
        return this;
    }


    public String getToken_type() {
        return token_type;
    }

    public LoginObject setToken_type(String token_type) {
        this.token_type = token_type;
        return this;
    }

    public String getName() {
        return name;
    }

    public LoginObject setName(String name) {
        this.name = name;
        return this;
    }

    public long getToken_timeout() {
        return token_timeout;
    }

    public LoginObject setToken_timeout(long token_timeout) {
        this.token_timeout = token_timeout;
        return this;
    }
}
