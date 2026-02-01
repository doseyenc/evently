package com.doseyenc.evently.ui.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DetailPagerAdapter extends FragmentStateAdapter {

    public DetailPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new DetailCommentsFragment();
            case 2:
                return new DetailLiveStatusFragment();
            default:
                return new DetailParticipantsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
