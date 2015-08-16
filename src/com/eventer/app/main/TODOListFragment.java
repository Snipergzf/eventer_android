package com.eventer.app.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eventer.app.R;

@SuppressLint("NewApi")
public class TODOListFragment extends Fragment {
	private LinearLayout container;
	private CollectListFragment collectfragment;
	private ConcernListFragment concernfragment;
	private Fragment[] fragments;
	public static TODOListFragment instance;
    private int currentIndex;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_todo_list, container, false);
		instance=this;
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		// TODO Auto-generated method stub
		container=(LinearLayout)rootView.findViewById(R.id.fragment_container);
		collectfragment = new CollectListFragment();
		concernfragment = new ConcernListFragment();
		fragments = new Fragment[] {collectfragment,concernfragment};
		getChildFragmentManager().beginTransaction()
        .add(R.id.fragment_container, fragments[0])
        .add(R.id.fragment_container, fragments[1])
        .hide(fragments[1]).show(fragments[0]).commit();
	}
	
	public void changeView(int vid){
		FragmentTransaction trx = getChildFragmentManager()
                .beginTransaction();
        trx.hide(fragments[currentIndex]);
        if (!fragments[vid].isAdded()) {
            trx.add(R.id.fragment_container, fragments[vid]);
        }
        trx.show(fragments[vid]).commit();
        currentIndex=vid;
	}
}
