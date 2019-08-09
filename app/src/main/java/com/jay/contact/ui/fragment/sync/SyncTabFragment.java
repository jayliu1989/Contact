package com.jay.contact.ui.fragment.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.jay.contact.R;
import com.jay.contact.base.BaseMainFragment;


public class SyncTabFragment extends BaseMainFragment {
    private Toolbar mToolbar;

    public static SyncTabFragment newInstance() {

        Bundle args = new Bundle();

        SyncTabFragment fragment = new SyncTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_sync, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);

        mToolbar.setTitle(R.string.title_sync);
    }


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }
}
