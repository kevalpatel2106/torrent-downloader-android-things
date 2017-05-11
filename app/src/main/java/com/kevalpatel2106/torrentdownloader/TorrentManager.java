package com.kevalpatel2106.torrentdownloader;

import android.support.annotation.NonNull;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by Keval Patel on 11/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class TorrentManager {

    private static HashMap<String, Torrent> mTorrentClients = new HashMap<>();

    public static void add(@NonNull final Torrent torrent)
            throws IOException, NoSuchAlgorithmException {

        Client client = new Client(InetAddress.getLocalHost(),
                SharedTorrent.fromFile(torrent.getTorrentFile(), torrent.getOutputDir()));
        client.setMaxDownloadRate(0);
        client.setMaxUploadRate(1.0);
        client.download();
        client.waitForCompletion();

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
}
