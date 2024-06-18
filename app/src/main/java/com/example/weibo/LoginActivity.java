package com.example.weibo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import com.example.weibo.retrofit.RetrofitApi;
import com.example.weibo.retrofit.RetrofitManager;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *设置按钮的点击事件：
 *
 * 返回按钮：finish()方法关闭当前活动。
 * 获取验证码按钮：调用sendVerificationCode()方法，发送验证码。
 * 登录按钮：调用login()方法，进行登录操作。
 * 跳过登录按钮：跳转到推荐页RecommendFragment，并传递跳过登录标记。
 *
 *手机号和验证码的输入：
 *
 * SimpleTextWatcher类：监听手机号和验证码输入框的文本变化，更新按钮状态。
 * updateButtonStates()方法：根据手机号和验证码的长度，设置获取验证码按钮和登录按钮的可用状态。
 * 对只允许数字的输入这一块的限制在xml里
 *
 * 发送验证码：
 *
 * sendVerificationCode()方法：
 * 检查手机号是否符合要求。
 * 发送验证码请求，通过RetrofitApi接口调用sendCode方法。
 * 请求成功后，显示提示并启动验证码重试计时器。
 * 请求失败后，显示提示并更新按钮状态。
 *
 * 启动验证码重试计时器：
 *
 * startRetryTimer()方法：使用Handler和Runnable实现验证码重试计时器，每秒更新按钮状态和倒计时。
 * RetryRunnable类：实现Runnable接口，更新获取验证码按钮的文本和状态。
 *
 * 登录操作：
 *
 * login()方法：
 * 检查手机号和验证码是否符合要求。
 * 发送登录请求，通过RetrofitApi接口调用login方法。
 * 请求成功后，保存Token，显示提示并跳转到主页面MainActivity，传递登录成功信息。
 * 请求失败后，显示提示。
 *
 * 内存管理：
 *
 * 在onDestroy()方法中，移除所有Handler回调，避免内存泄露。
 * 不然可能导致应用崩溃   》=《
 * */

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etVerificationCode;
    private Button btnGetVerificationCode, btnLogin, btnSkipLogin;
    private TextView btnBack;
    private int retryTime = 60;
    private boolean isSending = false;
    private static final String TAG = "LoginActivity";
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = findViewById(R.id.et_phone);
        etVerificationCode = findViewById(R.id.et_verification_code);
        btnGetVerificationCode = findViewById(R.id.btn_get_verification_code);
        btnLogin = findViewById(R.id.btn_login);
        btnSkipLogin = findViewById(R.id.btn_skip_login);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnGetVerificationCode.setOnClickListener(v -> sendVerificationCode());
        btnLogin.setOnClickListener(v -> login());
        btnSkipLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecommendFragment.class);
            intent.putExtra("login_success", false); // 跳过登录标记
            startActivity(intent);
            finish();
        });

        etPhone.addTextChangedListener(new SimpleTextWatcher(this));
        etVerificationCode.addTextChangedListener(new SimpleTextWatcher(this));

        updateButtonStates();
    }

    private void updateButtonStates() {
        String phone = etPhone.getText().toString().trim();
        String verificationCode = etVerificationCode.getText().toString().trim();

        btnGetVerificationCode.setEnabled(phone.length() == 11 && !isSending);
        btnLogin.setEnabled(phone.length() == 11 && verificationCode.length() == 6);
    }

    private void sendVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty() || phone.length() != 11) {
            Toast.makeText(this, "请输入完整手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        isSending = true;
        updateButtonStates();

        RetrofitApi api = RetrofitManager.getInstance().createApi();
        Call<RetrofitApi.ApiResponse> call = api.sendCode(new RetrofitApi.SendCodeRequest(phone));
        call.enqueue(new Callback<RetrofitApi.ApiResponse>() {
            @Override
            public void onResponse(Call<RetrofitApi.ApiResponse> call, Response<RetrofitApi.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    startRetryTimer();
                } else {
                    Toast.makeText(LoginActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                    isSending = false;
                    updateButtonStates();
                }
            }

            @Override
            public void onFailure(Call<RetrofitApi.ApiResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                isSending = false;
                updateButtonStates();
            }
        });
    }

    private void startRetryTimer() {
        handler.postDelayed(new RetryRunnable(this), 1000);
    }

    private static class RetryRunnable implements Runnable {
        private final WeakReference<LoginActivity> activityReference;

        RetryRunnable(LoginActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            LoginActivity activity = activityReference.get();
            if (activity == null || activity.retryTime <= 0) {
                if (activity != null) {
                    activity.btnGetVerificationCode.setText("获取验证码");
                    activity.isSending = false;
                    activity.retryTime = 60;
                    activity.updateButtonStates();
                }
                return;
            }

            activity.runOnUiThread(() -> {
                activity.btnGetVerificationCode.setText(activity.retryTime + "s");
                activity.updateButtonStates();
            });

            activity.retryTime--;
            activity.handler.postDelayed(this, 1000);
        }
    }

    private void login() {
        String phone = etPhone.getText().toString().trim();
        String verificationCode = etVerificationCode.getText().toString().trim();
        if (phone.isEmpty() || phone.length() != 11 || verificationCode.isEmpty() || verificationCode.length() != 6) {
            Toast.makeText(this, "请输入正确的手机号和验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitApi api = RetrofitManager.getInstance().createApi();
        Call<RetrofitApi.LoginResponse> call = api.login(new RetrofitApi.LoginRequest(phone, verificationCode));
        call.enqueue(new Callback<RetrofitApi.LoginResponse>() {
            @Override
            public void onResponse(Call<RetrofitApi.LoginResponse> call, Response<RetrofitApi.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    String token = response.body().getData();
                    SharedPreferences sharedPreferences = getSharedPreferences("weibo_prefs", MODE_PRIVATE);
                    sharedPreferences.edit().putString("token", token).apply();
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                    // 跳转到主页面并传递登录成功信息
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("login_success", true);
                    startActivity(intent);

                    // 结束当前Activity
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetrofitApi.LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // 移除所有Handler回调，避免内存泄露
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final WeakReference<LoginActivity> activityReference;

        SimpleTextWatcher(LoginActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        public void afterTextChanged(Editable s) {
            LoginActivity activity = activityReference.get();
            if (activity != null) {
                activity.updateButtonStates();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}
