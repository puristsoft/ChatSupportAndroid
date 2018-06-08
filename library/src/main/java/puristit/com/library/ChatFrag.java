package puristit.com.library;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import puristit.com.entities.MessageObject;
import puristit.com.entities.RoomObject;

/**
 * Created by Anas on 2/25/2018.
 */

public class ChatFrag extends Fragment implements View.OnClickListener, ChatFragmentAdapter.onChatFragClickListener {

    private Activity mContext;
    private RoomObject roomObject;
    private Button btn_send;
    private RecyclerView mRecycler;
    private ChatFragmentAdapter mAdapter;
    private Handler mHandler;
    private EditText et_chat_text;
    private chatFragInteractionListener mListener;
    private PurisitChat puristChat;
    private long conversationID;
    private ImageButton btn_attach;

    interface chatFragInteractionListener {
        void onTextMessageSend(long conversationID, String message);
        void onMediaMessageSend(long conversationID);
        void onShowImageFullView(String fileURL);
        void onDownloadFile(String fileURL, String fileName);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        try {
            mListener = (chatFragInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement chatFragInteractionListener");
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat, null);
        mRecycler = (RecyclerView)view.findViewById(R.id.rv_chat);
        btn_send = (Button)view.findViewById(R.id.btn_send);
        btn_attach = (ImageButton)view.findViewById(R.id.btn_attach);
        et_chat_text = (EditText)view.findViewById(R.id.et_chat_text);
        View ll_chat_footer = view.findViewById(R.id.ll_chat_footer);
        btn_send.setOnClickListener(this);
        btn_attach.setOnClickListener(this);

        if (PurisitChat.chatUIOptions.getBackgroundDrawable() != null){
            mRecycler.setBackgroundDrawable(PurisitChat.chatUIOptions.getBackgroundDrawable());
        } else {
            mRecycler.setBackgroundColor(PurisitChat.chatUIOptions.getBackgroundColor());
        }
        ll_chat_footer.setBackgroundColor(PurisitChat.chatUIOptions.getChatPageFooterBackgroundColor());
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String roomJson = null;
        String recentMessageJson = null;
        ArrayList<MessageObject> recentMessage = null;

        if (getArguments() != null){
            conversationID = getArguments().getLong("conversationID");
            roomJson = getArguments().getString("selectedRoom");
            recentMessageJson = getArguments().getString("recentMessage");
        }


        Gson gson = new Gson();
        if (roomJson != null){
            roomObject = gson.fromJson(roomJson, RoomObject.class);
        }
        if (recentMessageJson != null){
            Type listType = new TypeToken<List<MessageObject>>() {}.getType();
            recentMessage =  new Gson().fromJson(recentMessageJson, listType);

            Collections.sort(recentMessage, new Comparator<MessageObject>() {
                @Override
                public int compare(MessageObject lhs, MessageObject rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.getTimestamp() > rhs.getTimestamp() ? 1 : (lhs.getTimestamp() < rhs.getTimestamp()) ? -1 : 0;
                }
            });
        }


        handleRoomStatus(roomObject.getStatus());
        getActivity().setTitle(roomObject.getName());

        mRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ChatFragmentAdapter(mContext, recentMessage, this);
        mRecycler.setAdapter(mAdapter);
    }




    private void handleRoomStatus(String isOnline){
        boolean isOnlineStatus = isOnline.toLowerCase().equals("online");
        et_chat_text.setEnabled(isOnlineStatus);
        btn_send.setEnabled(isOnlineStatus);
        btn_attach.setEnabled(isOnlineStatus);

        if (isOnlineStatus){
            et_chat_text.setBackgroundColor(Color.parseColor("#FFFFFF"));
            et_chat_text.setHint(getString(R.string.purist_chat_send_msg_hint));
        } else {
            et_chat_text.setBackgroundColor(Color.parseColor("#EEEEEE"));
            et_chat_text.setHint(getString(R.string.purist_chat_operator_offline_hint));
        }
    }





    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            if (et_chat_text.getText().toString().isEmpty()){
                Toast.makeText(getContext(), getString(R.string.purist_chat_empty_msg_error), Toast.LENGTH_SHORT).show();
                return;
            }

            mListener.onTextMessageSend(conversationID, et_chat_text.getText().toString());
            et_chat_text.setText("");
        } else  if (v.getId() == R.id.btn_attach) {
            mListener.onMediaMessageSend(conversationID);
        }
    }


    @Override
    public void onViewClickListener(View view) {
        int position = mRecycler.getChildAdapterPosition((View) view.getTag());
        if (position >= 0) {
            MessageObject messageObject = mAdapter.getMessageOnPos(position);
            if (messageObject != null) {
                if (view.getId() == R.id.iv_msg_image) {
                    mListener.onShowImageFullView(messageObject.getThumbnail_path());
                } else if (view.getId() == R.id.tv_msg_fileName) {
                    mListener.onDownloadFile(messageObject.getFile_url(), messageObject.getFile_name());
                }
            }
        }
    }


    public long getRoomID(){
        return roomObject.getId();
    }

    public void newMessageReceived(long roomID, MessageObject newMessage){
        if (roomObject.getId() == roomID){
            mAdapter.addNewMsg(newMessage);
            mRecycler.smoothScrollToPosition(mAdapter.getItemCount());
        }
    }

    public void changeRoomStatus(String status){
        handleRoomStatus(status);
    }


}
