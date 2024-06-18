package com.example.weibo;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.weibo.adapter.ImagePagerAdapter;

import java.util.List;

public class ImageDetailActivity extends AppCompatActivity {

    private List<String> images;
    private int index;
    private TextView pageIndicator;
    private ImageView downloadButton;
    private ImageView avatar;
    private TextView username;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        ViewPager viewPager = findViewById(R.id.view_pager);
        pageIndicator = findViewById(R.id.page_info);
        downloadButton = findViewById(R.id.download_button);
        avatar = findViewById(R.id.avatar);
        username = findViewById(R.id.username);

        images = getIntent().getStringArrayListExtra("images");
        index = getIntent().getIntExtra("index", 0);
        String avatarUrl = getIntent().getStringExtra("avatar");
        String usernameText = getIntent().getStringExtra("username");

        Glide.with(this).load(avatarUrl).into(avatar);
        username.setText(usernameText);

        ImagePagerAdapter adapter = new ImagePagerAdapter(this, images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(index);
        updatePageIndicator(index);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                updatePageIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                finish();
                return true;
            }
        });

        viewPager.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        downloadButton.setOnClickListener(v -> downloadImage(images.get(viewPager.getCurrentItem())));
    }

    private void updatePageIndicator(int position) {
        String indicator = (position + 1) + "/" + images.size();
        pageIndicator.setText(indicator);
    }

    private void downloadImage(String imageUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setDestinationInExternalPublicDir("Download", Uri.parse(imageUrl).getLastPathSegment());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("Downloading Image");
        request.setDescription("Image is being downloaded...");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(this, "图片下载完成，请相册查看", Toast.LENGTH_SHORT).show();
    }
}
