package com.github.guch8017.db2021.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.data.ModelCustomerSearch;
import com.github.guch8017.db2021.data.ModelCustomerSearchResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FragmentCustomerList extends Fragment implements CustomerListAdapter.CustomerViewHolder.IOnItemClickListener, NetworkUtil.INetworkCallback<ModelCustomerSearchResponse>{
    final static String TAG = "FragmentCustomerList";
    private ViewDataBinding mBinding;
    private SwipeRefreshLayout mRefresh;
    private CustomerListAdapter<Customer> mAdapter;
    private ArrayList<Customer> customers = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_list, container, false);
        View view = mBinding.getRoot();
        mRefresh = view.findViewById(R.id.swipe_refresh);
        RecyclerView recView = view.findViewById(R.id.customer_list);
        mAdapter = new CustomerListAdapter<>(this);
        mAdapter.resetList(customers);
        recView.setAdapter(mAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRefresh.setOnRefreshListener(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FragmentCustomerList.this.getContext());
            View view1 = View.inflate(FragmentCustomerList.this.getContext(), R.layout.dialog_search_customer, null);
            builder.setView(view1);
            final AlertDialog dialog = builder.create();
            final Button btn_search = view1.findViewById(R.id.btn_search);
            btn_search.setOnClickListener(v -> {
                doCustomerSearch(view1);
                dialog.dismiss();
            });
            view1.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
                dialog.dismiss();
                mRefresh.setRefreshing(false);
            });
            dialog.setOnCancelListener(dialog1 -> mRefresh.setRefreshing(false));
            dialog.show();
        });
        FloatingActionButton fab = view.findViewById(R.id.list_add);
        fab.setOnClickListener(v -> NavHostFragment.findNavController(FragmentCustomerList.this).navigate(NavigationBarFragmentDirections.naviBarToCustomerInfoModifier(new Customer(), true)));
        return view;
    }

    private void doCustomerSearch(@NonNull View view){
        RadioGroup methodGroup = view.findViewById(R.id.rg_keyword_type);
        RadioGroup typeGroup = view.findViewById(R.id.rg_search_type);
        ModelCustomerSearch search = new ModelCustomerSearch();
        switch (methodGroup.getCheckedRadioButtonId()){
            case R.id.rb_id:
                search.mode = Static.METHOD_ID;
                break;
            case R.id.rb_name:
                search.mode = Static.METHOD_NAME;
                break;
            case R.id.rb_phone:
                search.mode = Static.METHOD_PHONE;
                break;
            default:
                throw new RuntimeException("Unknown search mode");
        }

        switch (typeGroup.getCheckedRadioButtonId()){
            case R.id.rb_exact:
                search.exact = true;
                break;
            case R.id.rb_approx:
                search.exact = false;
                break;
            default:
                throw new RuntimeException("Unknown search type");
        }

        EditText kwEt = view.findViewById(R.id.et_keyword);
        search.keyword = kwEt.getText().toString();
        new NetworkUtil<ModelCustomerSearchResponse>().doRequestWithSession(Static.CUSTOMER_SEARCH, search, FragmentCustomerList.this, ModelCustomerSearchResponse.class);

    }

    @Override
    public void onCustomerItemClick(Customer customer, int position) {
        NavHostFragment.findNavController(this).navigate(NavigationBarFragmentDirections.naviBarToCustomerInfoModifier(customer, false));
    }

    @Override
    public void onResponse(ModelCustomerSearchResponse model) {
        customers.clear();
        customers.addAll(model.data);
        FragmentCustomerList.this.getActivity().runOnUiThread(() -> {
            mRefresh.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onNoLogin() {
        getActivity().runOnUiThread(() -> {
            mRefresh.setRefreshing(false);
            NavController ctl = NavHostFragment.findNavController(FragmentCustomerList.this);
            ctl.navigate(R.id.navi_bar_to_login_fragment);
        });
    }

    @Override
    public void onFailure(Exception e) {
        FragmentCustomerList.this.getActivity().runOnUiThread(() -> mRefresh.setRefreshing(false));
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
    }
}
