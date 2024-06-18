package com.example.weibo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weibo.adapter.ViewPagerAdapter;
import com.example.weibo.welcome.PrivacyFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 检查首次运行：
 *
 * 在onCreate方法中，通过SharedPreferences读取isFirstRun标记。
 * 如果是首次运行，调用showPrivacyFragment方法显示隐私政策页面。
 * 如果不是首次运行，调用loadMainContent方法加载主界面内容。
 *
 * 显示隐私政策页面：
 *
 * 在showPrivacyFragment方法中，使用FragmentTransaction替换当前内容为PrivacyFragment。
 *
 * 加载主界面内容：
 *
 * 在onResume方法中，通过Intent获取login_success标记。
 * 如果登录成功，调用refreshProfileFragment方法刷新用户信息。
 *
 * 刷新用户信息：
 *
 * 在refreshProfileFragment方法中，找到ProfileFragment实例，并调用其loadUserInfo方法刷新用户信息。
 *
 * */


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查首次运行逻辑
        SharedPreferences sharedPreferences = getSharedPreferences("weibo_prefs", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            // 显示 PrivacyFragment
            showPrivacyFragment();
        } else {
            // 正常加载 MainActivity 的内容
            loadMainContent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 检查是否是从登录成功返回
        Intent intent = getIntent();
        boolean loginSuccess = intent.getBooleanExtra("login_success", false);
        if (loginSuccess) {
            // 刷新ProfileFragment
            refreshProfileFragment();
        }
    }

    private void showPrivacyFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment privacyFragment = new PrivacyFragment();
        transaction.replace(android.R.id.content, privacyFragment);
        transaction.commit();
    }

    private void loadMainContent() {
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        title = findViewById(R.id.title);

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("iH推荐");
                    tab.setIcon(R.drawable.icon_recommend);
                    break;
                case 1:
                    tab.setText("我的");
                    tab.setIcon(R.drawable.icon_profile);
                    break;
            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        title.setText("iH推荐");
                        break;
                    case 1:
                        title.setText("我的");
                        break;
                }
            }
        });
    }

    private void refreshProfileFragment() {
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("f1");
        if (profileFragment != null) {
            profileFragment.loadUserInfo();
        }
    }
}
