package com.example.weibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.weibo.LoginActivity;
import com.example.weibo.R;
import com.example.weibo.bean.WeiboInfo;
import com.example.weibo.retrofit.RetrofitApi;
import com.example.weibo.retrofit.RetrofitManager;
import com.example.weibo.utils.AnimationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeManager {

    private Context context;

    public LikeManager(Context context) {
        this.context = context;
    }

    public void like(String token, WeiboInfo weiboInfo, WeiboViewHolder weiboViewHolder) {
        RetrofitApi api = RetrofitManager.getInstance().createApi();
        RetrofitApi.LikeRequest likeRequest = new RetrofitApi.LikeRequest(weiboInfo.getId());
        api.like("Bearer " + token, likeRequest).enqueue(new Callback<RetrofitApi.ApiResponse>() {
            @Override
            public void onResponse(Call<RetrofitApi.ApiResponse> call, Response<RetrofitApi.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isData()) {
                    weiboInfo.setLikeFlag(true);
                    weiboInfo.setLikeCount(weiboInfo.getLikeCount() + 1);
                    weiboViewHolder.likeCount.setText(String.valueOf(weiboInfo.getLikeCount()));
                    weiboViewHolder.likeButton.setColorFilter(context.getResources().getColor(R.color.colorRed));
                    AnimationUtils.playLikeAnimation(weiboViewHolder.likeButton);
                } else {
                    Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetrofitApi.ApiResponse> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancelLike(String token, WeiboInfo weiboInfo, WeiboViewHolder weiboViewHolder) {
        RetrofitApi api = RetrofitManager.getInstance().createApi();
        RetrofitApi.LikeRequest likeRequest = new RetrofitApi.LikeRequest(weiboInfo.getId());
        api.cancelLike("Bearer " + token, likeRequest).enqueue(new Callback<RetrofitApi.ApiResponse>() {
            @Override
            public void onResponse(Call<RetrofitApi.ApiResponse> call, Response<RetrofitApi.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isData()) {
                    weiboInfo.setLikeFlag(false);
                    weiboInfo.setLikeCount(weiboInfo.getLikeCount() - 1);
                    weiboViewHolder.likeCount.setText(String.valueOf(weiboInfo.getLikeCount()));
                    weiboViewHolder.likeButton.setColorFilter(context.getResources().getColor(R.color.colorGrey));
                    AnimationUtils.playCancelLikeAnimation(weiboViewHolder.likeButton);
                } else {
                    Toast.makeText(context, "取消点赞失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetrofitApi.ApiResponse> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("weibo_prefs", 0);
        return sharedPreferences.getString("token", null);
    }
}
