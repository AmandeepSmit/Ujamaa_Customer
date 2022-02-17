package com.ujamaaonline.customer.activities;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ujamaaonline.customer.R;


public class VideoPlayerExo extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_exo);
        playerView = findViewById(R.id.exo_player);
        videoUrl = getIntent().getStringExtra("url");
    }

    private void initExoPlayer() {
        this.player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), getApplicationContext().getString(R.string.app_name)));
        Uri videoUri = Uri.parse(videoUrl);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (com.google.android.exoplayer2.util.Util.SDK_INT > 23) {
            initExoPlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (com.google.android.exoplayer2.util.Util.SDK_INT <= 23 || player == null) {
            initExoPlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (com.google.android.exoplayer2.util.Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (com.google.android.exoplayer2.util.Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //releaseAdsLoader();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
