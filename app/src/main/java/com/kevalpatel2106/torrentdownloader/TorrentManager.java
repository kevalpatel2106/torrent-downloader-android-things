package com.kevalpatel2106.torrentdownloader;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Keval Patel on 11/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class TorrentManager {

    private static HashMap<String, Torrent> mTorrentClients = new HashMap<>();

    public static void add(@NonNull final Torrent torrent)
            throws IOException, NoSuchAlgorithmException {

        final Client client = new Client(InetAddress.getLocalHost(),
                SharedTorrent.fromFile(torrent.getTorrentFile(),
                        torrent.getOutputDir()));
        client.setMaxDownloadRate(0.0);
        client.setMaxUploadRate(1.0);
        client.download();
        client.addObserver(new Observer() {
            @Override
            public void update(Observable observer, Object arg) {
                Client client1 = (Client) observer;
                SharedTorrent sharedTorrent = client1.getTorrent();
                Log.d("Progress", "Initialized? " + sharedTorrent.isInitialized()
                        + " Complete: " + sharedTorrent.getCompletion() + "%"
                        + "\nDownload: " + (sharedTorrent.getDownloaded() / 1024) + "KB"
                        + "\nUpload: " + (sharedTorrent.getUploaded() / 1024) + "KB"
                        + "\nCompletion: " + sharedTorrent.getCompletion() + "%");

                if (sharedTorrent.isComplete()) {
                    //Torrent Downloaded.
                    torrent.getTorrentFile().delete();
                }
            }
        });

        torrent.setClient(client);
        mTorrentClients.put(torrent.getKey(), torrent);
    }

    public static void stop(@NonNull String key) {
        Torrent torrent = mTorrentClients.get(key);
        torrent.getClient().stop();
    }

    public static float getProgress(@NonNull String key) {
        Torrent torrent = mTorrentClients.get(key);
        return torrent.getClient().getTorrent().getCompletion();
    }

    public static File getOutputDir(Context context) {
        File usbDir = new File("/mnt/usb");
        if (usbDir.exists() && usbDir.canWrite()) {
            Log.d("Output Dir", "External USB");
            return usbDir;
        } else {
            if (!Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).exists())
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }
//        return context.getExternalCacheDir();
    }
}
