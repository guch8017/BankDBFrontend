package com.github.guch8017.db2021.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.data.Account;
import com.github.guch8017.db2021.data.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerListAdapter<T> extends RecyclerView.Adapter<CustomerListAdapter.CustomerViewHolder> {
    private ArrayList<T> mList = new ArrayList<>();
    private final CustomerViewHolder.IOnItemClickListener mCallback;

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CustomerViewHolder.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        holder.bind(mList.get(position), position, mCallback);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_customer_list;
    }

    public void setList(List<T> newList){
        mList.clear();
        mList.addAll(newList);
        notifyDataSetChanged();
    }

    public void resetList(ArrayList<T> newList){
        mList = newList;
        Log.i("Adapter", "List reset");
        notifyDataSetChanged();
    }

    public CustomerListAdapter(CustomerViewHolder.IOnItemClickListener callback){
        super();
        this.mCallback = callback;
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder{
        private final ViewDataBinding mBinding;

        public CustomerViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public static CustomerViewHolder create(ViewGroup parent, int viewType){
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
            return new CustomerViewHolder(binding);
        }

        public void bind(Object data, int position, IOnItemClickListener callback){
            mBinding.setVariable(BR.customer, data);
            mBinding.setVariable(BR.position, position);
            mBinding.setVariable(BR.onClickListener, callback);
            mBinding.executePendingBindings();
        }

        public interface IOnItemClickListener{
            void onCustomerItemClick(Customer account, int position);
        }
    }
}
