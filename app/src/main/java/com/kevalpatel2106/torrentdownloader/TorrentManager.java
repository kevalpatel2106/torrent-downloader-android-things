package com.kevalpatel2106.torrentdownloader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
                SharedTorrent.fromFile(torrent.getTorrentFile(), torrent.getOutputDir()));
        client.setMaxDownloadRate(0.0);
        client.setMaxUploadRate(1.0);
        client.addObserver(new Observer() {
            @Override
            public void update(Observable observer, Object arg) {
                Client client1 = (Client) observer;
                SharedTorrent sharedTorrent = client1.getTorrent();
                Log.d("Progress", "Name: " + sharedTorrent.getName()
                        + " Complete: " + sharedTorrent.getCompletion() + "%"
                        + "\nDownload: " + (sharedTorrent.getDownloaded() / 1024) + "KB"
                        + "\nUpload: " + (sharedTorrent.getUploaded() / 1024) + "KB"
                        + "\nCompletion: " + sharedTorrent.getCompletion() + "%");

                if (sharedTorrent.isComplete()) {
                    //Torrent Downloaded.
                    torrent.getTorrentFile().delete();

                    startNextDownload();
                }
            }
        });

        torrent.setClient(client);
        mTorrentClients.put(torrent.getKey(), torrent);
    }

    private static void startNextDownload() {
        for (String key : mTorrentClients.keySet()) {
            Torrent torrent = mTorrentClients.get(key);
            if (torrent.getClient().getState() == Client.ClientState.WAITING
                    || torrent.getClient().getState() == Client.ClientState.VALIDATING) {
                torrent.getClient().download();
                break;
            }
        }
    }

    public static void stop(@NonNull String key) {
        Torrent torrent = mTorrentClients.get(key);
        torrent.getClient().stop();
    }

    public static float getProgress(@NonNull String key) {
        Torrent torrent = mTorrentClients.get(key);
        return torrent.getClient().getTorrent().getCompletion();
    }

    static File getOutputDir(Context context) {
        File usbDir = new File("/mnt/usb");
        if (usbDir.exists() && usbDir.canWrite()) {
            Log.d("Output Dir", "External USB");
            return usbDir;
        }
        return context.getExternalCacheDir();
    }
}
