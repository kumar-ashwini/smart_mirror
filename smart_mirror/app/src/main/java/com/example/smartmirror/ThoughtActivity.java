package com.example.smartmirror;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ThoughtActivity extends Activity implements CameraGestureSensor.Listener{

	private CameraGestureSensor mGestureSensor;
	private boolean mOpenCVInitiated = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_thought);
     OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		
    	mGestureSensor = new CameraGestureSensor(this);
		mGestureSensor.addGestureListener(this);
		
		
		
		
	}//on create

	
	

	/** OpenCV library initialization. */
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				mOpenCVInitiated = true; 
				CameraGestureSensor.loadLibrary();
				mGestureSensor.start();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};  

	

	public void handleFinish(View v) {
		finish();
	} 

	/** Called when the activity is resumed. The gesture detector is initialized. */
	@Override
	public void onResume() {
		super.onResume(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.start();
	}
	
	/** Called when the activity is paused. The gesture detector is stopped
	 *  so that the camera is no longer working to recognize gestures. */
	@Override
	public void onPause() {
		super.onPause(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.stop();
	}


	
	@Override
	public void onGestureUp(CameraGestureSensor caller, long gestureLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureDown(CameraGestureSensor caller, long gestureLength) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
         runOnUiThread(new Runnable() {
	
	               @Override
	         public void run() {
	
	            	   finish();
		
	         }
        });
		
	}

	@Override
	public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
		// TODO Auto-generated method stub
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Intent i= new Intent(ThoughtActivity.this, ToDoActivity.class);
				startActivity(i);
				
			}
		});
	}

	
}//main
