package com.github.guch8017.db2021.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.guch8017.db2021.NetworkUtil;
import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.Static;
import com.github.guch8017.db2021.data.ModelStat;
import com.github.guch8017.db2021.data.ModelStatResponse;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;


public class FragmentStatBranchDetail extends Fragment implements NetworkUtil.INetworkCallback<ModelStatResponse> {
    private LineChart mChart;
    private ListView mListView;
    private StatAdapter mAdapter;
    private ArrayList<ModelStatResponse.Stat1> mList;

    static class StatAdapter extends ArrayAdapter<ModelStatResponse.Stat1>{

        public StatAdapter(@NonNull Context context, int resource, @NonNull List<ModelStatResponse.Stat1> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ModelStatResponse.Stat1 item = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pay_history, parent, false);
            }
            TextView tv = convertView.findViewById(R.id.date);
            TextView cv = convertView.findViewById(R.id.count);
            tv.setText(item.branch);
            cv.setText(String.valueOf(item.fund));
            return convertView;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stat_detail, container, false);
        mListView = v.findViewById(R.id.stat_list_view);
        mAdapter = new StatAdapter(getContext(), R.layout.item_pay_history, mList);
        mListView.setAdapter(mAdapter);
        View dialogView = inflater.inflate(R.layout.dialog_data_picker, container, false);
        ((TextView)dialogView.findViewById(R.id.text_title)).setText("贷款总额统计");
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).setCancelable(false).create();
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(v1 -> {
            String start = ((TextView)dialogView.findViewById(R.id.et_start)).getText().toString();
            String end = ((TextView)dialogView.findViewById(R.id.et_end)).getText().toString();
            if(start == null || end == null || start.length() != 6 || end.length() != 6) {
                Toast.makeText(getContext(), "日期格式无效", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int y1 = Integer.parseInt(start.substring(0, 4));
                int m1 = Integer.parseInt(start.substring(5));
                int y2 = Integer.parseInt(end.substring(0, 4));
                int m2 = Integer.parseInt(end.substring(5));
                if(m1 < 0 || m1 > 12 || m2 < 0 || m2 > 12 || y1 < 2018 || y2 < 2018){
                    Toast.makeText(getContext(), "日期超出允许范围(201801~)", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (Exception e){
                Toast.makeText(getContext(), "日期格式无效", Toast.LENGTH_SHORT).show();
                return;
            }
            if(start.compareTo(end) > 0){
                Toast.makeText(getContext(), "终止日期早于起始日期", Toast.LENGTH_SHORT).show();
                return;
            }
            new NetworkUtil<ModelStatResponse>().doRequestWithSession(Static.STAT_GET_B, new ModelStat(start, end), FragmentStatBranchDetail.this, ModelStatResponse.class);
            dialog.dismiss();
        });
        dialog.show();
        return v;
    }

    @Override
    public void onResponse(ModelStatResponse model) {
        getActivity().runOnUiThread(() -> {
            mList.clear();
            mList.addAll(model.data);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onNoLogin() {
        // 服务端没写，PASS
    }

    @Override
    public void onFailure(Exception e) {
        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
    }
}
