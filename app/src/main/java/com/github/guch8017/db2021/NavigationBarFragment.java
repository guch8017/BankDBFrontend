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

import com.github.guch8017.db2021.ui.FragmentAccountList;
import com.github.guch8017.db2021.ui.FragmentCustomerList;
import com.github.guch8017.db2021.ui.FragmentLoanList;
import com.github.guch8017.db2021.ui.FragmentStatistic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class NavigationBarFragment extends Fragment {
    private int currentIndex = -1;
    static final int INDEX_CUSTOMER = 0;
    static final int INDEX_ACCOUNT = 1;
    static final int INDEX_LOAN = 2;
    static final int INDEX_STAT = 3;
    static HashMap<Integer, Fragment> fragmentHashMap = new HashMap<Integer, Fragment>(){{
       put(INDEX_CUSTOMER, new FragmentCustomerList());
       put(INDEX_ACCOUNT, new FragmentAccountList());
       put(INDEX_LOAN, new FragmentLoanList());
       put(INDEX_STAT, new FragmentStatistic());
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
                case R.id.main_menu_customer:
                    switchFragments(INDEX_CUSTOMER);
                    break;
                case R.id.main_menu_account:
                    switchFragments(INDEX_ACCOUNT);
                    break;
                case R.id.main_menu_loan:
                    switchFragments(INDEX_LOAN);
                    break;
                case R.id.main_menu_statistic:
                    switchFragments(INDEX_STAT);
                    break;
                default:
                    throw new RuntimeException("未知的ItemID");
            }
            return true;
        });
        if (currentIndex == -1) {
            switchFragments(INDEX_CUSTOMER);
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
