package com.example.videopost;

import com.example.videopost.customviews.PlayerFragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayerActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = "PLAYER_ACTIVITY";
	private PlayerFragment mPlayer;
	private String mTitle;
	private Button mOrienBtn;
	private Button mSizeBtn;
	private String mUrl = "http://www.tools4movies.com/dvd_catalyst_profile_samples/The%20Amazing%20Spiderman%20bionic.mp4";
	private boolean bSmall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		addFragment(true);
		mOrienBtn = (Button) findViewById(R.id.orientation);
		mOrienBtn.setOnClickListener(this);
		mSizeBtn = (Button) findViewById(R.id.size);
		mSizeBtn.setOnClickListener(this);

	}

	private void addFragment(boolean bFullScreen) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("player");
		if (prev != null) 
			ft.remove(prev);
		
		ft.addToBackStack(null);
		
		mPlayer = PlayerFragment.newInstance(mUrl, new int[] {LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT });
		ft.replace(R.id.frame, mPlayer, "player");
		ft.commit();

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = (String) title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
				&& !bSmall) {
			mSizeBtn.setVisibility(View.GONE);
			mOrienBtn.setVisibility(View.GONE);
			((PlayerFragment) mPlayer).setVideoParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.removeRule(RelativeLayout.ALIGN_TOP);
			mPlayer.getView().setLayoutParams(params);
		}
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mSizeBtn.setVisibility(View.VISIBLE);
			mOrienBtn.setVisibility(View.VISIBLE);
			bSmall = !bSmall;
			mSizeBtn.performClick();
		}
		mPlayer.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.orientation:
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			break;
		case R.id.size:
			if (bSmall) {
				((PlayerFragment) mPlayer).setVideoParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						true);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.orientation);
				mPlayer.getView().setLayoutParams(params);
				bSmall = false;
			} else {
				((PlayerFragment) mPlayer).setVideoParams(320, 180, true);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						320, 180);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.orientation);
				params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mPlayer.getView().setLayoutParams(params);
				bSmall = true;
			}
			break;

		}

	}

	@Override
	public void onBackPressed() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else
			super.onBackPressed();
	}

}
