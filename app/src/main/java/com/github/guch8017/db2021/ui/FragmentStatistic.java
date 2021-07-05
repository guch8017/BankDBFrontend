package com.github.guch8017.db2021.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.guch8017.db2021.NavigationBarFragmentDirections;
import com.github.guch8017.db2021.R;

public class FragmentStatistic extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistic, container, false);
        v.findViewById(R.id.user_stat_layout).setOnClickListener(view -> {
            NavHostFragment.findNavController(FragmentStatistic.this).navigate(NavigationBarFragmentDirections.actionNaviBarFragmentToNaviStatUser());
        });
        v.findViewById(R.id.branch_stat_layout).setOnClickListener(view2 -> {
            NavHostFragment.findNavController(FragmentStatistic.this).navigate(NavigationBarFragmentDirections.actionNaviBarFragmentToNaviStatBranch());
        });
        return v;
    }



}
