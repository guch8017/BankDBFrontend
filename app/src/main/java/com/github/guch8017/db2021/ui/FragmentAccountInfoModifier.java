package com.github.guch8017.db2021.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Account;
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.data.ModelAccount;
import com.github.guch8017.db2021.data.ModelAccountDelete;
import com.github.guch8017.db2021.data.ModelAccountDeleteResponse;
import com.github.guch8017.db2021.data.ModelAccountResponse;
import com.github.guch8017.db2021.view_model.GlobalShareVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FragmentAccountInfoModifier extends Fragment implements InfoModifyAdapter.IGetItem<Account>, InfoModifyAdapter.ItemViewHolder.IOnItemClickListener, NetworkUtil.INetworkCallback<ModelAccountResponse>{
    final static String TAG = "FragmentAccountInfoModifier";
    private Account mData;
    private boolean fromTextEdit = false;
    private int lastPosition;
    private InfoModifyAdapter<Account> mAdapter;
    private boolean isNewAccount;
    private TextView title;
    private FloatingActionButton deleteFab;
    private boolean deleteBtnPressed = false;
    private Map<Integer, String> mapper = new HashMap<Integer, String>(){{
        put(0, "储蓄账户");
        put(1, "支票账户");
    }};
    private GlobalShareVM mVM;


    class ResponseHandler implements NetworkUtil.INetworkCallback<ModelAccountDeleteResponse>{

        @Override
        public void onResponse(ModelAccountDeleteResponse model) {
            Toast.makeText(getContext(), "账户删除成功，请刷新账户列表", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(() -> NavHostFragment.findNavController(FragmentAccountInfoModifier.this).navigateUp());
        }

        @Override
        public void onNoLogin() {
            getActivity().runOnUiThread(() -> {
                NavController ctl = NavHostFragment.findNavController(FragmentAccountInfoModifier.this);
                ctl.navigate(FragmentAccountInfoModifierDirections.actionNaviAccountInfoModifierToNaviUserLogin());
            });
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getContext(), "操作失败: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        mVM = new ViewModelProvider(getActivity()).get(GlobalShareVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView Called");
        FragmentAccountInfoModifierArgs args = FragmentAccountInfoModifierArgs.fromBundle(getArguments());
        mData = args.getAccount();
        isNewAccount = args.getIsCreate();
        if(mVM.hasUnsavedChanges()){
            if(fromTextEdit){
                fromTextEdit = false;
                try {
                    switch (lastPosition){
                        case 6:
                            if (mData.type == Static.ACCOUNT_TYPE_SAVING){
                                mData.finance = mVM.getString();
                            } else {
                                mData.overdraft = Double.parseDouble(mVM.getString());
                            }
                            break;
                        case 7:
                            mData.rate = Double.parseDouble(mVM.getString());
                            break;
                        default:
                            break;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "输入的数字格式有误", Toast.LENGTH_SHORT).show();
                }

            }else {
                Account newAcc = mVM.getAccount();
                if (newAcc != null) {
                    mData = newAcc;
                }
            }
        }
        mAdapter = new InfoModifyAdapter<>(mData, this, this);
        View v = inflater.inflate(R.layout.fragment_info_modifier, container, false);
        RecyclerView recView = v.findViewById(R.id.info_modifier_recycler);
        recView.setAdapter(mAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        (title = v.findViewById(R.id.hint)).setText(isNewAccount ? R.string.title_create_account : R.string.title_modify_account);
        v.findViewById(R.id.input_confirm).setOnClickListener(v1 -> {
            if(mData.type == Static.ACCOUNT_TYPE_SAVING && mData.finance == null) {
                Toast.makeText(getContext(), "请填写 货币类型 字段", Toast.LENGTH_SHORT).show();
                return;
            }
            if(mData.holder.size() == 0) {
                Toast.makeText(getContext(), "请填写 持卡人 字段", Toast.LENGTH_SHORT).show();
                return;
            }
            if(mData.sub_branch == null || mData.sub_branch.equals("")){
                Toast.makeText(getContext(), "请填写 开户支行 字段", Toast.LENGTH_SHORT).show();
                return;
            }
            new NetworkUtil<ModelAccountResponse>().doRequestWithSession(
                    isNewAccount ? Static.ACCOUNT_CREATE : Static.ACCOUNT_MODIFY,
                    new ModelAccount(mData),
                    FragmentAccountInfoModifier.this,
                    ModelAccountResponse.class
            );
        });
        (deleteFab = v.findViewById(R.id.input_delete)).setOnClickListener(v12 -> new NetworkUtil<ModelAccountDeleteResponse>().doRequestWithSession(Static.ACCOUNT_DELETE, new ModelAccountDelete(mData.account_id), new ResponseHandler(), ModelAccountDeleteResponse.class));
        if(isNewAccount){
            deleteFab.setEnabled(false);
            deleteFab.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated Called");
    }

    @Override
    public InfoItem getItem(int position, Account model) {
        switch (position) {
            case 0:
                return new InfoItem(R.string.info_acc_id, model.account_id, false);
            case 1:
                return new InfoItem(R.string.info_acc_type, mapper.getOrDefault(model.type, "未知类型账户"), isNewAccount);
            case 2:
                return new InfoItem(R.string.info_branch, model.sub_branch, isNewAccount);
            case 3:
                return new InfoItem(R.string.info_open_date, model.open_date, false);
            case 4:
                return new InfoItem(R.string.info_recent_date, model.recent_visit, false);
            case 5:
                return new InfoItem(R.string.info_holder_list, model.holder.size() + " 人");
            case 6:
                if(model.type == 0){
                    return new InfoItem(R.string.info_finance, model.finance);
                }else {
                    return new InfoItem(R.string.info_overdraft, String.valueOf(model.overdraft));
                }
            case 7:
                return new InfoItem(R.string.info_rate, String.valueOf(model.rate));
            default:
                throw new IndexOutOfBoundsException();

        }
    }

    @Override
    public int getItemCount(Account model) {
        if(model.type == 0){
            return 8;
        } else {
            return 7;
        }
    }

    @Override
    public void onItemClick(int position, String originValue, @IdRes int tag) {
        Log.i(TAG, "OnItemClicked position=" + position);
        if(!getItem(position, mData).editable){
            return;
        }
        NavController ctl = NavHostFragment.findNavController(this);
        fromTextEdit = false;
        lastPosition = position;
        mVM.setChanges(false);
        mVM.setAccount(mData);
        switch (position){
            case 1:
                ctl.navigate(FragmentAccountInfoModifierDirections.accountInfoModifierToAccTypeSelector());
                break;
            case 2:
                ctl.navigate(FragmentAccountInfoModifierDirections.accountInfoModifierToBranchSelector(0));
                break;
            case 5:
                ctl.navigate(FragmentAccountInfoModifierDirections.accountInfoModifierToCustomerSelector(0));
                break;
            default:
                fromTextEdit = true;
                ctl.navigate(FragmentAccountInfoModifierDirections.accountModifierToTextEnter(tag, originValue != null ? originValue : ""));
        }

    }

    @Override
    public void onResponse(ModelAccountResponse model) {
        mData.reset(model.data);
        isNewAccount = false;
        getActivity().runOnUiThread(() -> {
            mAdapter.update(model.data);
            title.setText(R.string.title_modify_account);
            deleteFab.setEnabled(true);
            deleteFab.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onNoLogin() {
        getActivity().runOnUiThread(() -> {
            NavController ctl = NavHostFragment.findNavController(FragmentAccountInfoModifier.this);
            ctl.navigate(FragmentAccountInfoModifierDirections.actionNaviAccountInfoModifierToNaviUserLogin());
        });
    }

    @Override
    public void onFailure(Exception e) {
        Toast.makeText(getContext(), "操作失败: " + e.toString(), Toast.LENGTH_LONG).show();
    }
}
