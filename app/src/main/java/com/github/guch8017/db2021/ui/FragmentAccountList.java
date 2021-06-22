package com.github.guch8017.db2021.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.guch8017.db2021.NavigationBarFragment;
import com.github.guch8017.db2021.NavigationBarFragmentDirections;
import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Account;
import com.github.guch8017.db2021.data.Model;
import com.github.guch8017.db2021.data.ModelAccountSearch;
import com.github.guch8017.db2021.data.ModelAccountSearchResponse;
import com.github.guch8017.db2021.data.ModelCustomerSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FragmentAccountList extends Fragment implements AccountListAdapter.AccountViewHolder.IOnItemClickListener, NetworkUtil.INetworkCallback<ModelAccountSearchResponse> {
    final static String TAG = "FragmentAccountList";
    private ViewDataBinding mBinding;
    private AccountListAdapter<Account> mAdapter;
    private SwipeRefreshLayout mRefresh;
    private ArrayList<Account> accounts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accounts = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        if(mBinding == null)
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_list, container, false);
        View view = mBinding.getRoot();
        RecyclerView recView = view.findViewById(R.id.account_list);

        mAdapter = new AccountListAdapter<>(this);
        mAdapter.resetList(accounts);
        mRefresh = view.findViewById(R.id.swipe_refresh);
        mRefresh.setOnRefreshListener(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FragmentAccountList.this.getContext());
            View view1 = View.inflate(FragmentAccountList.this.getContext(), R.layout.dialog_search_account, null);
            builder.setView(view1);
            final AlertDialog dialog = builder.create();
            final RadioGroup type_select = view1.findViewById(R.id.rg_acc_type);
            final RadioGroup method_select = view1.findViewById(R.id.rg_keyword_type);
            final EditText kw_editText = view1.findViewById(R.id.et_keyword);
            RadioButton method_type = view1.findViewById(R.id.rb_type);
            method_type.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    type_select.setVisibility(View.VISIBLE);
                    type_select.setEnabled(true);
                    kw_editText.setEnabled(false);
                }else{
                    type_select.setVisibility(View.INVISIBLE);
                    type_select.setEnabled(false);
                    kw_editText.setEnabled(true);
                }
            });
            final Button btn_search = view1.findViewById(R.id.btn_search);
            final Button btn_cancel = view1.findViewById(R.id.btn_cancel);
            btn_search.setOnClickListener(v -> {
                ModelAccountSearch acSearch = new ModelAccountSearch();
                switch (method_select.getCheckedRadioButtonId()){
                    case R.id.rb_id:
                        acSearch.method = 0;
                        acSearch.keyword = kw_editText.getText().toString();
                        break;
                    case R.id.rb_branch:
                        acSearch.method = 1;
                        acSearch.keyword = kw_editText.getText().toString();
                        break;
                    case R.id.rb_type:
                        acSearch.method = 2;
                        switch (type_select.getCheckedRadioButtonId()){
                            case R.id.rb_saving:
                                acSearch.keyword = "0";
                                break;
                            case R.id.rb_checking:
                                acSearch.keyword = "1";
                                break;
                            default:
                                Log.e(TAG, "Unknown account type id " + type_select.getCheckedRadioButtonId());
                                return;
                        }
                        break;
                    case R.id.rb_customer:
                        acSearch.method = 3;
                        acSearch.keyword = kw_editText.getText().toString();
                        break;
                    default:
                        Log.e(TAG, "Unknown method type id " + type_select.getCheckedRadioButtonId());
                        return;
                }
                doAccountSearch(acSearch);
                dialog.dismiss();
            });
            btn_cancel.setOnClickListener(v -> {
                dialog.dismiss();
                mRefresh.setRefreshing(false);
            });
            final Button btn_load = view1.findViewById(R.id.btn_load);
            btn_load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NetworkUtil<ModelAccountSearchResponse>().doGetRequest(Static.ACCOUNT_GET, FragmentAccountList.this, ModelAccountSearchResponse.class);
                    dialog.dismiss();
                }
            });
            dialog.setOnCancelListener(dialog1 -> mRefresh.setRefreshing(false));
            dialog.show();
        });
        FloatingActionButton fab = view.findViewById(R.id.list_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FragmentAccountList.this).navigate(NavigationBarFragmentDirections.naviBarToAccountInfoModifier(new Account(), true));
            }
        });
        recView.setAdapter(mAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onAccountItemClick(Account account) {
        NavController ctl = NavHostFragment.findNavController(this);
        ctl.navigate(NavigationBarFragmentDirections.naviBarToAccountInfoModifier(account, false));
    }


    private void doAccountSearch(ModelAccountSearch param){
        new NetworkUtil<ModelAccountSearchResponse>().doRequestWithSession(Static.ACCOUNT_SEARCH, param, this, ModelAccountSearchResponse.class);
    }


    @Override
    public void onResponse(ModelAccountSearchResponse model) {
        if(!model.success){
            Toast.makeText(getContext(), "错误代码" + (model.code) + "\n" + model.msg, Toast.LENGTH_LONG).show();
        }else {
            accounts.clear();
            accounts.addAll(model.data);
        }
        FragmentAccountList.this.getActivity().runOnUiThread(() -> {
            mRefresh.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onNoLogin() {
        getActivity().runOnUiThread(() -> {
            mRefresh.setRefreshing(false);
            NavController ctl = NavHostFragment.findNavController(FragmentAccountList.this);
            ctl.navigate(R.id.navi_bar_to_login_fragment);
        });

    }

    @Override
    public void onFailure(Exception e) {
        FragmentAccountList.this.getActivity().runOnUiThread(() -> mRefresh.setRefreshing(false));
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
    }
}
