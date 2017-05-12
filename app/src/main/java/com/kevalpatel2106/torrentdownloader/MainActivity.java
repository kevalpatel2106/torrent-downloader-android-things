package com.kevalpatel2106.torrentdownloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private FTPManager mFTPManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFTPManager = new FTPManager(this);
        mFTPManager.startServer();


        try {
            new WebServer(this, getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFTPManager.stopServer();
    }
}
