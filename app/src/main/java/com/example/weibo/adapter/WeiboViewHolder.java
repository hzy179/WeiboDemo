package com.example.weibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weibo.ImageDetailActivity;
import com.example.weibo.LoginActivity;
import com.example.weibo.R;
import com.example.weibo.bean.WeiboInfo;
import com.example.weibo.widget.CustomVideoView;

import java.util.ArrayList;

public class WeiboViewHolder extends RecyclerView.ViewHolder {

    ImageView avatar;
    TextView username;
    TextView title;
    ImageView image;
    RecyclerView gridView;
    View videoContainer;
    ImageView videoThumbnail;
    ImageView playButton;
    CustomVideoView videoView;
    TextView likeCount;
    ImageView likeButton;
    ImageView deleteButton;
    ImageView commentButton;
    SeekBar seekBar;
    TextView currentTime;
    TextView totalTime;

    public WeiboViewHolder(@NonNull View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.avatar);
        username = itemView.findViewById(R.id.username);
        title = itemView.findViewById(R.id.title);
        image = itemView.findViewById(R.id.image);
        gridView = itemView.findViewById(R.id.grid_view);
        videoContainer = itemView.findViewById(R.id.video_container);
        videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
        playButton = itemView.findViewById(R.id.play_button);
        videoView = itemView.findViewById(R.id.video_view);
        likeCount = itemView.findViewById(R.id.like_count);
        likeButton = itemView.findViewById(R.id.like_button);
        deleteButton = itemView.findViewById(R.id.delete_button);
        commentButton = itemView.findViewById(R.id.comment);
        seekBar = itemView.findViewById(R.id.video_seekbar);
        currentTime = itemView.findViewById(R.id.video_current_time);
        totalTime = itemView.findViewById(R.id.video_total_time);
    }

    public void bindData(WeiboInfo weiboInfo, Context context, VideoManager videoManager, LikeManager likeManager, WeiboAdapter adapter) {
        username.setText(weiboInfo.getUsername());
        title.setText(weiboInfo.getTitle());
        likeCount.setText(String.valueOf(weiboInfo.getLikeCount()));

        Glide.with(context).load(weiboInfo.getAvatar()).into(avatar);

        videoContainer.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);

        if (weiboInfo.getVideoUrl() != null && !weiboInfo.getVideoUrl().isEmpty()) {
            videoContainer.setVisibility(View.VISIBLE);
            videoThumbnail.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            videoView.setBackgroundColor(Color.BLACK);

            Glide.with(context).load(weiboInfo.getPoster()).into(videoThumbnail);
            videoView.setVideoURI(Uri.parse(weiboInfo.getVideoUrl()));

            videoManager.setupVideoView(videoView, videoThumbnail, playButton, seekBar, currentTime, totalTime, context, adapter);

        } else if (weiboInfo.getImages() != null && !weiboInfo.getImages().isEmpty()) {
            if (weiboInfo.getImages().size() == 1) {
                String imageUrl = weiboInfo.getImages().get(0);
                image.setVisibility(View.VISIBLE);
                image.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    intent.putStringArrayListExtra("images", new ArrayList<>(weiboInfo.getImages()));
                    intent.putExtra("index", 0);
                    intent.putExtra("avatar", weiboInfo.getAvatar());
                    intent.putExtra("username", weiboInfo.getUsername());
                    context.startActivity(intent);
                });
                Glide.with(context).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        ViewGroup.LayoutParams params = image.getLayoutParams();
                        if (width > height) {
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            image.setScaleType(ImageView.ScaleType.FIT_START);
                        } else {
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            image.setScaleType(ImageView.ScaleType.FIT_START);
                        }
                        image.setLayoutParams(params);
                        image.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
            } else {
                gridView.setVisibility(View.VISIBLE);
                gridView.setLayoutManager(new GridLayoutManager(context, 3));
                gridView.setAdapter(new ImageGridAdapter(context, weiboInfo.getImages(), false, pos -> {
                    Intent intent = new Intent(context, ImageDetailActivity.class);
                    intent.putStringArrayListExtra("images", new ArrayList<>(weiboInfo.getImages()));
                    intent.putExtra("index", pos);
                    intent.putExtra("avatar", weiboInfo.getAvatar());
                    intent.putExtra("username", weiboInfo.getUsername());
                    context.startActivity(intent);
                }));
            }
        }

        deleteButton.setOnClickListener(v -> {
            Toast.makeText(context, "删除" + weiboInfo.getTitle(), Toast.LENGTH_SHORT).show();
            adapter.removeItem(getAdapterPosition());
        });

        commentButton.setOnClickListener(v -> {
            int pos = getAdapterPosition();
            Toast.makeText(context, "点击第" + (pos + 1) + "条数据评论按钮", Toast.LENGTH_SHORT).show();
        });

        likeButton.setOnClickListener(v -> {
            String token = likeManager.getToken();
            if (token == null) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            } else {
                if (weiboInfo.isLikeFlag()) {
                    likeManager.cancelLike(token, weiboInfo, this);
                } else {
                    likeManager.like(token, weiboInfo, this);
                }
            }
        });

        if (weiboInfo.isLikeFlag()) {
            likeButton.setColorFilter(context.getResources().getColor(R.color.colorRed));
        } else {
            likeButton.setColorFilter(context.getResources().getColor(R.color.colorGrey));
        }
    }
}
