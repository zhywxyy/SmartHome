/**
 * 
 */
package com.smarthome.player;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

/**
 * @author wangxuan
 *
 */
public class VPlayer implements OnBufferingUpdateListener,  
	OnCompletionListener, MediaPlayer.OnPreparedListener,  
	SurfaceHolder.Callback, OnVideoSizeChangedListener {  
	
	private static final String TAG = "VPlayer";
	
	private SurfaceHolder surfaceHolder;
	public  MediaPlayer mediaPlayer;  
	private SeekBar skbProgress;
	
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	
	private Timer mTimer = new Timer();
	
	public VPlayer(SurfaceView surfaceView, SeekBar skbProgress) {
		
		this.skbProgress = skbProgress;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		//surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mTimer.schedule(mTimerTask, 0, 1000);
	}
	
	public void play(String videoUrl) {
		try {
			mediaPlayer.reset();			
			mediaPlayer.setDataSource(videoUrl);
			//mediaPlayer.prepare();// prepare之后自动播放
			mediaPlayer.prepareAsync();			
			mediaPlayer.setScreenOnWhilePlaying(true);
			//mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		mediaPlayer.pause();
		mediaPlayer.setScreenOnWhilePlaying(false);
	}

	public void stop() {
		if (mediaPlayer != null) {   
            mediaPlayer.stop();  
            mediaPlayer.setScreenOnWhilePlaying(false);
            mediaPlayer.release();   
            mediaPlayer = null;   
        }   
	} 

	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};
	
	Handler handleProgress = new Handler() {  
        public void handleMessage(Message msg) {  
  
            int position = mediaPlayer.getCurrentPosition();  
            int duration = mediaPlayer.getDuration();  
              
            if (duration > 0) {  
                long pos = skbProgress.getMax() * position / duration;  
                skbProgress.setProgress((int) pos);  
            }  
        };  
    };  
	
	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
	 * function:
	 * 2015年4月25日
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnBufferingUpdateListener#onBufferingUpdate(android.media.MediaPlayer, int)
	 * function:
	 * 2015年4月25日
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		skbProgress.setSecondaryProgress(percent);  
        int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();  
        Log.v(TAG, currentProgress+"% play, " + percent + "% buffer");
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 * function:
	 * 2015年4月25日
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {  
            mediaPlayer = new MediaPlayer();  
            mediaPlayer.setDisplay(surfaceHolder);  
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
            mediaPlayer.setOnBufferingUpdateListener(this);  
            mediaPlayer.setOnPreparedListener(this);  
            mediaPlayer.setOnVideoSizeChangedListener(this);
        } catch (Exception e) {  
            Log.e(TAG, "surfaceCreated error", e);  
        }  
        Log.v(TAG, "surface created success");  
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 * function:
	 * 2015年4月25日
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 * function:
	 * 2015年4月25日
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnPreparedListener#onPrepared(android.media.MediaPlayer)
	 * function:
	 * 2015年4月25日
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub

		if (mediaPlayer.getVideoWidth() == 0
				|| mediaPlayer.getVideoHeight() == 0) {
			Log.e(TAG, "invalid video width or height");
			return;
		}

		mIsVideoReadyToBePlayed = true;

		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			mp.start();
		}

		Log.v(TAG, "onPrepared");
	}

	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnVideoSizeChangedListener#onVideoSizeChanged(android.media.MediaPlayer, int, int)
	 * function:
	 * 2015年5月1日
	 */
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub
	    Log.v(TAG, "onVideoSizeChanged");
	    
		if (width == 0 || height == 0) {
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height
					+ ")");
			return;
		}

		mIsVideoSizeKnown = true;
		surfaceHolder.setFixedSize(width, height);

		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			mp.start();
		}
	}
}