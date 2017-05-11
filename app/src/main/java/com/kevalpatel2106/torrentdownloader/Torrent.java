package com.kevalpatel2106.torrentdownloader;

import android.support.annotation.NonNull;

import com.turn.ttorrent.client.Client;

import java.io.File;

/**
 * Created by Keval Patel on 11/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class Torrent {

    private String key;

    private File torrentFile;

    private File outputDir;

    private Client client;

    public Torrent(@NonNull File torrentFile, @NonNull File outputDir) {
        if (!outputDir.isDirectory())
            throw new IllegalArgumentException("Invalid output directory.");
        this.outputDir = outputDir;

        this.torrentFile = torrentFile;
        this.key = torrentFile.getName();
    }

    public String getKey() {
        return key;
    }

    public File getTorrentFile() {
        return torrentFile;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
