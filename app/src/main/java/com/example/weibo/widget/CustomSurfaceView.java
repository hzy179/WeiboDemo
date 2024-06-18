//package com.example.weibo.widget;
//
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.util.AttributeSet;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.example.weibo.utils.TimeUtils;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
//
//    private MediaPlayer mediaPlayer;
//    private SurfaceHolder surfaceHolder;
//    private ImageView videoThumbnail;
//    private ImageView playButton;
//    private SeekBar seekBar;
//    private TextView currentTime;
//    private TextView totalTime;
//    private Timer timer;
//
//    public CustomSurfaceView(Context context) {
//        super(context);
//        init();
//    }
//
//    public CustomSurfaceView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    private void init() {
//        surfaceHolder = getHolder();
//        surfaceHolder.addCallback(this);
//    }
//
//    public void setVideoURI(Uri uri, ImageView videoThumbnail, ImageView playButton, SeekBar seekBar, TextView currentTime, TextView totalTime) {
//        this.videoThumbnail = videoThumbnail;
//        this.playButton = playButton;
//        this.seekBar = seekBar;
//        this.currentTime = currentTime;
//        this.totalTime = totalTime;
//
//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(getContext(), uri);
//            mediaPlayer.setDisplay(surfaceHolder);
//            mediaPlayer.setOnPreparedListener(this);
//            mediaPlayer.prepareAsync();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        if (mediaPlayer != null) {
//            mediaPlayer.setDisplay(holder);
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        mediaPlayer.start();
//        playButton.setVisibility(View.GONE);
//        videoThumbnail.setVisibility(View.GONE);
//        seekBar.setMax(mediaPlayer.getDuration());
//        totalTime.setText(TimeUtils.formatTime(mediaPlayer.getDuration()));
//
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (mediaPlayer.isPlaying()) {
//                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                    currentTime.post(() -> currentTime.setText(TimeUtils.formatTime(mediaPlayer.getCurrentPosition())));
//                }
//            }
//        }, 0, 1000);
//    }
//
//    public void playPause() {
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            playButton.setVisibility(View.VISIBLE);
//            videoThumbnail.setVisibility(View.VISIBLE);
//        } else {
//            mediaPlayer.start();
//            playButton.setVisibility(View.GONE);
//            videoThumbnail.setVisibility(View.GONE);
//        }
//    }
//
//    public boolean isPlaying() {
//        return mediaPlayer != null && mediaPlayer.isPlaying();
//    }
//
//    public int getCurrentPosition() {
//        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
//    }
//
//    public int getDuration() {
//        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
//    }
//}
