package puristit.com.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by Anas on 1/13/2017.
 */

public class ChatView extends WebView {

    private ChatViewListener mListener;

    public ChatView(Context context) {
        super(context);
        init(context);
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public ChatView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init(context);
    }


    private void init(Context context){
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new ChromeClient());
//        webview.addJavascriptInterface(new MainActivity.LoadListener(), "HTMLOUT");

        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setScrollbarFadingEnabled(true);
        getSettings().setLoadsImagesAutomatically(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);
        // Set cache size to 8 mb by default. should be more than enough
        getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        // This next one is crazy. It's the DEFAULT location for your
        // app's cache
        // But it didn't work for me without this line.
        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = context.getCacheDir().getAbsolutePath();
        Log.e("Main", "appCachePath = " + appCachePath);
        getSettings().setAppCachePath(appCachePath);
        getSettings().setAllowFileAccess(true);

        setWebViewClient(webViewClient);
    }



    public void setChatViewListener(ChatViewListener oListener){
        this.mListener = oListener;
    }


    WebViewClient webViewClient = new WebViewClient() {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d("Main", "onReceivedError :: " + description);
            // pDialog.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (mListener != null){
                mListener.onPageStarted(view, url, favicon);
            }
        }

        public void onPageFinished(WebView view, String url) {
            if (mListener != null){
                if (url.endsWith("/logout")) {
                    mListener.onChatViewDismiss();
                } else {
                    mListener.onPageFinished(view, url);
                }
            }
    }


    };




    public class ChromeClient extends WebChromeClient {

        // For Android 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mListener != null){
                mListener.onShowFileChooser(webView, filePathCallback, fileChooserParams);
                return true;
            }
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }


        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            if (mListener != null){
                mListener.openFileChooser(uploadMsg, "");
            }
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            if (mListener != null){
                mListener.openFileChooser(uploadMsg, acceptType);
            }
        }

    }


}
