package puristit.com.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import puristit.com.entities.MessageObject;
import puristit.com.entities.RoomObject;

/**
 * Created by Anas on 2/24/2018.
 */

public class RoomsFrag extends Fragment implements RoomsFragmentAdapter.OnRoomsClickedListener {

    private RecyclerView mRecycler;
    private ArrayList<RoomObject> rooms = new ArrayList<>();
    private RoomsFragmentAdapter mAdapter;
    private Activity mContext;
    private roomsInteractionListener mListener;



    interface roomsInteractionListener {
        void onRoomSelected(RoomObject roomObject);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        try {
            mListener = (roomsInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement roomsInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_rooms, null);
        mRecycler = (RecyclerView)view.findViewById(R.id.purist_rv_rooms);

        if (PurisitChat.chatUIOptions.getBackgroundDrawable() != null){
            mRecycler.setBackgroundDrawable(PurisitChat.chatUIOptions.getBackgroundDrawable());
        } else {
            mRecycler.setBackgroundColor(PurisitChat.chatUIOptions.getBackgroundColor());
        }
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(getString(R.string.purist_rooms_title));

        mRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new RoomsFragmentAdapter(mContext, rooms, this);
        mRecycler.setAdapter(mAdapter);

    }



    @Override
    public void onRoomClicked(View v) {
        int position = mRecycler.getChildAdapterPosition(v);
        if (position == -1) {
            return;
        }

        RoomObject clickedRoom = rooms.get(position);
        mAdapter.resetPendingCount(clickedRoom.getId());
        mListener.onRoomSelected(clickedRoom);
    }



    public void newMessageReceived(long roomID, MessageObject newMessage){
        mAdapter.incrementPendingCount(roomID);
    }

    public void changeRoomStatus(long roomID, String status){
        mAdapter.updateRoomStatus(roomID, status);
    }

    public void updateRooms(ArrayList<RoomObject> newRooms) {
        rooms = newRooms;
        mAdapter.updateRooms(rooms);
    }
}
