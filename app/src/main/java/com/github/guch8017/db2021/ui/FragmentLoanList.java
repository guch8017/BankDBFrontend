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

import com.github.guch8017.db2021.NavigationBarFragmentDirections;
import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.data.Loan;
import com.github.guch8017.db2021.data.ModelCustomerSearch;
import com.github.guch8017.db2021.data.ModelLoanSearch;
import com.github.guch8017.db2021.data.ModelLoanSearchResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FragmentLoanList extends Fragment implements LoanListAdapter.LoanViewHolder.IOnItemClickListener, NetworkUtil.INetworkCallback<ModelLoanSearchResponse>{
    final static String TAG = "FragmentCustomerList";
    private ViewDataBinding mBinding;
    private SwipeRefreshLayout mRefresh;
    private LoanListAdapter<Loan> mAdapter;
    private ArrayList<Loan> loans = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_loan_list, container, false);
        View view = mBinding.getRoot();
        mRefresh = view.findViewById(R.id.swipe_refresh);
        RecyclerView recView = view.findViewById(R.id.loan_list);
        mAdapter = new LoanListAdapter<>(this);
        mAdapter.resetList(loans);
        recView.setAdapter(mAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRefresh.setOnRefreshListener(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FragmentLoanList.this.getContext());
            View view1 = View.inflate(FragmentLoanList.this.getContext(), R.layout.dialog_search_loan, null);
            builder.setView(view1);
            final AlertDialog dialog = builder.create();
            final Button btn_search = view1.findViewById(R.id.btn_search);
            btn_search.setOnClickListener(v -> {
                doLoanSearch(view1);
                dialog.dismiss();
            });
            view1.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
                dialog.dismiss();
                mRefresh.setRefreshing(false);
            });
            dialog.setOnCancelListener(dialog1 -> mRefresh.setRefreshing(false));
            view1.findViewById(R.id.btn_load).setOnClickListener(v -> {
                new NetworkUtil<ModelLoanSearchResponse>().doGetRequest(Static.LOAN_GET, FragmentLoanList.this, ModelLoanSearchResponse.class);
                dialog.dismiss();
            });
            dialog.show();
        });
        FloatingActionButton fab = view.findViewById(R.id.list_add);
        fab.setOnClickListener(v -> NavHostFragment.findNavController(FragmentLoanList.this).navigate(NavigationBarFragmentDirections.actionNaviBarFragmentToNaviLoanInfoModifier(new Loan(), true)));
        return view;
    }

    private void doLoanSearch(@NonNull View view){
        RadioGroup methodGroup = view.findViewById(R.id.rg_keyword_type);
        int mode;
        switch (methodGroup.getCheckedRadioButtonId()){
            case R.id.rb_id:
                mode = Static.METHOD_ID;
                break;
            case R.id.rb_branch:
                mode = Static.METHOD_BRANCH;
                break;
            default:
                throw new RuntimeException("Unknown search mode");
        }

        EditText kwEt = view.findViewById(R.id.et_keyword);
        ModelLoanSearch search = new ModelLoanSearch(kwEt.getText().toString(), mode);
        new NetworkUtil<ModelLoanSearchResponse>().doRequestWithSession(Static.LOAN_SEARCH, search, FragmentLoanList.this, ModelLoanSearchResponse.class);
    }

    @Override
    public void onLoanItemClick(Loan loan, int position) {
        NavHostFragment.findNavController(this).navigate(NavigationBarFragmentDirections.actionNaviBarFragmentToNaviLoanInfoModifier(loan, false));
    }

    @Override
    public void onResponse(ModelLoanSearchResponse model) {
        loans.clear();
        loans.addAll(model.data);
        FragmentLoanList.this.getActivity().runOnUiThread(() -> {
            mRefresh.setRefreshing(false);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onNoLogin() {
        getActivity().runOnUiThread(() -> {
            mRefresh.setRefreshing(false);
            NavHostFragment.findNavController(FragmentLoanList.this).navigate(NavigationBarFragmentDirections.naviBarToLoginFragment());
        });
    }

    @Override
    public void onFailure(Exception e) {
        FragmentLoanList.this.getActivity().runOnUiThread(() -> mRefresh.setRefreshing(false));
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
    }
}
