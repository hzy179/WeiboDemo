package com.example.weibo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.weibo.bean.BaseBean;
import com.example.weibo.bean.UserInfo;
import com.example.weibo.retrofit.RetrofitApi;
import com.example.weibo.retrofit.RetrofitManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 加载用户信息：
 *
 * 在loadUserInfo方法中，从SharedPreferences中获取存储的token。
 * 如果token存在，使用Retrofit发送网络请求获取用户信息。
 * 请求成功时，更新界面显示用户名和头像。
 * 请求失败时，调用resetToLoggedOutState方法重置为未登录状态。
 * 如果token不存在，直接调用resetToLoggedOutState方法重置为未登录状态。
 *
 * 重置为未登录状态：
 *
 * 在resetToLoggedOutState方法中，设置用户名为"请先登录"。
 * 显示登录提示信息。
 * 设置空消息提示为"登录后查看"。
 * 隐藏注销按钮。
 * 设置头像为默认头像。
 * */
public class ProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private TextView usernameTextView;
    private TextView loginHintTextView;
    private TextView emptyMessageTextView;
    private TextView logoutButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        avatarImageView = view.findViewById(R.id.avatar);
        usernameTextView = view.findViewById(R.id.username);
        loginHintTextView = view.findViewById(R.id.login_hint);
        emptyMessageTextView = view.findViewById(R.id.empty_message);
        logoutButton = view.findViewById(R.id.logout_button);

        avatarImageView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("weibo_prefs", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);
            if (token == null) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        loadUserInfo();

        logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("weibo_prefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().remove("token").apply();
            Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
            loadUserInfo();
        });

        return view;
    }

    public void loadUserInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("weibo_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            RetrofitApi api = RetrofitManager.getInstance().createApi();
            Call<RetrofitApi.UserInfoResponse> call = api.getUserInfo("Bearer " + token);
            call.enqueue(new Callback<RetrofitApi.UserInfoResponse>() {
                @Override
                public void onResponse(Call<RetrofitApi.UserInfoResponse> call, Response<RetrofitApi.UserInfoResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserInfo userInfo = response.body().getData();
                        usernameTextView.setText(userInfo.getUsername());
                        loginHintTextView.setVisibility(View.GONE);
                        if (userInfo.getAvatar() != null) {
                            Glide.with(ProfileFragment.this)
                                    .load(userInfo.getAvatar())
                                    .transform(new CircleCrop())
                                    .into(avatarImageView);
                        }
                        emptyMessageTextView.setText("你没有新的动态哦~");
                        logoutButton.setVisibility(View.VISIBLE);
                    } else {
                        resetToLoggedOutState();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitApi.UserInfoResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "加载用户信息失败", Toast.LENGTH_SHORT).show();
                    resetToLoggedOutState();
                }
            });
        } else {
            resetToLoggedOutState();
        }
    }

    private void resetToLoggedOutState() {
        usernameTextView.setText("请先登录");
        loginHintTextView.setVisibility(View.VISIBLE);
        emptyMessageTextView.setText("登录后查看");
        logoutButton.setVisibility(View.GONE);
        avatarImageView.setImageResource(R.drawable.default_avatar);
    }
}
