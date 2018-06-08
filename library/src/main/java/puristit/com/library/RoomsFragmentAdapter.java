package puristit.com.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

import puristit.com.entities.RoomObject;


public class RoomsFragmentAdapter extends RecyclerView.Adapter<RoomsFragmentAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<RoomObject> ItemsList;
    private HashMap<Long, Integer> pendingMessagesCount;
    private Context mContext;
    OnRoomsClickedListener mListener;

    public interface OnRoomsClickedListener {
        void onRoomClicked(View v);
    }


    public RoomsFragmentAdapter(Context context, ArrayList<RoomObject> ItemsList, OnRoomsClickedListener oListener) {
        this.ItemsList = ItemsList;
        this.mContext = context;
        this.mListener = oListener;
        this.pendingMessagesCount = new HashMap<>();

        for (RoomObject roomObject: ItemsList) {
            pendingMessagesCount.put(roomObject.getId(), 0);
        }
    }


    @Override
    public RoomsFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup loadingHeader = (ViewGroup) mInflater.inflate(R.layout.rooms_row, viewGroup, false);
        return new roomViewHolder(loadingHeader);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final RoomObject currentItem = ItemsList.get(position);

        ((roomViewHolder)viewHolder).tv_room_name.setText(currentItem.getName());
        ((roomViewHolder)viewHolder).tv_room_status.setText(currentItem.getStatus());

        if (currentItem.getStatus().toLowerCase().equals("online")){
            ((roomViewHolder)viewHolder).v_room_status.setBackground(mContext.getResources().getDrawable(R.drawable.room_online_status));
        } else {
            ((roomViewHolder)viewHolder).v_room_status.setBackground(mContext.getResources().getDrawable(R.drawable.room_offline_status));
        }

        int pendingCount = pendingMessagesCount.get(currentItem.getId());
        if (pendingCount == 0){
            ((roomViewHolder)viewHolder).tv_room_pending.setVisibility(View.GONE);
        } else {
            ((roomViewHolder)viewHolder).tv_room_pending.setVisibility(View.VISIBLE);
            ((roomViewHolder)viewHolder).tv_room_pending.setText(String.valueOf(pendingCount));
        }
    }


    @Override
    public int getItemCount() {
        return (null != ItemsList ? ItemsList.size() : 0);
    }


    public void updateRooms(ArrayList<RoomObject> rooms){
        ItemsList = rooms;
        pendingMessagesCount.clear();
        for (RoomObject roomObject : ItemsList) {
            pendingMessagesCount.put(roomObject.getId(), 0);
        }

        notifyDataSetChanged();
    }

    public void incrementPendingCount(long roomID){
        if (pendingMessagesCount.containsKey(roomID)) {
            pendingMessagesCount.put(roomID, pendingMessagesCount.get(roomID) + 1);
            notifyDataSetChanged();
        }
    }

    public void resetPendingCount(long roomID){
        pendingMessagesCount.put(roomID, 0);
        notifyDataSetChanged();
    }

    public void updateRoomStatus(long roomID, String status){
        for (RoomObject room :ItemsList){
            if (room.getId() == roomID){
                room.setStatus(status);
            }
        }
        notifyDataSetChanged();
    }


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }


    public class roomViewHolder extends ViewHolder {
        protected TextView tv_room_name, tv_room_status, tv_room_pending;
        protected View v_room_status;


        public roomViewHolder(View view) {
            super(view);
            this.tv_room_name = (TextView) view.findViewById(R.id.tv_room_name);
            this.tv_room_status = (TextView) view.findViewById(R.id.tv_room_status);
            this.tv_room_pending = (TextView) view.findViewById(R.id.tv_room_pending);
            this.v_room_status = view.findViewById(R.id.v_room_status);
            view.setOnClickListener(RoomsFragmentAdapter.this);
        }
    }




    @Override
    public void onClick(View v) {
        mListener.onRoomClicked(v);
    }

}

