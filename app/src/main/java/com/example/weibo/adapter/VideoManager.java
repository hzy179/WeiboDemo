package com.example.weibo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.weibo.utils.TimeUtils;
import com.example.weibo.widget.CustomVideoView;
import java.util.Timer;
import java.util.TimerTask;

public class VideoManager {

    private CustomVideoView currentPlayingVideoView = null;
    private int currentPlayingPosition = -1;

    public void setupVideoView(CustomVideoView videoView, ImageView videoThumbnail, ImageView playButton,
                               SeekBar seekBar, TextView currentTime, TextView totalTime, Context context, WeiboAdapter adapter) {

        videoView.setVisibility(View.GONE);
        videoView.setBackgroundColor(Color.BLACK);

        videoView.setOnCompletionListener(mp -> {
            mp.seekTo(0);
            mp.pause();
            playButton.setVisibility(View.VISIBLE);
            videoThumbnail.setVisibility(View.VISIBLE);
        });

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f);
            seekBar.setMax(videoView.getDuration());
            totalTime.setText(TimeUtils.formatTime(videoView.getDuration()));
            mp.setOnInfoListener((mp1, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    videoView.setBackgroundColor(Color.TRANSPARENT);
                    videoThumbnail.setVisibility(View.GONE);
                }
                return true;
            });

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (videoView.isPlaying()) {
                        seekBar.setProgress(videoView.getCurrentPosition());
                        currentTime.post(() -> currentTime.setText(TimeUtils.formatTime(videoView.getCurrentPosition())));
                    }
                }
            }, 0, 1000);
        });

        View.OnClickListener videoClickListener = v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playButton.setVisibility(View.VISIBLE);
                videoThumbnail.setVisibility(View.VISIBLE);
            } else {
                if (currentPlayingVideoView != null && currentPlayingVideoView != videoView) {
                    currentPlayingVideoView.pause();
                    currentPlayingVideoView.setVisibility(View.GONE);
                    currentPlayingVideoView = null;
                    currentPlayingPosition = -1;
                }
                videoView.setVisibility(View.VISIBLE);
                videoView.start();
                playButton.setVisibility(View.GONE);
                videoThumbnail.setVisibility(View.GONE);
                currentPlayingPosition = adapter.getAdapterPositionForVideoView(videoView);
                currentPlayingVideoView = videoView;
            }
        };

        playButton.setOnClickListener(videoClickListener);
        videoView.setOnClickListener(videoClickListener);

        videoView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (currentPlayingPosition == adapter.getAdapterPositionForVideoView(videoView) && !videoView.isPlaying()) {
                    playButton.setVisibility(View.VISIBLE);
                    videoThumbnail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (currentPlayingPosition == adapter.getAdapterPositionForVideoView(videoView) && videoView.isPlaying()) {
                    videoView.pause();
                    playButton.setVisibility(View.VISIBLE);
                    videoThumbnail.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
