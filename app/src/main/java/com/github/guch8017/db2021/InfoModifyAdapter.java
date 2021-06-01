package com.github.guch8017.db2021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.github.guch8017.db2021.data.Customer;

public class InfoModifyAdapter extends BaseAdapter {
    private Customer mCustomer;
    private final Context mContext;

    private static class Item{
        int tag;
        String data;

        Item(int tag, String data){
            this.data = data;
            this.tag = tag;
        }
    }


    public InfoModifyAdapter(@NonNull Customer customer, @NonNull Context context) {
        mContext = context;
        mCustomer = customer;
    }

    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public Item getItem(int position) {
        switch (position) {
            case 0:
                return new Item(R.string.info_id, mCustomer.id);
            case 1:
                return new Item(R.string.info_name, mCustomer.name);
            case 2:
                return new Item(R.string.info_phone, mCustomer.phone);
            case 3:
                return new Item(R.string.info_address, mCustomer.address);
            case 4:
                return new Item(R.string.info_s_name, mCustomer.s_name);
            case 5:
                return new Item(R.string.info_s_phone, mCustomer.s_phone);
            case 6:
                return new Item(R.string.info_s_email, mCustomer.s_email);
            case 7:
                return new Item(R.string.info_s_relation, mCustomer.s_relation);
            default:
                return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_info_list, null);
        }
        Item item = getItem(position);
        ((TextView)convertView.findViewById(R.id.t_tag)).setText(item.tag);
        ((TextView)convertView.findViewById(R.id.t_data)).setText(item.data);
        return convertView;
    }

    public void update(Customer customer){
        mCustomer = customer;
        notifyDataSetChanged();
    }
}
