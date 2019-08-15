package com.jay.contact.ui.fragment.contact;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jay.contact.R;
import com.jay.contact.adapter.ContactAdapter;
import com.jay.contact.base.BaseMainFragment;
import com.jay.contact.entity.ContactModel;
import com.jay.contact.event.QueryContactEvent;
import com.jay.contact.utils.task.CommonTask;
import com.wang.avi.AVLoadingIndicatorView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ContactTabFragment extends BaseMainFragment {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.iv_right)
    ImageView ivRight;

    View view;
    Unbinder unbinder;

    CommonTask task;


    private ContactAdapter contactAdapter;
    private List<ContactModel> list = new ArrayList<>();

    public static ContactTabFragment newInstance() {

        Bundle args = new Bundle();

        ContactTabFragment fragment = new ContactTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_contact, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        initView();
    }

    private void initView() {
        tvTitle.setText(R.string.title_contact);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.drawable.ic_delete_24dp);
        ivRight.setPadding(20, 20, 20, 20);

        contactAdapter = new ContactAdapter(R.layout.item_contact, list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(contactAdapter);
    }


    @OnClick(R.id.iv_right)
    public void onViewClicked() {
        new Thread(() -> {
            list.clear();
            Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 访问所有联系人
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
            Log.d("Contact", "count:" + cursor.getCount());
            while (cursor.moveToNext()) {
                int contactsId = cursor.getInt(0);
//                list.add(new ContactModel(contactsId));
//                Log.d("Contact", "contactsId:" + contactsId);
                deleteContact(String.valueOf(contactsId));
            }

            deleteContact(list);
        }).start();
//
//        new Thread(() -> {
//            deleteContact(list);
//        }).start();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
//        mViewPager.setAdapter(new WechatPagerFragmentAdapter(getChildFragmentManager()
//                , getString(R.string.all), getString(R.string.more)));
        if (Build.VERSION.SDK_INT >= 21) {
            checkPermission();
        } else {
            startTask();
        }

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();

    }

    private void checkPermission() {
        AndPermission
                .with(this)
                .runtime().
                permission(Permission.READ_CONTACTS, Permission.WRITE_CONTACTS).
                onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        startTask();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                }).start();

    }

    private void startTask() {
        if (task != null && !task.isCancelled()){
            task.cancel(true);
            task = null;
        }
        task = new CommonTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                avi.show();
            }

            @Override
            protected Object doInBackground(Object... objects) {
                loadContactList();
                return true;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                avi.hide();
                EventBus.getDefault().post(new QueryContactEvent());
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

    private void loadContactList() {
        ContactModel contactModel = null;
        StringBuffer phoneNum = new StringBuffer();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            contactModel = new ContactModel();
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFieldColumnIndex);
            contactModel.setName(contact);
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            contactModel.setId(Long.valueOf(contactId));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

            phoneNum.setLength(0);
            while (phone.moveToNext()) {
                int numberFieldColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = phone.getString(numberFieldColumnIndex);

                phoneNum.append(phoneNumber);
                phoneNum.append(";");
            }
            if (phoneNum.length() > 0) {
                phoneNum.deleteCharAt(phoneNum.length() - 1);
            }
            contactModel.setPhoneNum(phoneNum.toString());
            phone.close();

            list.add(contactModel);
        }
        cursor.close();

        EventBus.getDefault().post(new QueryContactEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }

        EventBus.getDefault().unregister(this);
    }

    /* EventBus start */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventThread(QueryContactEvent contactEvent) {
        Log.e("Contact", "load all");

        if (avi != null && avi.isShown()) {
            avi.hide();
        }
        if (!task.isCancelled())
            task.cancel(true);
        contactAdapter.notifyDataSetChanged();
    }

    /* EventBus end */
    //COLUMN_CONTACT_ID
    public void deleteContact(String id) {
        Log.d("Contact", "**delete start**");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        //delete contact
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=" + id, null)
                .build());
        //delete contact information such as phone number,email
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=" + id, null)
                .build());
        try {
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d("Contact", "delete contact success");
        } catch (Exception e) {
            Log.d("Contact", "delete contact failed");
            Log.d("Contact", e.getMessage());
        }
        Log.d("Contact", "**delete end**");
    }

    public void deleteContact(List<ContactModel> contactModelList) {
        Log.d("Contact", "**delete start**");
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        Log.d("Contact", "" + contactModelList.size());

        for (ContactModel contactModel : contactModelList) {
//            Log.d("Contact", "" + contactModel.getId());

            //delete contact
//            ops.add(ContentProviderOperation.newDelete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contactModel.getId()))
//                    .withYieldAllowed(true)
//                    .build());
            ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=" + contactModel.getId(), null)
                    .build());
//            delete contact information such as phone number,email
            ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=" + contactModel.getId(), null)
                    .build());
        }

        try {
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Log.d("Contact", "delete contact success");
        } catch (Exception e) {
            Log.d("Contact", "delete contact failed");
            Log.d("Contact", e.getMessage());
        }
        Log.d("Contact", "**delete end**");
        list.clear();
        startTask();
    }

}
