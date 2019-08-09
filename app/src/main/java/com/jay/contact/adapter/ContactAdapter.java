package com.jay.contact.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jay.contact.R;
import com.jay.contact.entity.ContactModel;

import java.util.List;

public class ContactAdapter extends BaseQuickAdapter<ContactModel, BaseViewHolder> {

    public ContactAdapter(int layoutResId, @Nullable List<ContactModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ContactModel item) {
        helper.setText(R.id.tvID, String.valueOf(item.getId()));
        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvPhoneNo, item.getPhoneNum());
    }
}
