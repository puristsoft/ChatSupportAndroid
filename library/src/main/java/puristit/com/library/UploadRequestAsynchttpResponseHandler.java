package puristit.com.library;

import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by a.aldahoun on 17/08/2017.
 */

abstract class UploadRequestAsynchttpResponseHandler extends AsyncHttpResponseHandler {


    private static final String LOG_TAG = "AsyncHttpRH(UploadRequestAsynchttpResponseHandler)";
    protected static final int REQUEST_PROGRESS_MESSAGE = 400;

    public void sendRequestProgressMessage(long bytesWritten, long bytesTotal) {
        sendMessage(obtainMessage(REQUEST_PROGRESS_MESSAGE, new Object[]{bytesWritten, bytesTotal}));
    }

    @Override
    protected void handleMessage(Message message) {
        Object[] response;

        try {
            switch (message.what) {
                case REQUEST_PROGRESS_MESSAGE:
                    response = (Object[]) message.obj;
                    if (response != null && response.length >= 2) {
                        try {
                            onRequestProgress((Long) response[0], (Long) response[1]);
                        } catch (Throwable t) {
                            AsyncHttpClient.log.e(LOG_TAG, "custom onProgress contains an error", t);
                        }
                    } else {
                        AsyncHttpClient.log.e(LOG_TAG, "PROGRESS_MESSAGE didn't got enough params");
                    }
                    break;
               default:
                   super.handleMessage(message);
                   break;
            }
        } catch (Throwable error) {
            onUserException(error);
        }
    }



    /**
     * Fired when the request progress, override to handle in your own code
     *
     * @param bytesWritten offset from start of file
     * @param totalSize    total size of file
     */
    public void onRequestProgress(long bytesWritten, long totalSize) {
        AsyncHttpClient.log.v(LOG_TAG, String.format("Progress %d from %d (%2.0f%%)", bytesWritten, totalSize, (totalSize > 0) ? (bytesWritten * 1.0 / totalSize) * 100 : -1));
    }
}
