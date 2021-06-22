package com.github.guch8017.db2021.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.Loan;
import com.github.guch8017.db2021.data.ModelAccount;
import com.github.guch8017.db2021.data.ModelAccountDelete;
import com.github.guch8017.db2021.data.ModelAccountDeleteResponse;
import com.github.guch8017.db2021.data.ModelAccountResponse;
import com.github.guch8017.db2021.data.ModelLoanCreate;
import com.github.guch8017.db2021.data.ModelLoanDelete;
import com.github.guch8017.db2021.data.ModelLoanDeleteResponse;
import com.github.guch8017.db2021.data.ModelLoanPay;
import com.github.guch8017.db2021.data.ModelLoanResponse;
import com.github.guch8017.db2021.view_model.GlobalShareVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FragmentLoanInfoModifier extends Fragment implements InfoModifyAdapter.IGetItem<Loan>, InfoModifyAdapter.ItemViewHolder.IOnItemClickListener, NetworkUtil.INetworkCallback<ModelLoanResponse>{
    final static String TAG = "FragmentAccountInfoModifier";
    private Loan mData;
    private boolean fromTextEdit = false;
    private int lastPosition;
    private InfoModifyAdapter<Loan> mAdapter;
    private boolean isNewLoan;
    private TextView title;
    private FloatingActionButton deleteFab;
    private FloatingActionButton addFab;
    private FloatingActionButton payFab;
    private boolean deleteBtnPressed = false;

    private GlobalShareVM mVM;


    class ResponseHandler implements NetworkUtil.INetworkCallback<ModelLoanDeleteResponse>{

        @Override
        public void onResponse(ModelLoanDeleteResponse model) {
            Toast.makeText(getContext(), "账户删除成功，请刷新账户列表", Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(() -> NavHostFragment.findNavController(FragmentLoanInfoModifier.this).navigateUp());
        }

        @Override
        public void onNoLogin() {
            getActivity().runOnUiThread(() -> {
                NavController ctl = NavHostFragment.findNavController(FragmentLoanInfoModifier.this);
                ctl.navigate(FragmentAccountInfoModifierDirections.actionNaviAccountInfoModifierToNaviUserLogin());
            });
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getContext(), "操作失败: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    static class Adapter extends ArrayAdapter<Loan.PayHistory> {
        LayoutInflater mInflater;

        public Adapter(@NonNull Context context, List<Loan.PayHistory> data) {
            super(context, 0, data);
            mInflater = LayoutInflater.from(context);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.item_pay_history, parent, false);
            }
            Loan.PayHistory item = getItem(position);
            ((TextView) convertView.findViewById(R.id.date)).setText(item.date);
            ((TextView) convertView.findViewById(R.id.count)).setText(String.valueOf(item.fund));
            return convertView;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVM = new ViewModelProvider(getActivity()).get(GlobalShareVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoanInfoModifierArgs args = FragmentLoanInfoModifierArgs.fromBundle(getArguments());
        mData = args.getLoan();
        isNewLoan = args.getIsCreate();
        if(mVM.hasUnsavedChanges()){
            if(fromTextEdit){
                if(mData != null){
                    try {
                        mData.total = Double.parseDouble(mVM.getString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "无法将输入解析为Double", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Loan newLoan = mVM.getLoan();
                if (newLoan != null) {
                    mData = newLoan;
                }
            }
        }

        mAdapter = new InfoModifyAdapter<>(mData, this, this);
        View v = inflater.inflate(R.layout.fragment_info_modifier, container, false);
        addFab = v.findViewById(R.id.input_confirm);
        deleteFab = v.findViewById(R.id.input_delete);
        payFab = v.findViewById(R.id.input_pay);
        if(isNewLoan){
            payFab.setVisibility(View.GONE);
            addFab.setVisibility(View.VISIBLE);
            deleteFab.setVisibility(View.GONE);
        } else {
            payFab.setVisibility(View.VISIBLE);
            addFab.setVisibility(View.INVISIBLE);
            addFab.setEnabled(false);
            deleteFab.setVisibility(View.VISIBLE);
        }
        RecyclerView recView = v.findViewById(R.id.info_modifier_recycler);
        recView.setAdapter(mAdapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
        (title = v.findViewById(R.id.hint)).setText(isNewLoan ? R.string.title_create_loan : R.string.title_view_loan);
        if(isNewLoan) {
            addFab.setOnClickListener(v1 -> {
                if (mData.customers.size() == 0) {
                    Toast.makeText(getContext(), "请选择至少一名贷款持有人", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mData.branch == null || mData.branch.equals("")) {
                    Toast.makeText(getContext(), "请填写 开户支行 字段", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mData.total <= 0) {
                    Toast.makeText(getContext(), "贷款总额度应大于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                new NetworkUtil<ModelLoanResponse>().doRequestWithSession(
                        Static.LOAN_CREATE,
                        new ModelLoanCreate(mData.customers, mData.total, mData.branch),
                        FragmentLoanInfoModifier.this,
                        ModelLoanResponse.class
                );
            });
        }else{
            addFab.setVisibility(View.INVISIBLE);
            addFab.setEnabled(false);
        }
        deleteFab.setOnClickListener(v12 -> new NetworkUtil<ModelLoanDeleteResponse>().doRequestWithSession(Static.LOAN_DELETE, new ModelLoanDelete(mData.loan_id), new ResponseHandler(), ModelLoanDeleteResponse.class));
        if(isNewLoan){
            deleteFab.setEnabled(false);
            deleteFab.setVisibility(View.INVISIBLE);
        }
        v.findViewById(R.id.input_pay).setOnClickListener(v13 -> doPay());
        return v;
    }


    @Override
    public InfoItem getItem(int position, Loan model) {
        switch (position) {
            case 0:
                return new InfoItem(R.string.info_loan_id, model.loan_id, false);
            case 1:
                return new InfoItem(R.string.info_loan_total, String.valueOf(model.total), isNewLoan);
            case 2:
                return new InfoItem(R.string.info_branch, model.branch, isNewLoan);
            case 3:
                return new InfoItem(R.string.info_create_date, model.create_date, false);
            case 4:
                return new InfoItem(R.string.info_loan_holder, model.customers.size() + "人", isNewLoan);
            case 5:
                return new InfoItem(R.string.info_pay_history, model.paid_history.size() + " 条记录");
            default:
                throw new IndexOutOfBoundsException();

        }
    }

    @Override
    public int getItemCount(Loan model) {
        return 6;
    }

    @Override
    public void onItemClick(int position, String originValue, @IdRes int tag) {
        NavController ctl = NavHostFragment.findNavController(this);
        fromTextEdit = false;
        lastPosition = position;
        mVM.setChanges(false);
        mVM.setLoan(mData);
        switch (position){
            case 2:
                ctl.navigate(FragmentLoanInfoModifierDirections.actionNaviLoanInfoModifierToNaviBranchSelector(1));
                break;
            case 4:
                ctl.navigate(FragmentLoanInfoModifierDirections.actionNaviLoanInfoModifierToNaviCustomerSelector(1));
                break;
            case 1:
                fromTextEdit = true;
                ctl.navigate(FragmentLoanInfoModifierDirections.actionNaviLoanInfoModifierToNaviTextInput(tag, originValue));
                break;
            case 5:
                if(!isNewLoan){
                    View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pay_history, null);
                    ListView lv = v.findViewById(R.id.history_list);
                    Adapter historyAdapter = new Adapter(getContext(), mData.paid_history);
                    lv.setAdapter(historyAdapter);
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(v).create();
                    v.findViewById(R.id.btn_ok).setOnClickListener(v1 -> dialog.dismiss());
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }

    public void doPay(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pay_loan, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        view.findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = view.findViewById(R.id.et_pay);
                ModelLoanPay model;
                try {
                    model = new ModelLoanPay(mData.loan_id, Double.parseDouble(et.getText().toString()));
                } catch (NumberFormatException e){
                    Toast.makeText(getContext(), "输入异常，无法解析为Double", Toast.LENGTH_SHORT).show();
                    return;
                }
                new NetworkUtil<ModelLoanResponse>().doRequestWithSession(Static.LOAN_PAY, model, FragmentLoanInfoModifier.this, ModelLoanResponse.class);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onResponse(ModelLoanResponse model) {
        mData = model.data;
        isNewLoan = false;
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), "成功", Toast.LENGTH_SHORT).show();
            mAdapter.update(model.data);
            title.setText(R.string.title_view_loan);
            deleteFab.setEnabled(true);
            deleteFab.setVisibility(View.VISIBLE);
            addFab.setEnabled(false);
            addFab.setVisibility(View.INVISIBLE);
            payFab.setEnabled(true);
            payFab.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onNoLogin() {
        getActivity().runOnUiThread(() -> {
            NavController ctl = NavHostFragment.findNavController(FragmentLoanInfoModifier.this);
            ctl.navigate(FragmentLoanInfoModifierDirections.actionNaviLoanInfoModifierToNaviUserLogin());
        });
    }

    @Override
    public void onFailure(Exception e) {
        Toast.makeText(getContext(), "操作失败: " + e.toString(), Toast.LENGTH_LONG).show();
    }
}
