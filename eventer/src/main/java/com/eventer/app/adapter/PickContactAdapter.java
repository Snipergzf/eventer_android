package com.eventer.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventer.app.Constant;
import com.eventer.app.R;
import com.eventer.app.entity.User;
import com.eventer.app.other.ShareToSingleActivity;
import com.eventer.app.task.LoadUserAvatar;
import com.eventer.app.task.LoadUserAvatar.ImageDownloadedCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的好友Adapter实现
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
public  class PickContactAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean[] isCheckedArray;
    private Bitmap[] bitmaps;
    private LoadUserAvatar avatarLoader;
    private List<User> list = new ArrayList<>();
    private List<String> exitingMembers = new ArrayList<>();
    private List<String> addList = new ArrayList<>();
    private int res;

    public PickContactAdapter(Context context, int resource,
                              List<User> users) {

        layoutInflater = LayoutInflater.from(context);
        avatarLoader = new LoadUserAvatar(context, Constant.IMAGE_PATH);
        this.res = resource;
        this.list = users;
        bitmaps = new Bitmap[list.size()];
        isCheckedArray = new boolean[list.size()];

    }

    public Bitmap getBitmap(int position) {
        return bitmaps[position];
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        convertView = layoutInflater.inflate(res, null);

        ImageView iv_avatar = (ImageView) convertView
                .findViewById(R.id.iv_avatar);
        TextView tv_name = (TextView) convertView
                .findViewById(R.id.tv_name);
        TextView tvHeader = (TextView) convertView
                .findViewById(R.id.header);
        final User user = list.get(position);

        final String avatar = user.getAvatar();
        String name;
        String beizhu = user.getBeizhu();
        String header = user.getHeader();
        final String username = user.getUsername();
        if(TextUtils.isEmpty(beizhu)){
            name =  user.getNick();
        }else {
            name = beizhu;
        }
        tv_name.setText(name);
        iv_avatar.setImageResource(R.drawable.default_avatar);
        iv_avatar.setTag(avatar);
        Bitmap bitmap;
        if (avatar != null && !avatar.equals("")&&!avatar.equals("default")) {
            bitmap = avatarLoader.loadImage(iv_avatar, avatar,
                    new ImageDownloadedCallBack() {
                        @Override
                        public void onImageDownloaded(ImageView imageView,
                                                      Bitmap bitmap,int status) {
                            if(status==-1){
                                if (imageView.getTag() == avatar) {
                                    imageView.setImageBitmap(bitmap);
                                }
                            }
                        }

                    });

            if (bitmap != null) {
                iv_avatar.setImageBitmap(bitmap);
            }
            bitmaps[position] = bitmap;
        }
        if (position == 0 || header != null
                && !header.equals(getItem(position - 1))) {
            if ("".equals(header)) {
                tvHeader.setVisibility(View.GONE);
            } else {
                tvHeader.setVisibility(View.VISIBLE);
                tvHeader.setText(header);
            }
        } else {
            tvHeader.setVisibility(View.GONE);
        }

        // 选择框checkbox
        final CheckBox checkBox = (CheckBox) convertView
                .findViewById(R.id.checkbox);

        if (exitingMembers != null && exitingMembers.contains(username)) {
            checkBox.setButtonDrawable(R.drawable.btn_check);
        } else {
            checkBox.setButtonDrawable(R.drawable.check_blue);
        }

        if (addList != null && addList.contains(username)) {
            checkBox.setChecked(true);
            isCheckedArray[position] = true;
        }

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // 群组中原来的成员一直设为选中状态
                if (exitingMembers.contains(username)) {
                    isChecked = true;
                    checkBox.setChecked(true);
                }
                isCheckedArray[position] = isChecked;

                if (isChecked) {
                    // 选中用户显示在滑动栏显示
                    ShareToSingleActivity.instance.showCheckImage(getBitmap(position),
                            list.get(position));

                } else {
                    // 用户显示在滑动栏删除
                    ShareToSingleActivity.instance.deleteImage(list.get(position));

                }

            }
        });
        // 群组中原来的成员一直设为选中状态
        if (exitingMembers.contains(username)) {
            checkBox.setChecked(true);
            isCheckedArray[position] = true;
        } else {
            checkBox.setChecked(isCheckedArray[position]);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    public List<String> getMembers(){
        return exitingMembers;
    }

    public List<String> getAddList(){
        return addList;
    }

    @Override
    public String getItem(int position) {
        if (position < 0) {
            return "";
        }

        return list.get(position).getHeader();

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
