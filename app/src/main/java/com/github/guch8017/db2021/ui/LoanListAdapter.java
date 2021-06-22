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
import com.github.guch8017.db2021.data.Loan;

import java.util.ArrayList;
import java.util.List;

public class LoanListAdapter<T> extends RecyclerView.Adapter<LoanListAdapter.LoanViewHolder> {
    private ArrayList<T> mList = new ArrayList<>();
    private final LoanViewHolder.IOnItemClickListener mCallback;

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "Create view holder");
        return LoanViewHolder.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        Log.d("Adapter", "Bind view holder");
        holder.bind(mList.get(position), position, mCallback);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_loan_list;
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

    public LoanListAdapter(LoanViewHolder.IOnItemClickListener callback){
        super();
        this.mCallback = callback;
    }

    public static class LoanViewHolder extends RecyclerView.ViewHolder{
        private final ViewDataBinding mBinding;

        public LoanViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public static LoanViewHolder create(ViewGroup parent, int viewType){
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
            return new LoanViewHolder(binding);
        }

        public void bind(Object data, int position, IOnItemClickListener callback){
            mBinding.setVariable(BR.position, position);
            mBinding.setVariable(BR.loanInfo, data);
            mBinding.setVariable(BR.onClickListener, callback);
            mBinding.executePendingBindings();
        }

        public interface IOnItemClickListener{
            void onLoanItemClick(Loan loan, int position);
        }
    }
}
