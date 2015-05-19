/**
 * 
 */
package com.smarthome.player;

import com.smarthome.smarthome.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * @author wangxuan
 *
 */
public class VideoPlayer extends Activity{
	
	private SurfaceView surfaceView;  
    private Button btnPause, btnPlay, btnStop;  
    private VPlayer vplayer;
    private SeekBar skbProgress;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoplayer);
		
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);  
		  
        btnPlay = (Button) this.findViewById(R.id.btnPlay);  
        btnPlay.setOnClickListener(new ClickEvent());  
  
        btnPause = (Button) this.findViewById(R.id.btnPause);  
        btnPause.setOnClickListener(new ClickEvent());  
  
        btnStop = (Button) this.findViewById(R.id.btnStop);  
        btnStop.setOnClickListener(new ClickEvent());         
        
        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);  
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        
        vplayer = new VPlayer(surfaceView, skbProgress); 
	}
	
	class ClickEvent implements OnClickListener {  
		  
        @Override  
        public void onClick(View arg0) {  
            if (arg0 == btnPlay) {
            	//String url="http://daily3gp.com/vids/family_guy_penis_car.3gp";
            	//String url = "http://imgWNAS0WNAS5WNASgd0.m3wscdn-h.kukuplay.com/ts/380789_1361124906157_1368438620253/playlist.m3u8";
            	String url = "http://m3u8.uusee.com/53/4A/534AD157-0000-0000-FFFF-FFFFFFFFFF14_ts_live.m3u8"; 
            	//String url = "/mnt/sdcard/test.mp4";//= "http://v.yinyuetai.com/video/2278949";
            	
            	vplayer.play(url); 
            }
            else if (arg0 == btnPause) 
            {
            	vplayer.pause(); 
            }
            else if (arg0 == btnStop) 
            {
            	vplayer.stop(); 
            }
        }		
	}  
	
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * vplayer.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			vplayer.mediaPlayer.seekTo(progress);
		}
	} 
}
