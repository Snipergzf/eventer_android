package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.entity.User;
import com.eventer.app.task.LoadImage;
import com.eventer.app.task.LoadImage.ImageDownloadedCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的好友Adapter实现
 *
 */
public class ContactAdapter extends ArrayAdapter<User> implements
        SectionIndexer {

    List<String> list;
    List<User> userList;
    List<User> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    public MyFilter myFilter;
    private LoadImage avatarLoader;

    @SuppressLint("SdCardPath")
    public ContactAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
        avatarLoader = new LoadImage(context, Constant.IMAGE_PATH);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
        }

        ImageView iv_avatar = (ImageView) convertView
                .findViewById(R.id.iv_avatar);

        TextView nameTextview = (TextView) convertView
                .findViewById(R.id.tv_name);
        TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
        View view_temp = convertView.findViewById(R.id.view_temp);
        User user = getItem(position);
        String header = user.getHeader();
        String usernick = user.getNick();
        String beizhu = user.getBeizhu();
        String useravatar = user.getAvatar();

        if (position == 0 || header != null
                && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                tvHeader.setVisibility(View.GONE);
                view_temp.setVisibility(View.VISIBLE);
            } else {
                tvHeader.setVisibility(View.VISIBLE);
                tvHeader.setText(header);
                view_temp.setVisibility(View.GONE);
            }
        } else {
            tvHeader.setVisibility(View.GONE);
            view_temp.setVisibility(View.VISIBLE);
        }
        // 显示申请与通知item
        if(!TextUtils.isEmpty(beizhu))
            nameTextview.setText(beizhu);
        else
            nameTextview.setText(usernick);
        showUserAvatar(iv_avatar, useravatar);

        return convertView;
    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getHeader().substring(0, 1);
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    private class MyFilter extends Filter {
        List<User> mList = null;

        public MyFilter(List<User> myList) {
            super();
            this.mList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(
                CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mList == null) {
                mList = new ArrayList<>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
                Log.e("1", copyUserList.size()+"");
            } else {
                String prefixString = prefix.toString();
                final int count = mList.size();
                final ArrayList<User> newValues = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    final User user = mList.get(i);
                    String username = user.getUsername();

                    EMConversation conversation = EMChatManager.getInstance()
                            .getConversation(username);
                    if (conversation != null) {
                        username = conversation.getUserName();
                    }

                    if (username.startsWith(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected synchronized void publishResults(CharSequence constraint,
                                                   FilterResults results) {
            userList.clear();
            userList.addAll((List<User>) results.values);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private void showUserAvatar(ImageView iamgeView, String avatar) {
        if(avatar==null||avatar.equals("")||avatar.equals("default")) return;
        final String url_avatar = avatar;
        iamgeView.setTag(url_avatar);

        Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,
                new ImageDownloadedCallBack() {

                    @Override
                    public void onImageDownloaded(ImageView imageView,
                                                  Bitmap bitmap,int status) {
                        if(status==-1){
                            if (imageView.getTag().equals(url_avatar)) {
                                imageView.setImageBitmap(bitmap);

                            }
                        }
                    }

                });
        if (bitmap != null)
            iamgeView.setImageBitmap(bitmap);


    }

}
