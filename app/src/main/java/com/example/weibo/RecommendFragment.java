package com.example.weibo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weibo.adapter.WeiboAdapter;
import com.example.weibo.bean.WeiboInfo;
import com.example.weibo.retrofit.GenericApiResponse;
import com.example.weibo.retrofit.RetrofitApi;
import com.example.weibo.retrofit.RetrofitManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 数据加载逻辑：
 *
 * 初次加载和下拉刷新时调用 fetchWeiboData 方法。
 * 在上拉加载更多时，先添加一个加载项，然后调用 fetchWeiboData 方法。
 *
 * fetchWeiboData 方法：
 *
 * 设置 isLoading 标志，防止重复加载。
 * 创建 Retrofit API 实例，准备请求。
 * 根据是否有 token，设置请求头。
 * 发起网络请求，获取推荐的微博数据。
 *
 * 处理网络请求响应：
 *
 * 简述：
 *
 * 网络请求
 * 使用 Retrofit 发起网络请求。
 * 请求成功时，根据当前页码更新适配器的数据源。
 * 请求失败时，显示错误提示。
 *
 * 详细：
 * 在请求成功时，根据当前页码更新适配器的数据源。
 * 如果是第一页数据，清空现有数据并添加新数据。
 * 如果是加载更多，移除加载项，并添加新数据。
 * 如果没有更多数据，显示 "无更多内容" 的提示。
 * 在请求失败时，显示错误提示，并处理错误页面的显示。
 *
 * 视图更新方法：
 *
 * 这玩意还是难搞，很容易xjb显示，我的视频播放前会黑屏不知道食是不是因为这个？？？
 * showLoading: 显示加载中的视图，隐藏其他视图。
 * hideLoading: 隐藏加载中的视图，显示主要内容视图。
 * showError: 显示错误视图，隐藏其他视图。
 * checkIfEmpty: 检查数据是否为空，如果为空则显示空视图。
 *
 * 获取和处理 token：
 *
 * 从 SharedPreferences 中获取 token。
 * 如果 token 无效，移除 token 并跳转到登录页面。
 * */


public class RecommendFragment extends Fragment {

    private static final String TAG = "RecommendFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private WeiboAdapter weiboAdapter;
    private FrameLayout loadingLayout;
    private FrameLayout errorLayout;
    private TextView emptyView;
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean isFirstLoad = true;

    public RecommendFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        loadingLayout = view.findViewById(R.id.loading_layout);
        errorLayout = view.findViewById(R.id.error_layout);
        emptyView = view.findViewById(R.id.empty_view);
        Button retryButton = errorLayout.findViewById(R.id.retry_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weiboAdapter = new WeiboAdapter(getContext());
        recyclerView.setAdapter(weiboAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::refreshWeiboData);
        retryButton.setOnClickListener(v -> {
            showLoading();
            refreshWeiboData();
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    currentPage++;
                    loadMoreWeiboData();
                }
            }
        });

        fetchWeiboData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipeRefreshLayout = null;
        recyclerView = null;
        loadingLayout = null;
        errorLayout = null;
    }

    private void refreshWeiboData() {
        currentPage = 1;
        weiboAdapter.clearWeiboInfoList();
        fetchWeiboData();
    }

    private void loadMoreWeiboData() {
        if (!isLoading) {
            weiboAdapter.addLoadingFooter();
            fetchWeiboData();
        }
    }

    private void fetchWeiboData() {
        isLoading = true;
        RetrofitApi api = RetrofitManager.getInstance().createApi();
        String token = getToken();

        if (isFirstLoad) {
            showLoading();
        }

        api.getHomePage(token != null ? "Bearer " + token : null, currentPage, pageSize).enqueue(new Callback<GenericApiResponse<RetrofitApi.Page<WeiboInfo>>>() {

            @Override
            public void onResponse(Call<GenericApiResponse<RetrofitApi.Page<WeiboInfo>>> call, Response<GenericApiResponse<RetrofitApi.Page<WeiboInfo>>> response) {
                isLoading = false;
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                hideLoading();


                /**未登录状态的数据加载实现
                 * * 修改 RetrofitApi 接口，允许在没有token的情况下进行数据请求。添加了一个@Nullable，就OK了
                 *  * 修改 RecommendFragment 中的网络请求逻辑，判断token是否存在，并根据情况进行处理。在token为空时，不请求Authorization头。
                 *  * 更新 RetrofitManager 中的构建逻辑，避免在没有token时添加Authorization头。
                 * */
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    RetrofitApi.Page<WeiboInfo> page = response.body().getData();
                    List<WeiboInfo> weiboInfoList = page.getRecords();
                    Log.d(TAG, "Received data: " + weiboInfoList);
                    if (currentPage == 1) {
                        weiboAdapter.setWeiboInfoList(weiboInfoList);
                        checkIfEmpty();
                    } else {
                        weiboAdapter.removeLoadingFooter();
                        if (weiboInfoList.isEmpty()) {
                            Toast.makeText(getContext(), "无更多内容", Toast.LENGTH_SHORT).show();
                        } else {
                            weiboAdapter.addWeiboInfoList(weiboInfoList);
                        }
                    }
                    isFirstLoad = false;
                } else if (response.code() == 403) {
                    Log.e(TAG, "Token is invalid. Redirecting to login.");
                    handleInvalidToken();
                } else {
                    Log.e(TAG, "Response failed: " + response.message());
                    Toast.makeText(getContext(), "加载失败: " + response.message(), Toast.LENGTH_SHORT).show();
                    weiboAdapter.removeLoadingFooter();
                    if (currentPage == 1) {
                        showError();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericApiResponse<RetrofitApi.Page<WeiboInfo>>> call, Throwable t) {
                Log.e(TAG, "Request failed", t);
                Toast.makeText(getContext(), "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false;
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                weiboAdapter.removeLoadingFooter();
                if (currentPage == 1) {
                    showError();
                }
            }
        });
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private void showError() {
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void checkIfEmpty() {
        if (weiboAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("weibo_prefs", 0);
        return sharedPreferences.getString("token", null);
    }

    private void handleInvalidToken() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("weibo_prefs", 0);
        sharedPreferences.edit().remove("token").apply();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
