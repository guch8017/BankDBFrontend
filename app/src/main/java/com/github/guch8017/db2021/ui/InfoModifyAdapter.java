package com.github.guch8017.db2021.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guch8017.db2021.R;

public class InfoModifyAdapter<T> extends RecyclerView.Adapter<InfoModifyAdapter.ItemViewHolder> {
    private T mData;
    private ItemViewHolder.IOnItemClickListener mCallback;
    private IGetItem<T> mGetter;

    public InfoModifyAdapter(@NonNull T data, ItemViewHolder.IOnItemClickListener callback, IGetItem<T> itemGetter) {
        mData = data;
        mCallback = callback;
        mGetter = itemGetter;
    }



    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ItemViewHolder.create(parent, R.layout.item_info_list);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Log.d("Adapter", "Bind view holder");
        holder.bind(mGetter.getItem(position, mData), position, mCallback);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mGetter.getItemCount(mData);
    }

    public void update(T data){
        mData = data;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private final ViewDataBinding mBinding;
        private final ImageView ic;

        public ItemViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            ic = binding.getRoot().findViewById(R.id.next_ic);
        }

        public static ItemViewHolder create(ViewGroup parent, int viewType){
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
            return new ItemViewHolder(binding);
        }

        public void bind(Object data, int position, IOnItemClickListener callback){
            Log.i("Modifier", "Pos = " + position + " ET:" + ((InfoItem)data).editable);
            mBinding.setVariable(BR.position, position);
            mBinding.setVariable(BR.item, data);
            mBinding.setVariable(BR.onClickListener, callback);
            ic.setVisibility(((InfoItem)data).editable ? View.VISIBLE : View.INVISIBLE);
            mBinding.executePendingBindings();
        }

        public interface IOnItemClickListener{
            void onItemClick(int position, String originValue, @IdRes int tag);
        }
    }

    public interface IGetItem<T>{
        InfoItem getItem(int position, T model);
        int getItemCount(T model);
    }
}
