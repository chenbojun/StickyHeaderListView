package com.sunfusheng.StickyHeaderListView.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.sunfusheng.StickyHeaderListView.R;

/**
 * Created by bojunchen on 2017/11/14.
 */

public class TestActivity extends FragmentActivity {

	Fragment mainFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		init();
	}

	private void init() {
		mainFragment = getSupportFragmentManager().findFragmentByTag("mainFragment");
		if (mainFragment == null) {
			mainFragment = new MainFragment();
		}
		if (!mainFragment.isAdded()) {
			getSupportFragmentManager().beginTransaction().add(R.id.fl_container, mainFragment, "mainFragment").commit();
		}
	}
}
