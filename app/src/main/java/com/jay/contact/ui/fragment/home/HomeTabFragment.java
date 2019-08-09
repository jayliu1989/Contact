package com.jay.contact.ui.fragment.home;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.jay.contact.R;
import com.jay.contact.base.BaseMainFragment;
import com.jay.contact.event.AddContactEvent;
import com.jay.contact.event.ProgressValue;
import com.jay.contact.event.TabSelectedEvent;
import com.jay.contact.ui.fragment.MainFragment;
import com.jay.contact.utils.task.CommonTask;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class HomeTabFragment extends BaseMainFragment {
    @BindView(R.id.etContactCount)
    TextInputEditText etContactCount;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;

    View view;
    Unbinder unbinder;
    private ProgressDialog progressDialog;
    private ProgressValue progressValue;


    private int contactCount;


    public static HomeTabFragment newInstance() {

        Bundle args = new Bundle();

        HomeTabFragment fragment = new HomeTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        initView(view);
    }

    private void initView(View view) {
        EventBusActivityScope.getDefault(_mActivity).register(this);

        toolbar.setTitle(R.string.home);

        progressDialog = new ProgressDialog(getActivity(),ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);


    }

    /**
     * Reselected Tab
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        if (event.position != MainFragment.FIRST) return;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);

        if (unbinder != null) {
            unbinder.unbind();
        }

        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.btnStart)
    public void onViewClicked() {
        contactCount = 100;
        try {
            contactCount = Integer.valueOf(etContactCount.getText().toString().trim());
        }catch (Exception e){
            contactCount = 100;
        }
        startTask();
    }

    private void startTask() {
        CommonTask task = new CommonTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMax(contactCount);
                progressDialog.show();
            }

            @Override
            protected Object doInBackground(Object... objects) {

                generateContact();

                return true;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }

            @Override
            protected void onCancelled(Object o) {
                super.onCancelled(o);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                avi.hide();
            }
        };

        task.execute();
    }

    private void generateContact() {
        progressValue = new ProgressValue();

        ContentValues values = new ContentValues();
        StringBuilder sbName = new StringBuilder();
        StringBuilder sbPhoneNumber = new StringBuilder();
        for (int i = 0; i < contactCount; i++) {
            sbName.setLength(0);
            sbPhoneNumber.setLength(0);
            sbName.append("name");
            sbName.append(i);
            sbPhoneNumber.append("861335395");
            sbPhoneNumber.append(i);

            /*
             * 向RawContacts.CONTENT_URI空值插入，
             * 先获取Android系统返回的rawContactId
             * 后面要基于此id插入值
             */
            Uri rawContactUri = getActivity().getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);
            values.clear();

            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            // 内容类型
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            // 联系人名字
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, sbName.toString());
            // 向联系人URI添加联系人名字
            getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            values.clear();

            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            // 联系人的电话号码
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, sbPhoneNumber.toString());
            // 电话类型
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            // 向联系人电话号码URI添加电话号码
            getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            values.clear();

            progressValue.setProgress(i);
            EventBus.getDefault().post(progressValue);
        }

        EventBus.getDefault().post(new AddContactEvent());

    }

    /* EventBus start */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventThread(AddContactEvent contactEvent) {
        Toast.makeText(getActivity(), "Add Contact List Successful!", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventThread(ProgressValue progressValue) {
        progressDialog.setProgress(progressValue.getProgress());
    }
    /* EventBus end */

}
