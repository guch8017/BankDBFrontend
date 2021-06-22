package com.github.guch8017.db2021.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Account;
import com.github.guch8017.db2021.data.Branch;
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.data.Loan;
import com.github.guch8017.db2021.data.ModelCustomerSearchResponse;
import com.github.guch8017.db2021.view_model.GlobalShareVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class CustomerSelector extends Fragment implements NetworkUtil.INetworkCallback<ModelCustomerSearchResponse> {
    @Override
    public void onResponse(ModelCustomerSearchResponse model) {
        getActivity().runOnUiThread(() -> {
            List<String> holder;
            if(from == 0){
                Account acc = mVM.getAccount();
                if(acc == null){
                    Toast.makeText(getContext(), "错误：无法获取存储实例", Toast.LENGTH_LONG).show();
                    return;
                }
                holder = acc.holder;
            }else {
                Loan loan = mVM.getLoan();
                if (loan == null) {
                    Toast.makeText(getContext(), "错误：无法获取存储实例", Toast.LENGTH_LONG).show();
                    return;
                }
                holder = loan.customers;
            }
            mBeans.clear();
            mBeans.addAll(CustomerSelector.UnionBean.getBeans(model.data, holder));
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onNoLogin() {
        Toast.makeText(getContext(), "未登录", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Exception e) {
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
    }

    static class SelectorBean{
        boolean selected;

        public SelectorBean(){
            selected = false;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public void inverseSelected(){
            this.selected = !this.selected;
        }

        public boolean isSelected() {
            return selected;
        }
    }

    static class UnionBean{
        CustomerSelector.SelectorBean bean;
        Customer customer;
        public UnionBean(Customer customer){
            this.customer = customer;
            this.bean = new CustomerSelector.SelectorBean();
        }

        public UnionBean(Customer customer, boolean selected){
            this.customer = customer;
            this.bean = new CustomerSelector.SelectorBean();
            this.bean.setSelected(selected);
        }

        public static List<CustomerSelector.UnionBean> getBeans(List<Customer> customerList, List<String> selected){
            ArrayList<CustomerSelector.UnionBean> ret = new ArrayList<>();
            for(Customer c: customerList){
                if(selected.contains(c.identifier_id))
                    ret.add(new CustomerSelector.UnionBean(c, true));
                else
                    ret.add(new CustomerSelector.UnionBean(c));
            }
            return ret;
        }
    }

    static class Adapter extends RecyclerView.Adapter<CustomerSelector.Adapter.ViewHolder>{
        private final List<CustomerSelector.UnionBean> mList;
        private final LayoutInflater mInflater;

        public Adapter(Context c, List<CustomerSelector.UnionBean> beans){
            super();
            mInflater = LayoutInflater.from(c);
            mList = beans;
        }


        @NonNull
        @Override
        public CustomerSelector.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.item_selector, parent, false);
            return new CustomerSelector.Adapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomerSelector.Adapter.ViewHolder holder, int position) {
            Log.d("Selector", "OnBind pos=" + position);
            holder.bind(mList.get(position));
            holder.itemView.setOnClickListener(v -> {
                mList.get(position).bean.inverseSelected();
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder{
            private final TextView text;
            private final ImageView selectedIc;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.selector_text);
                selectedIc = itemView.findViewById(R.id.selector_selected);
            }

            public void bind(CustomerSelector.UnionBean bean) {
                text.setText(bean.customer.identifier_id);
                selectedIc.setVisibility((bean.bean.isSelected()) ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    private final ArrayList<CustomerSelector.UnionBean> mBeans = new ArrayList<>();
    private CustomerSelector.Adapter mAdapter;
    private GlobalShareVM mVM;
    private int from;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVM = new ViewModelProvider(getActivity()).get(GlobalShareVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        from = CustomerSelectorArgs.fromBundle(getArguments()).getType();
        View v = inflater.inflate(R.layout.fragment_multi_selector_list, container, false);
        final RecyclerView lv = v.findViewById(R.id.selector_list);
        mAdapter = new CustomerSelector.Adapter(getContext(), mBeans);
        lv.setAdapter(mAdapter);
        lv.setLayoutManager(new LinearLayoutManager(getContext()));
        final FloatingActionButton fab = v.findViewById(R.id.selector_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> target = new ArrayList<>();
                for (CustomerSelector.UnionBean b: mBeans){
                    if(b.bean.isSelected()){
                        target.add(b.customer.identifier_id);
                    }
                }
                if(target.size() == 0){
                    Toast.makeText(getContext(), "必须绑定一个以上的用户", Toast.LENGTH_LONG).show();
                    return;
                }
                if(from == 0) {
                    Account ac = mVM.getAccount();
                    if (ac == null) {
                        Toast.makeText(getContext(), "无法保存：无法获取保存实例", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ac.holder = target;
                    mVM.setAccount(ac);
                } else {
                    Loan lo = mVM.getLoan();
                    if (lo == null) {
                        Toast.makeText(getContext(), "无法保存：无法获取保存实例", Toast.LENGTH_LONG).show();
                        return;
                    }
                    lo.customers = target;
                    mVM.setLoan(lo);
                }
                mVM.setChanges(true);
                NavHostFragment.findNavController(CustomerSelector.this).navigateUp();
            }
        });
        new NetworkUtil<ModelCustomerSearchResponse>().doGetRequest(Static.CUSTOMER_GET, this, ModelCustomerSearchResponse.class);
        return v;
    }


}