package com.example.weibo.adapter;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.weibo.ProfileFragment;
import com.example.weibo.RecommendFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecommendFragment();
            case 1:
                return new ProfileFragment();
            default:
                return new RecommendFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
