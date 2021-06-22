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

import java.util.ArrayList;
import java.util.List;

public class AccountListAdapter<T> extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {
    private ArrayList<T> mList = new ArrayList<>();
    private final AccountViewHolder.IOnItemClickListener mCallback;

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "Create view holder");
        return AccountViewHolder.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Log.d("Adapter", "Bind view holder");
        holder.bind(mList.get(position), position, mCallback);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_account_list;
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

    public AccountListAdapter(AccountViewHolder.IOnItemClickListener callback){
        super();
        this.mCallback = callback;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder{
        private final ViewDataBinding mBinding;

        public AccountViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public static AccountViewHolder create(ViewGroup parent, int viewType){
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
            return new AccountViewHolder(binding);
        }

        public void bind(Object data, int position, IOnItemClickListener callback){

            mBinding.setVariable(BR.accountInfo, data);
            mBinding.setVariable(BR.onClickListener, callback);
            mBinding.executePendingBindings();
        }

        public interface IOnItemClickListener{
            void onAccountItemClick(Account account);
        }
    }
}
