package com.jay.contact.ui.fragment.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jay.contact.R;
import com.jay.contact.base.BaseMainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SyncTabFragment extends BaseMainFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    View view;
    Unbinder unbinder;

    public static SyncTabFragment newInstance() {

        Bundle args = new Bundle();

        SyncTabFragment fragment = new SyncTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_sync, container, false);

        unbinder = ButterKnife.bind(this,view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvTitle.setText(R.string.title_sync);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null){
            unbinder.unbind();
        }
    }
}
