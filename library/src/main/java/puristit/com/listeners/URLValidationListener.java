package puristit.com.listeners;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Anas on 1/13/2017.
 */

public interface URLValidationListener {

    /**
     * Notify the host application that Char URl is valid and can be used
     */
    public void onURLValid();


    /**
     * Notify the host application that Char URl is not valid and initialize api must be called
     * to get new chat url
     */
    public void onURLExpired();

    /**
     * Notify the host application that there wa problem checking the Url, it is better to call initialize
     * api to get new chat url
     */
    public void onError();


}
