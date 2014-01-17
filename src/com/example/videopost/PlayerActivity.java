package com.example.videopost;

import java.util.TreeSet;

import com.example.videopost.customviews.PlayerFragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
	private int width;
	private int height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
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
		/*width = size.x;
		height = (size.y + (getActionBar().getHeight()));*/
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			mPlayer = PlayerFragment.newInstance(mUrl, new int[] {
					(Integer) height, (Integer) width }, new int[] {
					(Integer) width, (Integer) height + getActionBar().getHeight()+getNavigationBarHeight()+getStatusBarHeight()});
		else
			mPlayer = PlayerFragment.newInstance(mUrl, new int[] {
					(Integer) width, (Integer) height }, new int[] {
					(Integer) height+ getActionBar().getHeight() +getNavigationBarHeight()+getStatusBarHeight(), (Integer) width });
		ft.replace(R.id.frame, mPlayer, "player");
		ft.commit();

	}


// navigation bar (at the bottom of the screen on a Nexus device)
private int getNavigationBarHeight() {
	Resources resources = getResources();
	int resourceId = resources.getIdentifier("navigation_bar_height",
			"dimen", "android");
	if (resourceId > 0) {
		return resources.getDimensionPixelSize(resourceId);
	}
	return 0;
}

// navigation bar (at the bottom of the screen on a Nexus device)
private int getStatusBarHeight() {
	Resources resources = getResources();
	int resourceId = resources.getIdentifier("status_bar_height", "dimen",
			"android");
	if (resourceId > 0) {
		return resources.getDimensionPixelSize(resourceId);
	}
	return 0;
}


	@Override
	public void setTitle(CharSequence title) {
		mTitle = (String) title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		mPlayer.onConfigurationChanged(newConfig);
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			if (Build.VERSION.SDK_INT < 16) {
				getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			} else {
				View decorView = getWindow().getDecorView();
				// Hide the status bar.
				int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
				decorView.setSystemUiVisibility(uiOptions);
			}
			// Remember that you should never show the action bar if the
			// status bar is hidden, so hide that too if necessary.
			ActionBar actionBar = getActionBar();
			actionBar.hide();
			if (!bSmall) {
				mSizeBtn.setVisibility(View.GONE);
				mOrienBtn.setVisibility(View.GONE);
			} else {
				mSizeBtn.setVisibility(View.VISIBLE);
				mOrienBtn.setVisibility(View.VISIBLE);
			}

		}
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			View decorView = getWindow().getDecorView();
			// Hide the status bar.
			int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
			decorView.setSystemUiVisibility(uiOptions);
			// Remember that you should never show the action bar if the
			// status bar is hidden, so hide that too if necessary.
			ActionBar actionBar = getActionBar();
			actionBar.show();
			mSizeBtn.setVisibility(View.VISIBLE);
			mOrienBtn.setVisibility(View.VISIBLE);

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.size:
			if (bSmall) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						width, height);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.orientation);
				mPlayer.getView().setLayoutParams(params);
				bSmall = false;
			} else {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						320, 180);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.ALIGN_TOP, R.id.orientation);
				params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mPlayer.getView().setLayoutParams(params);
				mPlayer.setVideoParams(320, 180);
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
