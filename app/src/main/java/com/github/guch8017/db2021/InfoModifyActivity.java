package com.github.guch8017.db2021;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.guch8017.db2021.data.Customer;

public class InfoModifyActivity extends AppCompatActivity {
    private ListView mList;
    private InfoModifyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mList = findViewById(R.id.info_list_view);
        mAdapter = new InfoModifyAdapter(new Customer("114514", "田所浩二", "1919810", "Japan", "野兽鲜卑", "123456", "test@test.com", "ftiend"), this);
        mList.setAdapter(mAdapter);
    }
}
