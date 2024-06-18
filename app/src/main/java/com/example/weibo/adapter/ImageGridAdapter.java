package com.example.weibo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weibo.R;

import java.util.List;

/**
 * 图片网格适配器，用于显示网格图片
 *
 * 优化：
 * 提取加载图片的方法： 提取 loadImageAsBitmap 和 loadRegularImage 两个方法，分别处理单张图片和多张图片的加载，减少了 onBindViewHolder 方法的嵌套深度。
 * 简化条件判断： onBindViewHolder 方法中，根据 isSingleImage 判断直接调用相应的方法，减少了嵌套层次。
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> imageUrls; // 图片URL列表
    private final boolean isSingleImage;  // 是否为单张图片
    private final OnImageClickListener onImageClickListener;

    // 构造函数，初始化上下文、图片URL列表以及是否为单张图片的标志
    public ImageGridAdapter(Context context, List<String> imageUrls, boolean isSingleImage, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isSingleImage = isSingleImage;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view, onImageClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        if (isSingleImage) {
            loadImageAsBitmap(holder.imageView, imageUrl);
        } else {
            loadRegularImage(holder.imageView, imageUrl);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size(); // 返回图片列表的大小
    }

    // 加载单张图片的位图
    private void loadImageAsBitmap(ImageView imageView, String imageUrl) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 当图片加载清除时可以执行的操作
                    }
                });
    }

    // 加载多张图片的常规方法
    private void loadRegularImage(ImageView imageView, String imageUrl) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = 400;  // 固定宽度
        layoutParams.height = 400; // 固定高度
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(imageUrl).into(imageView);
    }

    // 图片视图持有者
    static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView; // 图片视图
        OnImageClickListener onImageClickListener;

        // 初始化图片视图
        public ImageViewHolder(@NonNull View itemView, OnImageClickListener onImageClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            this.onImageClickListener = onImageClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onImageClickListener.onImageClick(getAdapterPosition());
        }
    }

    // 图片点击监听器
    public interface OnImageClickListener {
        void onImageClick(int position);
    }
}
