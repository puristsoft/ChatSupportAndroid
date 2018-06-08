package puristit.com.library;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import puristit.com.entities.MessageObject;


public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.ViewHolder> implements View.OnClickListener {

    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    private final String baseImageURL;
    private final onChatFragClickListener mListener;
    private ArrayList<MessageObject> ItemsList;
    private Context mContext;
    private SimpleDateFormat dateFormat;

    public interface onChatFragClickListener{
        void onViewClickListener(View view);
    }

    public ChatFragmentAdapter(Context context, ArrayList<MessageObject> recentMessage, onChatFragClickListener listener) {
        this.ItemsList = new ArrayList<>();
        this.mContext = context;
        this.mListener = listener;

        if (recentMessage != null){
            ItemsList.addAll(recentMessage);
        }

        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(new ImageLoaderConfiguration.Builder(mContext).build());
        this.options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .build();
        this.baseImageURL = RequestManager.serverURL;

        dateFormat = new SimpleDateFormat("     dd/MM/yyyy   hh:mm a");
    }


    public void addNewMsg(MessageObject messageObject){
        this.ItemsList.add(messageObject);
        notifyItemInserted(this.ItemsList.size());
    }

    public MessageObject getMessageOnPos(int position){
        if (position > -1 && position < ItemsList.size()){
            return this.ItemsList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ItemsList.get(position).getSender().ordinal();
    }

    @Override
    public ChatFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

//        switch (MessageObject.Sender.values()[viewType]) {
//            case Mine:
                ViewGroup mineView = (ViewGroup) mInflater.inflate(R.layout.msg_row, viewGroup, false);
                return new msgViewHolder(mineView);
//            case Operator:
//                ViewGroup operatorView = (ViewGroup) mInflater.inflate(R.layout.msg_row, viewGroup, false);
//                return new msgViewHolder(operatorView);
//        }
//
//        return null;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final MessageObject currentItem = ItemsList.get(position);

        ((msgViewHolder)viewHolder).tv_msg_sender.setText(currentItem.getSent_by());
        ((msgViewHolder)viewHolder).tv_msg_time.setText(dateFormat.format(new Date(currentItem.getTimestamp()).getTime()));

        ((msgViewHolder)viewHolder).tv_msg_body.setVisibility(View.GONE);
        ((msgViewHolder)viewHolder).tv_msg_fileName.setVisibility(View.GONE);
        ((msgViewHolder)viewHolder).iv_msg_image.setVisibility(View.GONE);

        if (currentItem.getFile_name() != null && !currentItem.getFile_name().isEmpty()){
            if (currentItem.getThumbnail_path() != null && !currentItem.getThumbnail_path().isEmpty()){
                ((msgViewHolder)viewHolder).iv_msg_image.setVisibility(View.VISIBLE);
                imageLoader.displayImage(baseImageURL + currentItem.getThumbnail_path(),  ((msgViewHolder)viewHolder).iv_msg_image, options);
            } else {
                ((msgViewHolder) viewHolder).tv_msg_fileName.setVisibility(View.VISIBLE);
                final SpannableString spannableString = new SpannableString(currentItem.getFile_name());
                spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((msgViewHolder) viewHolder).tv_msg_fileName.setText(spannableString, TextView.BufferType.SPANNABLE);
            }
        }else {
            ((msgViewHolder)viewHolder).tv_msg_body.setVisibility(View.VISIBLE);
            ((msgViewHolder)viewHolder).tv_msg_body.setText(currentItem.getBody());
        }

        switch (currentItem.getSender()){
            case Mine:
                if (PurisitChat.chatUIOptions.getMyMsgSkin().getBackgroundDrawable() != null){
                    ((msgViewHolder)viewHolder).ll_msg_background.setBackgroundDrawable(PurisitChat.chatUIOptions.getMyMsgSkin().getBackgroundDrawable());
                } else {
                    ((msgViewHolder)viewHolder).ll_msg_background.setCardBackgroundColor(PurisitChat.chatUIOptions.getMyMsgSkin().getBackgroundColor());
                }
                ((msgViewHolder)viewHolder).v_msg_empty.setVisibility(View.VISIBLE);
                ((msgViewHolder)viewHolder).tv_msg_sender.setTextColor(PurisitChat.chatUIOptions.getMyMsgSkin().getTextColor());
                ((msgViewHolder)viewHolder).tv_msg_body.setTextColor(PurisitChat.chatUIOptions.getMyMsgSkin().getTextColor());
                ((msgViewHolder)viewHolder).tv_msg_time.setTextColor(PurisitChat.chatUIOptions.getMyMsgSkin().getTextColor());
                break;
            case Operator:
                if (PurisitChat.chatUIOptions.getOthersMsgSkin().getBackgroundDrawable() != null){
                    ((msgViewHolder)viewHolder).ll_msg_background.setBackgroundDrawable(PurisitChat.chatUIOptions.getOthersMsgSkin().getBackgroundDrawable());
                } else {
                    ((msgViewHolder)viewHolder).ll_msg_background.setCardBackgroundColor(PurisitChat.chatUIOptions.getOthersMsgSkin().getBackgroundColor());
                }
                ((msgViewHolder)viewHolder).v_msg_empty.setVisibility(View.GONE);
                ((msgViewHolder)viewHolder).tv_msg_sender.setTextColor(PurisitChat.chatUIOptions.getOthersMsgSkin().getTextColor());
                ((msgViewHolder)viewHolder).tv_msg_body.setTextColor(PurisitChat.chatUIOptions.getOthersMsgSkin().getTextColor());
                ((msgViewHolder)viewHolder).tv_msg_time.setTextColor(PurisitChat.chatUIOptions.getOthersMsgSkin().getTextColor());
                break;
        }
    }


    @Override
    public int getItemCount() {
        return (null != ItemsList ? ItemsList.size() : 0);
    }



    @Override
    public void onClick(View view) {
        if (mListener != null){
            mListener.onViewClickListener(view);
        }
    }


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }
    }


    public class msgViewHolder extends ViewHolder {
        protected TextView tv_msg_sender, tv_msg_body, tv_msg_time, tv_msg_fileName;
        protected CardView ll_msg_background;
        protected View v_msg_empty;
        protected ImageView iv_msg_image;


        public msgViewHolder(View view) {
            super(view);
            this.tv_msg_sender = (TextView) view.findViewById(R.id.tv_msg_sender);
            this.tv_msg_body = (TextView) view.findViewById(R.id.tv_msg_body);
            this.tv_msg_time = (TextView) view.findViewById(R.id.tv_msg_time);
            this.ll_msg_background = (CardView) view.findViewById(R.id.ll_msg_background);
            this.v_msg_empty = (View) view.findViewById(R.id.v_msg_empty);

            this.iv_msg_image = (ImageView) view.findViewById(R.id.iv_msg_image);
            this.iv_msg_image.setTag(itemView);
            this.tv_msg_fileName = (TextView) view.findViewById(R.id.tv_msg_fileName);
            this.tv_msg_fileName.setTag(itemView);

            this.iv_msg_image.setOnClickListener(ChatFragmentAdapter.this);
            this.tv_msg_fileName.setOnClickListener(ChatFragmentAdapter.this);
        }
    }






}

