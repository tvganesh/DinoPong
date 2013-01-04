package com.tvganesh.dinopong;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**********************************************************************************************
 * Dino Pong - Designed and developed by Tinniam V Ganesh using AndEngine
 * Date: 28 Dec 2012
 * Blog: http://gigadom.wordpress.com
 * 
 * AndEngine developed by  (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 * 
 */


public class SplashScreen extends Activity {
	private long ms=0;
	private long splashTime=2000;
	private boolean splashActive = true;
	private boolean paused=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		 Thread mythread = new Thread() {
    	public void run() {
    		try {
    			while (splashActive && ms < splashTime) {
    				if(!paused)
    					ms=ms+100;
    				sleep(100);
    			}
    		} catch(Exception e) {}
    		finally {
    			Intent intent = new Intent(SplashScreen.this, DinoPong.class);
        		startActivity(intent);
    		}
        	}
    };
    mythread.start(); 
	}

}