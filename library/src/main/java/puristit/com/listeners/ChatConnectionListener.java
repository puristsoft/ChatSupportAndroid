package puristit.com.listeners;

import java.util.ArrayList;

import puristit.com.entities.MessageObject;

/**
 * Created by Anas on 1/13/2017.
 */

public abstract class ChatConnectionListener {

    /**
     * Notify the host application that Chat connection is opened
     */
    public void onConnectionOpened() {
    }


    /**
     * Notify the host application that Char connection is closed
     */
    public void onConnectionClosed() {
    }


    /**
     * Notify the host application that Char connection ping occur
     */
    public void onPing() {
    }


    /**
     * Notify the host application that the subscription is confirmed and connection is ready
     */
    public void onSubscriptionConfirmed() {
    }


    /**
     * Notify the host application that the room requested is joined
     *
     * @param roomID the id of the room joined to
     * @param conversationID the id of the conversation of this room
     * @param recentMessages recent messages in this conversation
     */
    public abstract void onRoomJoined(long roomID, long conversationID, ArrayList<MessageObject> recentMessages);



    /**
     * Notify the host application that a new message has been received
     *
     * @param roomID the id of the room
     * @param newMessage the new messages in this conversation
     */
    public abstract void onTextMessageReceived(long roomID, MessageObject newMessage);



    /**
     * Notify the host application that a certain room status changed
     *
     * @param roomID the id of the room
     * @param status the new status of thi room
     */
    public abstract void onRoomStatusChanged(long roomID, String status);

    /**
     * Notify the host application that there wa problem connecting
     */
    public abstract void onError(Exception e);


}
