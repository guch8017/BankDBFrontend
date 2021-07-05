package com.github.guch8017.db2021.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guch8017.db2021.NavigationBarFragmentDirections;
import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Customer;
import com.github.guch8017.db2021.data.ModelCustomer;
import com.github.guch8017.db2021.data.ModelCustomerDelete;
import com.github.guch8017.db2021.data.ModelCustomerDeleteResponse;
import com.github.guch8017.db2021.data.ModelCustomerResponse;
import com.github.guch8017.db2021.view_model.GlobalShareVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentCustomerInfoModifier extends Fragment implements InfoModifyAdapter.IGetItem<Customer>, InfoModifyAdapter.ItemViewHolder.IOnItemClickListener {
    private InfoModifyAdapter<Customer> mAdapter;
    private Customer mData;
    private int lastPosition;
    private GlobalShareVM mVM;
    private boolean isCreateCustomer;
    private FloatingActionButton fabCreate;
    private FloatingActionButton fabDelete;
    private TextView hintText;

    class UpdateHandler implements NetworkUtil.INetworkCallback<ModelCustomerResponse>{

        @Override
        public void onResponse(ModelCustomerResponse model) {
            isCreateCustomer = false;
            getActivity().runOnUiThread(() -> {
                fabDelete.setVisibility(View.VISIBLE);
                fabDelete.setEnabled(true);
                hintText.setText(R.string.title_modify_customer);
                mAdapter.update(model.data);
            });
            Toast.makeText(getContext(), "数据上传成功", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onNoLogin() {
            getActivity().runOnUiThread(() -> {
                NavHostFragment.findNavController(FragmentCustomerInfoModifier.this).navigate(FragmentCustomerInfoModifierDirections.actionNaviCustomerInfoModifierToNaviUserLogin());
            });
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    class DeleteHandler implements NetworkUtil.INetworkCallback<ModelCustomerDeleteResponse> {

        @Override
        public void onResponse(ModelCustomerDeleteResponse model) {
            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(() -> NavHostFragment.findNavController(FragmentCustomerInfoModifier.this).navigateUp());
        }

        @Override
        public void onNoLogin() {
            getActivity().runOnUiThread(() -> {
                NavHostFragment.findNavController(FragmentCustomerInfoModifier.this).navigate(FragmentCustomerInfoModifierDirections.actionNaviCustomerInfoModifierToNaviUserLogin());
            });
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVM = new ViewModelProvider(getActivity()).get(GlobalShareVM.class);
        FragmentCustomerInfoModifierArgs args = FragmentCustomerInfoModifierArgs.fromBundle(getArguments());
        mData = args.getCustomer();
        isCreateCustomer = args.getIsCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mVM.hasUnsavedChanges()){
            String s = mVM.getString();
            switch (lastPosition){
                case 0:
                    mData.identifier_id = s;
                    break;
                case 1:
                    mData.name = s;
                    break;
                case 2:
                    mData.phone = s;
                    break;
                case 3:
                    mData.address = s;
                    break;
                case 4:
                    mData.s_name = s;
                    break;
                case 5:
                    mData.s_phone = s;
                    break;
                case 6:
                    mData.s_email = s;
                    break;
                case 7:
                    mData.s_rel = s;
                    break;
                default:
                    break;
            }
        }
        mAdapter = new InfoModifyAdapter<>(mData, this, this);
        View v = inflater.inflate(R.layout.fragment_info_modifier, container, false);
        hintText = v.findViewById(R.id.hint);
        hintText.setText(isCreateCustomer ? R.string.title_create_customer : R.string.title_modify_customer);
        fabCreate = v.findViewById(R.id.input_confirm);
        fabDelete = v.findViewById(R.id.input_delete);
        if(isCreateCustomer){
            fabDelete.setVisibility(View.INVISIBLE);
            fabDelete.setEnabled(false);
        }else{
            fabDelete.setVisibility(View.VISIBLE);
            fabDelete.setEnabled(true);
        }
        fabCreate.setOnClickListener(v1 -> {
            if(mData.identifier_id == null || mData.s_rel == null || mData.s_email == null || mData.s_phone == null || mData.s_name == null || mData.address == null || mData.phone == null || mData.name == null){
                Toast.makeText(getContext(), "必要信息未填写完全", Toast.LENGTH_SHORT).show();
                return;
            }
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(mData.s_email);
            if(!matcher.matches()){
                Toast.makeText(getContext(), "邮箱格式异常", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = isCreateCustomer ? Static.CUSTOMER_CREATE : Static.CUSTOMER_MODIFY;
            new NetworkUtil<ModelCustomerResponse>().doRequestWithSession(url, new ModelCustomer(mData), new UpdateHandler(), ModelCustomerResponse.class);
        });
        fabDelete.setOnClickListener(v2 -> new NetworkUtil<ModelCustomerDeleteResponse>().doRequestWithSession(Static.CUSTOMER_DELETE, new ModelCustomerDelete(mData.identifier_id), new DeleteHandler(), ModelCustomerDeleteResponse.class));
        RecyclerView recView = v.findViewById(R.id.info_modifier_recycler);
        recView.setAdapter(mAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }


    @Override
    public InfoItem getItem(int position, Customer model) {
        switch (position) {
            case 0:
                return new InfoItem(R.string.info_id, model.identifier_id, isCreateCustomer);
            case 1:
                return new InfoItem(R.string.info_name, model.name);
            case 2:
                return new InfoItem(R.string.info_phone, model.phone);
            case 3:
                return new InfoItem(R.string.info_address, model.address);
            case 4:
                return new InfoItem(R.string.info_s_name, model.s_name);
            case 5:
                return new InfoItem(R.string.info_s_phone, model.s_phone);
            case 6:
                return new InfoItem(R.string.info_s_email, model.s_email);
            case 7:
                return new InfoItem(R.string.info_s_relation, model.s_rel);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount(Customer model) {
        return 8;
    }


    @Override
    public void onItemClick(int position, String originValue, int tag) {
        if(!getItem(position, mData).editable){
            return;
        }
        lastPosition = position;
        mVM.setChanges(false);
        NavHostFragment.findNavController(this).navigate(FragmentCustomerInfoModifierDirections.customerModifierToTextEnter(tag, originValue != null ? originValue : ""));
    }


}
