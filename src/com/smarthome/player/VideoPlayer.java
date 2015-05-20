/**
 * 
 */
package com.smarthome.player;

import java.util.Timer;
import java.util.TimerTask;

import com.smarthome.smarthome.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

/**
 * @author wangxuan
 *
 */
public class VideoPlayer extends Activity{
	
	private static final int REFRESH_SEEKBAR = 0;
	private static final int REFRESH_PROGRESSBAR = 1;
	private static final int DISMISS_PROGRESSBAR = 2;
	
	private VideoView videoView;  
    private Button btnPause, btnPlay, btnStop;  
   
    private SeekBar seekBar;  
    
    private PackageManager pm;
    private ProgressDialog progressBar;
    
    private Timer mTimer = new Timer();
    
    private boolean progressBarisshow = true; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoplayer);
		
		videoView = (VideoView) this.findViewById(R.id.videoView);  
		  
        btnPlay = (Button) this.findViewById(R.id.btnPlay);  
        btnPlay.setOnClickListener(new ClickEvent());  
  
        btnPause = (Button) this.findViewById(R.id.btnPause);  
        btnPause.setOnClickListener(new ClickEvent());  
        btnPause.setVisibility(View.GONE);
  
        btnStop = (Button) this.findViewById(R.id.btnStop);  
        btnStop.setOnClickListener(new ClickEvent());         
        
        seekBar = (SeekBar) this.findViewById(R.id.seekBar);  
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        
        pm = getPackageManager();
        
        progressBar = new ProgressDialog(this);
         
        mTimer.schedule(mTimerTask, 0, 1000);
        
        
	}
	
	class ClickEvent implements OnClickListener {  		  
        @Override  
        public void onClick(View arg0) {  
            if (arg0 == btnPlay) {
            	
            	//String url = "http://www.androidbook.com/akc/filestorage/android/documentfiles/3389/movie.mp4";
            	// 山东卫视
            	//String url = "http://61.155.130.204/live/14?key=-zoHLEBota08DWaDuk6lsERV1h_xADTA&ver=seg&n=1&a=4914&cip=222.73.92.52";
            	// 欢视网民视
            	//String url = "http://60.250.71.253:8134/hls-live/livepkgr/_definst_/liveevent/STB-01.m3u8";
            	// 欢视网台视
            	//String url = "http://60.250.71.253:8134/hls-live/livepkgr/_definst_/liveevent/STB-02.m3u8";
            	// 欢视网中视
            	//String url = "http://60.250.71.253:8134/hls-live/livepkgr/_definst_/liveevent/STB-03.m3u8";
            	// 华视HD
            	// String url = "http://60.250.71.253:8134/hls-live/livepkgr/_definst_/liveevent/STB-04.m3u8";
            	//公视HD
            	// String url = "http://60.250.71.253:8134/hls-live/livepkgr/_definst_/liveevent/STB-05.m3u8";
            	//民视新闻台HD
            	// String url = "http://60.250.71.253:8134/hls-live/livepkgr/_definst_/liveevent/STB-06.m3u8";
            	
            	String url = "http://199.195.195.108:32001";
            	
            	Uri uri = Uri.parse(url);
            	
            	videoView.setVideoURI(uri);	
            	videoView.requestFocus();
            	videoView.start();  
            	btnPlay.setVisibility(View.GONE);
            	btnPause.setVisibility(View.VISIBLE);
            	progressBar.show();
            }
            else if (arg0 == btnPause) 
            {
            	videoView.pause();  
            	btnPause.setVisibility(View.GONE);
            	btnPlay.setVisibility(View.VISIBLE);
            }
            else if (arg0 == btnStop) 
            {
            	videoView.stopPlayback();
            }
        }		
	}  
	
	TimerTask mTimerTask = new TimerTask() {
		
		private long new_KB, old_KB;
		
		@Override
		public void run() {	
			if(videoView.isPlaying() && progressBarisshow == true)
			{
				handleProgress.sendEmptyMessage(DISMISS_PROGRESSBAR);
				progressBarisshow = false;
			}
			else if (videoView.isPlaying() && seekBar.isPressed() == false) {
				handleProgress.sendEmptyMessage(REFRESH_SEEKBAR);
			}
			else if(!videoView.isPlaying())
			{
				new_KB = getUidRxBytes() - old_KB;
	            old_KB = getUidRxBytes();
	            Message msg = new Message();           
	            msg.what = REFRESH_PROGRESSBAR;
	            msg.obj = new_KB;
	            handleProgress.sendMessage(msg);
			}
			
			/*System.out.println("videoView.isPlaying() = " + (videoView.isPlaying() == true ? "true" : "false"));
			System.out.println("progressBarisshow = " + (progressBarisshow == true ? "true" : "false"));*/
		}
	};
	
	Handler handleProgress = new Handler() {  		
		int position, duration, pos, percent;
		long s_KB;
		
        public void handleMessage(Message msg) {  
        	switch(msg.what)
        	{
			case REFRESH_SEEKBAR:
				position = videoView.getCurrentPosition();
				duration = videoView.getDuration();

				if (duration > 0) {
					pos = seekBar.getMax() * position / duration;
					seekBar.setProgress((int) pos);
					progressBar.dismiss();
				}

				percent = videoView.getBufferPercentage();
				seekBar.setSecondaryProgress(percent);
				break;
        	case REFRESH_PROGRESSBAR: 
        		s_KB = (Long) msg.obj;
        		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+s_KB);
        		progressBar.setMessage("视频加载中("+s_KB+"K/S)...");
        		break;
        	case DISMISS_PROGRESSBAR:
        		System.out.println("dismiss");
        		progressBar.dismiss();
        		break;
        	}
        };  
    };  
	
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			//this.progress = progress * vplayer.mediaPlayer.getDuration()
			//		/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			//vplayer.mediaPlayer.seekTo(progress);
		}
	} 
	
	public long getUidRxBytes() { // 获取总的接受字节数，包含Mobile和WiFi等
		ApplicationInfo ai = null;
		try {
			ai = pm.getApplicationInfo("com.smarthome.smarthome",
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0
				: (TrafficStats.getTotalRxBytes() / 1024);
	}
}
