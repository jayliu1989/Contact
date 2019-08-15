package com.jay.contact.ui.fragment.home;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.jay.contact.R;
import com.jay.contact.base.BaseMainFragment;
import com.jay.contact.entity.ContactModel;
import com.jay.contact.event.AddContactEvent;
import com.jay.contact.event.ProgressValue;
import com.jay.contact.event.TabSelectedEvent;
import com.jay.contact.ui.fragment.MainFragment;
import com.jay.contact.utils.task.CommonTask;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class HomeTabFragment extends BaseMainFragment {
    @BindView(R.id.etContactCount)
    TextInputEditText etContactCount;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    View view;
    Unbinder unbinder;

    private ProgressDialog progressDialog;
    private ProgressValue progressValue;


    private int contactCount;
    private List<ContactModel> list = new ArrayList<>();


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

        tvTitle.setText(R.string.home);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();

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
        } catch (Exception e) {
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

        if (getActivity() != null  && getActivity().hasWindowFocus()){
            task.execute();
        }
    }

    private void generateContact() {
        progressValue = new ProgressValue();
        ContactModel contactModel;

        for (int i = 0; i < contactCount; i++) {
            contactModel = new ContactModel();
            contactModel.setName("name"+i);
            contactModel.setPhoneNum("861335395"+i);
            list.add(contactModel);


        }

        addContact(list);
//        batchAddContact(list);

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
        if (progressValue.getProgress() == contactCount){
            progressDialog.hide();
        }
    }
    /* EventBus end */

    public void addContact(List<ContactModel> list){
        ContentValues values = new ContentValues();
        for (int i = 0; i < list.size(); i++) {
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
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, list.get(i).getName());
            // 向联系人URI添加联系人名字
            getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            values.clear();

            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            // 联系人的电话号码
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).getPhoneNum());
            // 电话类型
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            // 向联系人电话号码URI添加电话号码
            getActivity().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            values.clear();

            progressValue.setProgress(i);
            EventBus.getDefault().post(progressValue);
        }
    }

    public void batchAddContact(List<ContactModel> list) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex;
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                            rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, list.get(i).getName())
                    .withYieldAllowed(true)
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, list.get(i).getPhoneNum())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .withYieldAllowed(true)
                    .build());

        }
        try {
            //这里才调用的批量添加
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        progressValue.setProgress(contactCount);
        EventBus.getDefault().post(progressValue);
    }

}
