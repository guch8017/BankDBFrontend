package com.github.guch8017.db2021.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Account;
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.view_model.GlobalShareVM;

public class AccountTypeSelector extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_type_selector, container, false);

        GlobalShareVM vm = new ViewModelProvider(getActivity()).get(GlobalShareVM.class);
        Account acc = vm.getAccount();
        if(acc == null){
            Toast.makeText(getContext(), "无法获取存储实例：修改将无法保存", Toast.LENGTH_LONG).show();
            return v;
        }
        v.findViewById(R.id.layout_saving).setOnClickListener(v1 -> {
            acc.type = Static.ACCOUNT_TYPE_SAVING;
            vm.setAccount(acc);
            vm.setChanges(true);
            NavHostFragment.findNavController(AccountTypeSelector.this).navigateUp();
        });
        v.findViewById(R.id.layout_cheque).setOnClickListener(v12 -> {
            acc.type = Static.ACCOUNT_TYPE_CHEQUES;
            vm.setAccount(acc);
            vm.setChanges(true);
            NavHostFragment.findNavController(AccountTypeSelector.this).navigateUp();
        });
        return v;
    }
}
