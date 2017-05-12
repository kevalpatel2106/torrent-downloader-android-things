package com.kevalpatel2106.torrentdownloader;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Keval Patel on 11/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class WebServer extends NanoHTTPD {
    private final Context mContext;
    private AssetManager assetManager;

    public WebServer(Context context, AssetManager assetManager) throws IOException {
        super(8080);
        mContext = context;
        this.assetManager = assetManager;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {

        try {

            if (session.getMethod() != Method.POST) {
                return getHTMLResponse("torrentform.html");
            } else {
                HashMap<String, String> files = new HashMap<>();
                try {
                    session.parseBody(files);
                } catch (ResponseException e1) {
                    e1.printStackTrace();
                }

                File file = new File(files.get("torrentfile"));
                Log.d("File name", file.getName());
                Log.d("File size", file.length() + "");

                if (file.length() > 0) {

                    File torrentFile = copyToCache(file);
                    if (torrentFile != null) {

                        Torrent torrent = new Torrent(torrentFile, TorrentManager.getOutputDir(mContext));
                        TorrentManager.add(torrent);
                        return getHTMLResponse("success.html");
                    }
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("<html><body><h1>500 : Internal server error.</h1></body></html>\n");
    }

    @NonNull
    private Response getHTMLResponse(String assteName) throws IOException {
        InputStream inputStream = assetManager.open(assteName);
        return newFixedLengthResponse(Response.Status.OK, "text/html", inputStream, inputStream.available());
    }

    @Nullable
    private File copyToCache(File sourceFile) {
        File cacheFile = new File(mContext.getExternalCacheDir(), sourceFile.getName() + ".torrent");
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(cacheFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            return cacheFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
