package com.example.weibo.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weibo.MainActivity;
import com.example.weibo.R;


/**
 * 设置隐私文本的可点击部分：
 *
 * setPrivacyText方法：
 * 获取隐私文本内容。
 * 使用SpannableString设置隐私文本的可点击部分。
 * 使用ClickableSpan为用户协议和隐私政策设置点击事件。
 * 设置文本的可点击性和移动方法。
 *
 * 处理用户同意或不同意隐私政策：
 *
 * agreeButton的点击事件：
 * 设置SharedPreferences以保存用户已同意隐私政策的状态。
 * 调用startMainActivity方法启动主活动。
 * disagreeButton的点击事件：
 * 关闭当前活动。
 *
 * */

public class PrivacyFragment extends Fragment {

    private TextView agreeButton, disagreeButton, privacyText;

    public PrivacyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        agreeButton = view.findViewById(R.id.agree_text);
        disagreeButton = view.findViewById(R.id.disagree_text);
        privacyText = view.findViewById(R.id.privacy_text);

        setPrivacyText();

        agreeButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("weibo_prefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply();
            if (isAdded()) {
                startMainActivity();
            }
        });

        disagreeButton.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity().finish();
            }
        });

        return view;
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setPrivacyText() {
        String text = getString(R.string.ih_2);
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan userAgreementClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getActivity(), "查看用户协议", Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getActivity(), "查看隐私协议", Toast.LENGTH_SHORT).show();
            }
        };

        int startUserAgreement = text.indexOf("《用户协议》");
        int endUserAgreement = startUserAgreement + "《用户协议》".length();

        int startPrivacyPolicy = text.indexOf("《隐私政策》");
        int endPrivacyPolicy = startPrivacyPolicy + "《隐私政策》".length();

        spannableString.setSpan(userAgreementClick, startUserAgreement, endUserAgreement, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyPolicyClick, startPrivacyPolicy, endPrivacyPolicy, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyText.setText(spannableString);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
