package com.example.videopost.customviews;

import com.example.videopost.R;
import com.example.videopost.utils.Constants;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayerFragment extends Fragment implements
	OnPreparedListener, OnErrorListener,
		CustomMediaController.MediaPlayerControl, OnTouchListener {

	private static final String TAG = "PLAYER_FRAGMENT";
	private CustomVideoView videoView;
	private View view;
	private CustomMediaController controller;
	private int[] mVertParams = new int[2], mHoriParams = new int[2];
	private boolean bFullScreen;
	int width;
	int height;
	
 /**
  * Creamos una nueva instancia del Player fragment
  * @param url String con la url del v’deo a reproducir
  * @param vert dimensiones del videoView en portrait
  * @return
  */
    public static PlayerFragment newInstance(String url, int[] vert, int[] hor) {
    	PlayerFragment f = new PlayerFragment();
    	//Pasamos como argumentos la url y las dimensiones en caso de ser diferentes de null
        Bundle args = new Bundle();
        args.putString(Constants.ARG_URL, url);
        if(vert!=null)
        	args.putIntArray(Constants.ARG_PARAMS_VERT, vert);
        if(hor!=null)
        	args.putIntArray(Constants.ARG_PARAMS_HOR, hor);
		
        f.setArguments(args);
        return f;
    }

	/**
	 * This method will only be called once when the retained Fragment is first
	 * created.
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retain this fragment across configuration changes.
		setRetainInstance(true);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_player, container, false);
		// Videoview customizado
	
		if(getArguments()!=null)
		{
			
			if (getArguments().containsKey(Constants.ARG_PARAMS_VERT))
			{
				mVertParams = getArguments().getIntArray(Constants.ARG_PARAMS_VERT);
				mVertParams[1] = (int) (mVertParams[0] * ((float) 9 / (float) 16));
			}
			
			if(getArguments().containsKey(Constants.ARG_PARAMS_HOR))
			{
				mHoriParams = getArguments().getIntArray(Constants.ARG_PARAMS_HOR);
				//mHoriParams[1] = (int) (mHoriParams[0] * ((float) 9 / (float) 16));
			}
			
		}
	
	    //Capturamos el toque sobre el fragment y mostramos el media controller
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				controller.show();
				return false;
			}
		});
		return view;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
			videoView = (CustomVideoView) view.findViewById(R.id.videoView);
			
			//Creamos el MediaController y se lo a–adimos al FrameLayout de la interfaz
			// Además habilitamos el botón de fullScreen
			controller = new CustomMediaController(getActivity());
			controller.setMediaPlayer(this);
			controller.updateFullScreen();
			controller.setAnchorView((FrameLayout) view.findViewById(R.id.player_control));
			controller.setEnabled(true);
			 // Checks la orientatición de la pantalla y asignamos las dimensiones del video en funci—n de dicha orientaci—n
		    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    	setVideoParams(mHoriParams[1], mHoriParams[0]);
		    	videoView.setLayoutParams(new RelativeLayout.LayoutParams(mHoriParams[1], mHoriParams[0]));
		    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
		    	//ancho
		    	videoView.setLayoutParams(new RelativeLayout.LayoutParams(mVertParams[0], mVertParams[1]));
		    	setVideoParams(mVertParams[0], mVertParams[1]);
		    }
		    
		    if (getArguments().containsKey(Constants.ARG_URL)) {
				iniciarVideo(getArguments().getString(Constants.ARG_URL));
			}
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		 // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	videoView.setLayoutParams(new RelativeLayout.LayoutParams(mHoriParams[0], mHoriParams[1]));
	    	videoView.changeVideoSize(mHoriParams[0], mHoriParams[1]);
	    	
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	//ancho
	    	videoView.setLayoutParams(new RelativeLayout.LayoutParams(mVertParams[0], mVertParams[1]));
	    	videoView.changeVideoSize(mVertParams[0], mVertParams[1]);
	    }
	    controller.setAnchorView((FrameLayout) view.findViewById(R.id.player_control));

	}
	/**
	 * Métodos que modifica las dimensiones del vídeo
	 * @param w int con el ancho deseado
	 * @param h int con el alto deseado
	 * @param bVertical boolean que define si se están pasando par‡metros para la posición horizontal o vertical. 
	 * True significa que es vertial y false que es horizontal
	 */
	public void setVideoParams(int w, int h)
	{
	    	videoView.setLayoutParams(new RelativeLayout.LayoutParams(w, h));
	    	videoView.changeVideoSize(w, h);
	   		
	}

	@Override
	public void onStart() {
		super.onStart();
		if (videoView != null)
			videoView.onStart();

	}

	@Override
	public void onResume() {
		super.onResume();
		if (videoView != null)
			videoView.onPause();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (videoView != null)
			videoView.onPause();

	}

	@Override
	public void onStop() {
		super.onStop();
		if (videoView != null)
			videoView.onStop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (videoView != null)
			videoView.onDestroy();
	}


	// INICIAR VIDEO AL DARLE AL PLAY
	public void iniciarVideo(String url) {
		videoView.setVisibility(View.VISIBLE);

		if(videoView.isPlaying())
			videoView.stopPlayback();
		if(url!=null && !url.isEmpty())
			videoView.iniciarVideo(url);

		videoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				controller.show();
				return false;
			}
		});
	}
	

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		return false;
	}


	@Override
	public void onPrepared(MediaPlayer arg0) {
		controller.show();
	}

	// Implement VideoMediaController.MediaPlayerControl
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return videoView.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return videoView.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return videoView.isPlaying();
	}

	@Override
	public void pause() {
		videoView.pause();
	}

	@Override
	public void seekTo(int i) {
		videoView.seekTo(i);
	}

	@Override
	public void start() {
		videoView.start();
	}

	@Override
	public boolean isFullScreen() {
		return bFullScreen;
	}

	@Override
	public void toggleFullScreen() {
		if(!isFullScreen())
		{
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			videoView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			bFullScreen = true;
		}
		else
		{
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			videoView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			bFullScreen = false;
		}
			
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		controller.show();
		return false;
	}

}
