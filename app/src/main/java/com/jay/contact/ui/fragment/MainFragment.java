package com.jay.contact.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.jay.contact.R;
import com.jay.contact.event.TabSelectedEvent;
import com.jay.contact.ui.fragment.home.HomeTabFragment;
import com.jay.contact.ui.fragment.contact.ContactTabFragment;
import com.jay.contact.ui.fragment.sync.SyncTabFragment;
import com.jay.contact.ui.view.BottomBar;
import com.jay.contact.ui.view.BottomBarTab;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.fragmentation.SupportFragment;


public class MainFragment extends SupportFragment {
    private static final int REQ_MSG = 1;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    private SupportFragment[] mFragments = new SupportFragment[3];

    private BottomBar mBottomBar;


    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(HomeTabFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = HomeTabFragment.newInstance();
            mFragments[SECOND] = ContactTabFragment.newInstance();
            mFragments[THIRD] = SyncTabFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
        } else {
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(ContactTabFragment.class);
            mFragments[THIRD] = findChildFragment(SyncTabFragment.class);
        }
    }

    private void initView(View view) {
        mBottomBar = view.findViewById(R.id.bottomBar);

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_home_black_24dp, getString(R.string.title_home)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_contact_black_24dp, getString(R.string.title_contact)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_sync_black_24dp, getString(R.string.title_sync)));

        // 模拟未读消息
        mBottomBar.getItem(FIRST).setUnreadCount(0);

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);

                BottomBarTab tab = mBottomBar.getItem(FIRST);
                if (position == FIRST) {
                    tab.setUnreadCount(0);
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MSG && resultCode == RESULT_OK) {

        }
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }
}
