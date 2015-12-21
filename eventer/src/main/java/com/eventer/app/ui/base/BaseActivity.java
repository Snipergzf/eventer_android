package com.eventer.app.ui.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.eventer.app.R;


@SuppressLint("SetTextI18n")
public class BaseActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        

    }

    public void setBaseTitle(int res){
        TextView tv_title=(TextView)findViewById(R.id.include_title);
        tv_title.setText(res);
    }

    public void setBaseTitle(String title){
        TextView tv_title=(TextView)findViewById(R.id.include_title);
        tv_title.setText(title);
    }

    public void back(View view) {
        finish();
    }

}
