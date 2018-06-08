package puristit.com.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import puristit.com.entities.MessageObject;

/**
 * Created by Anas on 2/25/2018.
 */

public class FullViewFrag extends Fragment {

    private Activity mContext;
    private ImageView iv_full_view;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private String baseImageURL;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_fullview, null);
        iv_full_view = (ImageView)view.findViewById(R.id.purist_iv_full_view);

        this.imageLoader = ImageLoader.getInstance();
        this.imageLoader.init(new ImageLoaderConfiguration.Builder(mContext).build());
        this.options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .build();
        this.baseImageURL = RequestManager.serverURL;
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String fileURL = null;
        String recentMessageJson = null;
        ArrayList<MessageObject> recentMessage = null;

        if (getArguments() != null){
            fileURL = getArguments().getString("fileURL");
        }

        imageLoader.displayImage(baseImageURL + fileURL,  iv_full_view, options);
    }


}
