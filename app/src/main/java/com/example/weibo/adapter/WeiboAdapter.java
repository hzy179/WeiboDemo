package com.example.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weibo.R;
import com.example.weibo.bean.WeiboInfo;
import com.example.weibo.widget.CustomVideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 解耦合：
 * 分离 ViewHolder 类：
 *
 * 将 ViewHolder 类移动到单独的文件中。
 * 抽取通用功能：
 *
 * 抽取视频播放相关逻辑到单独的管理类 VideoManager。
 * 抽取点赞和取消点赞逻辑到单独的管理类 LikeManager。
 * 抽取动画和时间格式化等工具方法到单独的工具类。
 * */
public class WeiboAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WeiboInfo> weiboInfoList = new ArrayList<>();
    private Context context;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private VideoManager videoManager;
    private LikeManager likeManager;

    public WeiboAdapter(Context context) {
        this.context = context;
        this.videoManager = new VideoManager();
        this.likeManager = new LikeManager(context);
    }

    @Override
    public int getItemViewType(int position) {
        return weiboInfoList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weibo, parent, false);
            return new WeiboViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WeiboViewHolder) {
            WeiboInfo weiboInfo = weiboInfoList.get(position);
            WeiboViewHolder weiboViewHolder = (WeiboViewHolder) holder;
            weiboViewHolder.bindData(weiboInfo, context, videoManager, likeManager, this);
        }
    }

    @Override
    public int getItemCount() {
        return weiboInfoList == null ? 0 : weiboInfoList.size();
    }

    public void setWeiboInfoList(List<WeiboInfo> weiboInfoList) {
        Collections.shuffle(weiboInfoList);
        this.weiboInfoList = weiboInfoList;
        notifyDataSetChanged();
    }

    public void addWeiboInfoList(List<WeiboInfo> weiboInfoList) {
        int startPosition = this.weiboInfoList.size();
        this.weiboInfoList.addAll(weiboInfoList);
        notifyItemRangeInserted(startPosition, weiboInfoList.size());
        if (weiboInfoList.isEmpty()) {
            Toast.makeText(context, "无更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearWeiboInfoList() {
        this.weiboInfoList.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < weiboInfoList.size()) {
            weiboInfoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, weiboInfoList.size());
        }
    }

    public void addLoadingFooter() {
        weiboInfoList.add(null);
        notifyItemInserted(weiboInfoList.size() - 1);
    }

    public void removeLoadingFooter() {
        if (!weiboInfoList.isEmpty()) {
            int position = weiboInfoList.size() - 1;
            WeiboInfo item = weiboInfoList.get(position);
            if (item == null) {
                weiboInfoList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public boolean isLoadingFooterVisible() {
        return !weiboInfoList.isEmpty() && weiboInfoList.get(weiboInfoList.size() - 1) == null;
    }

    public int getAdapterPositionForVideoView(CustomVideoView videoView) {
        for (int i = 0; i < weiboInfoList.size(); i++) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) videoView.getTag();
            if (holder != null && holder.getAdapterPosition() == i) {
                return i;
            }
        }
        return -1;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
