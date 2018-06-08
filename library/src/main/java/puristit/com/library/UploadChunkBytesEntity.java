package puristit.com.library;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

/**
 * Created by a.bakez on 5/24/2016.
 */
class UploadChunkBytesEntity implements HttpEntity {


    private final String TAG = "UploadChunkBytesEntity";
    private UploadRequestAsynchttpResponseHandler progressHandler = null;
    private FileInputStream fileInputStream;
    private long contentLength;
    private long skipOffset;


//    private long totalFileSize;
//    public UploadChunkBytesEntity(FileInputStream fileInputStream, long totalFileSize) {
//        this(fileInputStream, 0, 0, totalFileSize);
//    }
//
//
//    public UploadChunkBytesEntity(FileInputStream fileInputStream, long contentLength, long totalFileSize) {
//        this(fileInputStream, contentLength, 0, totalFileSize);
//    }
//
//
//    public UploadChunkBytesEntity(FileInputStream fileInputStream, long contentLength, long skipOffset, long totalFileSize) {
//        this.fileInputStream = fileInputStream;
//        this.contentLength = contentLength;
//        this.skipOffset = skipOffset;
//        this.totalFileSize = totalFileSize;
//
//
//    }


    public UploadChunkBytesEntity(FileInputStream fileInputStream, long contentLength) {
        this(fileInputStream, contentLength, 0);
    }


    public UploadChunkBytesEntity(FileInputStream fileInputStream, long contentLength, long skipOffset) {
        this.fileInputStream = fileInputStream;
        this.contentLength = contentLength;
        this.skipOffset = skipOffset;

    }


    public void setProgressHandler(UploadRequestAsynchttpResponseHandler progressHandler){
        this.progressHandler = progressHandler;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public Header getContentType() {
        return null;
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        throw new UnsupportedOperationException(
                "getContent() is not supported. Use writeTo() instead.");
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        if (skipOffset> 0) {
            fileInputStream.skip(skipOffset);
        }


        final byte[] tmp = new byte[16*1024];
        int bytesRead;
        int readCount = 0;
        while (readCount < contentLength && (bytesRead = fileInputStream.read(tmp)) != -1) {
            outstream.write(tmp, 0, bytesRead);
            readCount += bytesRead;
            updateProgress(readCount);

        }
        outstream.flush();
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {

    }


    private void updateProgress(long count) {
        if (progressHandler != null) {
            progressHandler.sendRequestProgressMessage(count, contentLength);
        } else {
            Log.i(TAG, "progressHandler is not implemented, to show progress just setProgressHandler()");
        }
    }


//    void addPart(String key, String value, String contentType) {
//        try {
//            out.write(boundaryLine);
//            out.write(createContentDisposition(key));
//            out.write(createContentType(contentType));
//            out.write(CR_LF);
//            out.write(value.getBytes());
//            out.write(CR_LF);
//        } catch (final IOException e) {
//            // Shall not happen on ByteArrayOutputStream
//            AsyncHttpClient.log.e(LOG_TAG, "addPart ByteArrayOutputStream exception", e);
//        }
//    }
}
