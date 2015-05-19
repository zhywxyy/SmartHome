package com.smarthome.smarthome;

import com.smarthome.player.VideoPlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnVideo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnVideo = (Button) this.findViewById(R.id.btnVideo);  
		btnVideo.setOnClickListener(new ClickEvent());  
	}
	
	class ClickEvent implements OnClickListener {  
		  
        @Override  
        public void onClick(View arg0) {  
            if (arg0 == btnVideo) {
            	 Intent intent=new Intent();  
                 intent.setClass(MainActivity.this, VideoPlayer.class);  
                 startActivity(intent);
            }
        }		
	}  
}
