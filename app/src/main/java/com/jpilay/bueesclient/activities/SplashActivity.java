package com.jpilay.bueesclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.jpilay.bueesclient.R;

public class SplashActivity extends AppCompatActivity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.preferences), Context.MODE_PRIVATE);
				Boolean estado = sp.getBoolean(getResources().getString(R.string.status), false);

				if (estado){
					Intent i = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(i);
					finish();
				}else{
					Intent i = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(i);
					finish();
				}

			}
		}, SPLASH_TIME_OUT);
	}

}