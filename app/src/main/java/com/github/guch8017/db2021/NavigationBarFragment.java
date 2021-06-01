package com.github.guch8017.db2021;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.guch8017.umaguide.ui.support_card_list.SupportCardListFragment;
import com.github.guch8017.umaguide.ui.uma_list.UmaListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class NavigationBarFragment extends Fragment {
    private int currentIndex = -1;
    static final int UMA_INDEX = 0;
    static final int SP_INDEX = 1;
    static final int RACE_INDEX = 2;
    static final int SETTING_INDEX = 3;
    static HashMap<Integer, Fragment> fragmentHashMap = new HashMap<Integer, Fragment>(){{
       put(UMA_INDEX, new UmaListFragment());
       put(SP_INDEX, new SupportCardListFragment());
    }};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        BottomNavigationView nav = view.findViewById(R.id.bottom_nav_view);
        nav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.main_menu_uma_musume:
                    switchFragments(UMA_INDEX);
                    break;
                case R.id.main_menu_support_card:
                    switchFragments(SP_INDEX);
                    break;
                case R.id.main_menu_race:
                    switchFragments(RACE_INDEX);
                    break;
                case R.id.main_menu_setting:
                    switchFragments(SETTING_INDEX);
                    break;
                default:
                    throw new RuntimeException("未知的ItemID");
            }
            return true;
        });
        if (currentIndex == -1) {
            switchFragments(UMA_INDEX);
        }
        return view.getRootView();
    }

    private void switchFragments(int index) {
        currentIndex = index;
        Log.i("NavigationBar", String.valueOf(index));
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        Fragment frameContainer = getChildFragmentManager().findFragmentById(R.id.frame_container);
        if(frameContainer != null){
            transaction.detach(frameContainer);
        }

        Fragment fragment = getChildFragmentManager().findFragmentByTag(String.valueOf(index));
        if (fragment == null) {
            fragment = fragmentHashMap.get(index);
            transaction.add(R.id.frame_container, fragment, String.valueOf(index));
        } else {
            transaction.attach(fragment);
        }

        transaction
                .setPrimaryNavigationFragment(fragment)
                .setReorderingAllowed(true)
                .commit();
    }
}
