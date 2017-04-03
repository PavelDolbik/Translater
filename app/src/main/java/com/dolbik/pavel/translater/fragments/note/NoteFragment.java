package com.dolbik.pavel.translater.fragments.note;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dolbik.pavel.translater.R;
import com.dolbik.pavel.translater.adapters.ViewPagerAdapter;
import com.dolbik.pavel.translater.fragments.favorite.FavoriteFragment;
import com.dolbik.pavel.translater.fragments.history.HistoryFragment;

public class NoteFragment extends Fragment {


    private ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager();
        return view;
    }


    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        Resources resources = getResources();
        adapter.addFragment(new HistoryFragment(),  resources.getString(R.string.hf_title));
        adapter.addFragment(new FavoriteFragment(), resources.getString(R.string.ff_title));
        viewPager.setAdapter(adapter);
    }

}
