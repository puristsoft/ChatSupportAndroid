package puristit.com.widget;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Anas on 1/13/2017.
 */

public interface ChatViewListener {

    /**
     * Notify the host application that a page has started loading. This method
     * is called once for each main frame load so a page with iframes or
     * framesets will call onPageStarted one time for the main frame. This also
     * means that onPageStarted will not be called when the contents of an
     * embedded frame changes, i.e. clicking a link whose target is an iframe,
     * it will also not be called for fragment navigations (navigations to
     * #fragment_id).
     *
     * @param view The WebView that is initiating the callback.
     * @param url The url to be loaded.
     * @param favicon The favicon for this page if it already exists in the
     *            database.
     */
    public void onPageStarted(WebView view, String url, Bitmap favicon);


    /**
     * Notify the host application that a page has finished loading. This method
     * is called only for main frame. When onPageFinished() is called, the
     * rendering picture may not be updated yet. To get the notification for the
     * new Picture, use {@link WebView.PictureListener#onNewPicture}.
     *
     * @param view The WebView that is initiating the callback.
     * @param url The url of the page.
     */
    public void onPageFinished(WebView view, String url);


    /**
     * Notify the host application that the Chat View is closing, when the user clicks on the Done
     * Button.
     *
     * */
    public abstract void onChatViewDismiss();


    /**
     * Tell the client to show a file chooser. this is for android 5.0+
     *
     * This is called to handle HTML forms with 'file' input type, in response to the
     * user pressing the "Select File" button.
     * To cancel the request, call <code>filePathCallback.onReceiveValue(null)</code> and
     * return true.
     *
     * @param webView The WebView instance that is initiating the request.
     * @param filePathCallback Invoke this callback to supply the list of paths to files to upload,
     *                         or NULL to cancel. Must only be called if the
     *                         <code>showFileChooser</code> implementations returns true.
     * @param fileChooserParams Describes the mode of file chooser to be opened, and options to be
     *                          used with it.
     * @return true if filePathCallback will be invoked, false to use default handling.
     *
     * @see WebChromeClient.FileChooserParams
     */
    public abstract void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);


    /**
     * Tell the client to open a file chooser. this is for android 3.0+
     * @param uploadMsg A ValueCallback to set the URI of the file to upload.
     *      onReceiveValue must be called to wake up the thread.a
     * @param acceptType The value of the 'accept' attribute of the input tag
     *         associated with this file picker.
     *
     */
    public abstract void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);
}
