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
import com.github.guch8017.db2021.data.Loan;
import com.github.guch8017.db2021.data.ModelBranchSearchResponse;
import com.github.guch8017.db2021.data.ModelCustomerSearchResponse;
import com.github.guch8017.db2021.view_model.GlobalShareVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BranchSelector extends Fragment implements NetworkUtil.INetworkCallback<ModelBranchSearchResponse> {
    @Override
    public void onResponse(ModelBranchSearchResponse model) {
        String branch;
        if(from == 0) {
            Account acc = mVM.getAccount();
            if (acc == null) {
                Toast.makeText(getContext(), "错误：无法获取存储实例", Toast.LENGTH_LONG).show();
                return;
            }
            branch = acc.sub_branch;
        }else{
            Loan loan = mVM.getLoan();
            if (loan == null) {
                Toast.makeText(getContext(), "错误：无法获取存储实例", Toast.LENGTH_LONG).show();
                return;
            }
            branch = loan.branch;
        }
        if(branch != null) {
            for (int i = 0; i < model.data.size(); ++i) {
                if (branch.equals(model.data.get(i).name)) {
                    target = i;
                }
            }
        }
        getActivity().runOnUiThread(() -> {
            mBeans.clear();
            mBeans.addAll(UnionBean.getBeans(model.data, target));
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
        SelectorBean bean;
        Branch branch;
        public UnionBean(Branch branch){
            this.branch = branch;
            this.bean = new SelectorBean();
        }

        public UnionBean(Branch branch, boolean selected){
            this.branch = branch;
            this.bean = new SelectorBean();
            this.bean.setSelected(selected);
        }

        public static List<UnionBean> getBeans(List<Branch> branchList, int pos){
            ArrayList<UnionBean> ret = new ArrayList<>();
            for (int i = 0; i < branchList.size(); ++i) {
                if(i == pos)
                    ret.add(new UnionBean(branchList.get(i), true));
                else
                    ret.add(new UnionBean(branchList.get(i)));
            }
            return ret;
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        private final List<UnionBean> mList;
        private final LayoutInflater mInflater;

        public Adapter(Context c, List<UnionBean> beans){
            super();
            mInflater = LayoutInflater.from(c);
            mList = beans;
        }


        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.item_selector, parent, false);
            return new Adapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.d("Selector", "OnBind pos=" + position);
            holder.bind(mList.get(position));
            holder.itemView.setOnClickListener(v -> {
                if(target == position){
                    return;
                }
                if(target >= 0 && target < mList.size()){
                    mList.get(target).bean.inverseSelected();
                    notifyItemChanged(target);
                }
                mList.get(position).bean.inverseSelected();
                notifyItemChanged(position);
                target = position;
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private final TextView text;
            private final ImageView selectedIc;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.selector_text);
                selectedIc = itemView.findViewById(R.id.selector_selected);
            }

            public void bind(UnionBean bean) {
                text.setText(bean.branch.name);
                selectedIc.setVisibility((bean.bean.isSelected()) ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    private final ArrayList<UnionBean> mBeans = new ArrayList<>();
    private Adapter mAdapter;
    private int target = -1;
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
        from = BranchSelectorArgs.fromBundle(getArguments()).getType();
        View v = inflater.inflate(R.layout.fragment_multi_selector_list, container, false);
        final RecyclerView lv = v.findViewById(R.id.selector_list);
        mAdapter = new Adapter(getContext(), mBeans);
        lv.setAdapter(mAdapter);
        lv.setLayoutManager(new LinearLayoutManager(getContext()));
        final FloatingActionButton fab = v.findViewById(R.id.selector_save);
        fab.setOnClickListener(v1 -> {
            if (target < 0 || target > mBeans.size()){
                Toast.makeText(getContext(), "请选择一个支行", Toast.LENGTH_SHORT).show();
                return;
            }
            if(from == 0) {
                Account ac = mVM.getAccount();
                if (ac == null) {
                    Toast.makeText(getContext(), "无法保存：无法获取保存实例", Toast.LENGTH_LONG).show();
                    return;
                }
                ac.sub_branch = mBeans.get(target).branch.name;
                mVM.setAccount(ac);
            }else{
                Loan lo = mVM.getLoan();
                if (lo == null) {
                    Toast.makeText(getContext(), "无法保存：无法获取保存实例", Toast.LENGTH_LONG).show();
                    return;
                }
                lo.branch = mBeans.get(target).branch.name;
                mVM.setLoan(lo);
            }
            mVM.setChanges(true);
            NavHostFragment.findNavController(BranchSelector.this).navigateUp();
        });
        new NetworkUtil<ModelBranchSearchResponse>().doGetRequest(Static.BRANCH_GET, this, ModelBranchSearchResponse.class);
        return v;
    }


}