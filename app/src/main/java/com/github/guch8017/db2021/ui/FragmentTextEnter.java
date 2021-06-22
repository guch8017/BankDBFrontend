package com.github.guch8017.db2021.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.guch8017.db2021.R;
import com.github.guch8017.db2021.view_model.GlobalShareVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentTextEnter extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTextEnterArgs args = FragmentTextEnterArgs.fromBundle(getArguments());
        View v = inflater.inflate(R.layout.fragment_text_input, container, false);
        TextView tagView = v.findViewById(R.id.input_tag);
        tagView.setText(args.getTag());
        final EditText et = v.findViewById(R.id.input_text);
        et.setText(args.getDefaultValue());
        FloatingActionButton fab = v.findViewById(R.id.input_confirm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalShareVM vm = new ViewModelProvider(getActivity()).get(GlobalShareVM.class);
                vm.setString(et.getText().toString());
                vm.setChanges(true);
                NavHostFragment.findNavController(FragmentTextEnter.this).navigateUp();
            }
        });
        return v;
    }
}
