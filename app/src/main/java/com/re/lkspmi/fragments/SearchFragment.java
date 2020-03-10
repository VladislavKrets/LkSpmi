package com.re.lkspmi.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.re.lkspmi.R;
import com.re.lkspmi.adapters.ViewPagerAdapter;

public class SearchFragment extends Fragment {

    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter adapter = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, null);
        viewPager = (ViewPager) view.findViewById(R.id.search_viewpager);
        setupViewPager(viewPager);
        tabLayout = view.findViewById(R.id.search_tablayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new StudentsSearchFragment(), "Студенты");
        adapter.addFragment(new EmployeesSearchFragment(), "Работники");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
