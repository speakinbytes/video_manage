package com.example.videopost.customviews;

import com.example.videopost.R;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;
import android.view.View.OnClickListener;

/**
 * 
 * El player puede ser fijo (por defecto), los controles se encuentran debajo de
 * video, o relativo, los controles se encuentran una capa por encima del video.
 * 
 * Se pueden configurar tanto las imagenes de los botones de control como el
 * fondo del reproductor y de los controles
 * 
 */
public class CustomVideoView extends VideoView implements OnCompletionListener,
		OnPreparedListener, OnVideoSizeChangedListener, OnErrorListener,
		OnClickListener {

	private String TAG = "CustomVideoView";
	private Context mCtx;
	private String urlVideo;

	private final String VIDEO_PREFERENCES = "VIDEO_PREFERENCES";
	private final String VIDEO_STATE = "VIDEO_STATE";

	private ProgressDialog pd;
	private Boolean videoIniciado = false;
	private int mMillis = 0;
	private CustomVideoListener mListener;

	/**
	 * Se usa en onMesare para determinar el tama–o del video player. El ancho
	 */
	public int forcedWidth;

	/**
	 * Se usa en onMesare para determinar el tama–o del video player. El ancho
	 */
	public int forcedHeight;

	/**
	 * Constructor basico
	 * 
	 * @param mCtx
	 */
	public CustomVideoView(Context context) {
		super(context);
		this.mCtx = context;
		setOnClickListener(this);

	}

	/**
	 * Contructor
	 * 
	 * @param mCtx
	 * @param attrs
	 */
	public CustomVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mCtx = context;
		setOnClickListener(this);
	}

	/**
	 * Metodo que inicia la reproduccion del video
	 */
	public void iniciarVideo(String videoUrl) {

		try {
			this.urlVideo = videoUrl;
			pd = new ProgressDialog(mCtx);
			pd.setMessage(mCtx.getString(R.string.cargando_video));
			pd.closeOptionsMenu();
			pd.setCancelable(true);
			pd.setCanceledOnTouchOutside(false);
			pd.show();

			new Thread() {
				public void run() {
					try {
						setOnPreparedListener(CustomVideoView.this);
					} catch (Exception e) {
						if (pd != null) {
							pd.dismiss();
						}
						Toast.makeText(mCtx, mCtx.getString(R.string.error_video),
								Toast.LENGTH_SHORT).show();
					}

				}
			}.start();

			Log.v(TAG, Uri.parse(urlVideo).toString());
			CustomVideoView.this.setVideoURI(Uri.parse(urlVideo));

		} catch (Exception e) {
			if (pd != null) {
				pd.dismiss();
			}

			Toast.makeText(mCtx, mCtx.getString(R.string.error_video),
					Toast.LENGTH_SHORT).show();
		}

		// Cuando el video se completa paramos el servicio de estadisticas y
		// finalizamos la actividad
		CustomVideoView.this.setOnCompletionListener(this);

		// Cuando se produce un error en el video, quitamos el processdialog
		CustomVideoView.this.setOnErrorListener(CustomVideoView.this);

	}

	/**
	 * Metodos que controla el video cuando pasa por onResume en un Activity
	 */
	public void onResume() {

		if (videoIniciado && !isPlaying()) {
			seekTo(getLastVideoPosition());
			start();
		}
	}

	/**
	 * Metodos que controla el video cuando pasa por onStart en un Activity
	 */
	public void onStart() {
		if (videoIniciado && !isPlaying()) {
			seekTo(getLastVideoPosition());
			start();
		}
	}

	/**
	 * Metodos que controla el video cuando pasa por onPause en un Activity
	 */
	public void onPause() {

		if (isPlaying()) {
			pause();
			saveVideoPosition();
		}
	}

	/**
	 * Metodos que controla el video cuando pasa por onStop en un Activity
	 */
	public void onStop() {

		if (isPlaying()) {
			pause();
			saveVideoPosition();
		}
	}

	public void onDestroy() {

		if (CustomVideoView.this != null && CustomVideoView.this.isPlaying()
				&& CustomVideoView.this.canPause()) {
			CustomVideoView.this.stopPlayback();
			videoIniciado = false;
		}
	}

	/**
	 * Metodo que devuelve la ultima posicion del video
	 * 
	 * @return ultima posicion reproducida del video
	 **/
	private int getLastVideoPosition() {
		// Restore preferences
		SharedPreferences settings = mCtx.getSharedPreferences(
				VIDEO_PREFERENCES, 0);
		return settings.getInt(VIDEO_STATE, 0);
	}

	/**
	 * Metodo para guardar la posicion de reproduccion del video
	 */
	private void saveVideoPosition() {

		// We need an Editor object to make preference changes.
		// All objects are from android.mCtx.mCtx
		SharedPreferences settings = mCtx.getSharedPreferences(
				VIDEO_PREFERENCES, 0);
		SharedPreferences.Editor editor = settings.edit();

		try {

			if (CustomVideoView.this != null) {
				if (getCurrentPosition() < getDuration()) {
					editor.putInt(VIDEO_STATE, getCurrentPosition());
				} else {
					// Si se ha acabado guardamos el principio
					editor.putInt(VIDEO_STATE, 0);
				}
				// Commit the edits!
				editor.commit();
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

	}


	public void onCompletion(MediaPlayer mp) {
		videoIniciado = false;
		mListener.onCompletionListener();
	}

	public void onPrepared(MediaPlayer mp) {
		pd.dismiss();
		try {
			if (mMillis != 0) {
				seekTo(mMillis);
			}

			if (!videoIniciado) {
				mp.setOnVideoSizeChangedListener(this);
				start();
				videoIniciado = true;
				if (mListener != null)
					mListener.onPrepareListener();
			}
		} catch (IllegalStateException e) {
			Log.e(TAG, e.toString());
		}

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
		if (mListener != null)
			mListener.onVideoSizeChanged();

	}

	@Override
	public void onClick(View v) {
		if (mListener != null)
			mListener.onClickView();
	}

	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		pd.dismiss();

		if (mListener != null)
			mListener.onErrorListener();
		return true;
	}

	public void onMeasure(int specwidth, int specheight) {

		getHolder().setFixedSize(forcedWidth, forcedHeight);
		setMeasuredDimension(forcedWidth, forcedHeight);
	}

	/**
	 * Resize the view size and request a layout.
	 */
	public void changeVideoSize(int newWidth, int newHeight) {
		forcedWidth = newWidth;
		forcedHeight = newHeight;

		forceLayout();
		invalidate();
	}

	/**
	 * Clase interna para escuchar los eventos de la clase
	 */
	public interface CustomVideoListener {

		public void onErrorListener();

		public void onVideoSizeChanged();

		public void onCompletionListener();

		public void onPrepareListener();

		public void onClickView();

	}

}
