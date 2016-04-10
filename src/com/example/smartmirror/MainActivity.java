package com.example.smartmirror;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import com.example.asr.*;

import edu.washington.cs.touchfreelibrary.sensors.CameraGestureSensor;
import edu.washington.cs.touchfreelibrary.sensors.ClickSensor;



import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AnalogClock;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ListeningActivity implements  CameraGestureSensor.Listener, ClickSensor.Listener,TTS.OnUtteranceCompletedListener{
DigitalClock clock;
TextView datetv;
private String ACCESS_TOKEN="9a7d4f922b0f48c78d1dc3ed7dcfb39b";
private AIDataService aiDataService;
private AIConfiguration config;
private TTS tts;

private String date1;

private SharedPreferences prefs;

private HashMap<String,String> hm;
private Typeface weatherFont;

private ImageView eventPic;
private TextView cityField;
private TextView detailsField;
private TextView currentTemperatureField;
private TextView weatherIcon;
private TextView calenderEvent;
private Handler handler=new Handler();

private CameraGestureSensor mGestureSensor;
private boolean mOpenCVInitiated = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_screen);
       
        
        clock= (DigitalClock) findViewById(R.id.digitalClock1);
        datetv= (TextView) findViewById(R.id.dateTime);
        cityField=(TextView) findViewById(R.id.city);
        weatherIcon=(TextView) findViewById(R.id.weatherIcon);
        currentTemperatureField= (TextView) findViewById(R.id.currentTemp1);
        detailsField= (TextView) findViewById(R.id.details1);
        calenderEvent=(TextView) findViewById(R.id.event1);
        eventPic= (ImageView) findViewById(R.id.eventPic);
        events();
        
        
        mGestureSensor = new CameraGestureSensor(this);
        mGestureSensor.addGestureListener(this);
		mGestureSensor.addClickListener(this);	
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        prefs= getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE);
        Date d= Calendar.getInstance().getTime();
        datetv.setText(d.toString().substring(0, 10));
        
        
        ConnectivityManager cm= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info1= cm.getNetworkInfo(0);
        NetworkInfo info2= cm.getNetworkInfo(1);
        
        
   	weatherFont = Typeface.createFromAsset(getAssets(), "weather.ttf");     
    weatherIcon.setTypeface(weatherFont);
    TempPreferences tp= new TempPreferences(this);
	hm= tp.getTemp();
        if((info1.isConnected()||info2.isConnected()))
        {
        	detailsField.setText(hm.get("Details"));
        	weatherIcon.setText(hm.get("Icon"));
        	currentTemperatureField.setText(hm.get("Temp"));
         updateWeatherData("newdelhi");
        }else
        {
        	detailsField.setText(hm.get("Details"));
        	weatherIcon.setText(hm.get("Icon"));
        	currentTemperatureField.setText(hm.get("Temp"));
        	
        	
        	
        }
        
        
        context = getApplicationContext(); 
        
  	  VoiceRecognitionListener.getInstance().setListener(this); 
  	 tts=TTS.getInstance(context);
  	  config = new AIConfiguration(ACCESS_TOKEN,AIConfiguration.SupportedLanguages.English,AIConfiguration.RecognitionEngine.System);
       aiDataService = new AIDataService(context, config);
        
    }//on create

    
    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(context, city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                        	
                            Toast.makeText(context, 
                                    getString(R.string.place_not_found), 
                                    Toast.LENGTH_LONG).show(); 
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                           renderWeather(json);
                        }
                    });
                }               
            }
        }.start();
    }
    
    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) + 
                    ", " + 
                    json.getJSONObject("sys").getString("country"));
             
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                    "\n" + "Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Pressure: " + main.getString("pressure") + " hPa");
             
            currentTemperatureField.setText(
                        String.format("%.2f", main.getDouble("temp"))+ " ℃");
     
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            
           
            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);
            
            
        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
    
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
            case 2 : icon = this.getString(R.string.weather_thunder);
                     break;         
            case 3 : icon = this.getString(R.string.weather_drizzle);
                     break;     
            case 7 : icon = this.getString(R.string.weather_foggy);
                     break;
            case 8 : icon = this.getString(R.string.weather_cloudy);
                     break;
            case 6 : icon = getString(R.string.weather_snowy);
                     break;
            case 5 : icon = getString(R.string.weather_rainy);
                     break;
            }
        }
        weatherIcon.setText(icon);
        hm= new HashMap<String, String>();
        hm.put("Details", detailsField.getText().toString());
        hm.put("Temp", currentTemperatureField.getText().toString());
        hm.put("Icon", weatherIcon.getText().toString());
        
        TempPreferences tp= new TempPreferences(this);
        tp.setTemp(hm);
        
    }

    public void events()
    {
    	Calendar calendar = Calendar.getInstance();
		int year=calendar.get(Calendar.YEAR);
		int month=calendar.get(Calendar.MONTH);
		int day=calendar.get(Calendar.DATE);
		
		
		calendar.set(year,month,day, 0, 0, 0);
		long startDay = calendar.getTimeInMillis();
		calendar.set(year,month,day, 23, 59, 59);
		long endDay = calendar.getTimeInMillis();

		String[] projection = new String[] { BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART };
		String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ?";
		String[] selectionArgs = new String[] { Long.toString(startDay), Long.toString(endDay) }; 
//
		Cursor cur = getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);
		
		
		
		while(cur.moveToNext())
		{
			calenderEvent.setText(cur.getString(1));
		}
		
		String eventDetail= calenderEvent.getText().toString();
		if(eventDetail.contains("Cycling"))
		{
			eventPic.setImageResource(R.drawable.cycle);
		}
				
    }


	@Override
	public void onGestureUp(CameraGestureSensor caller, long gestureLength) {
		
          runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			Toast.makeText(context, "UP", Toast.LENGTH_SHORT).show();
				
			}
		});
		
	}


	@Override
	public void onGestureDown(CameraGestureSensor caller, long gestureLength) {
	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak");
				startActivityForResult(intent, 1234);
				
			}
		});
		
	}


	@Override
	public void onGestureLeft(CameraGestureSensor caller, long gestureLength) {
		// TODO Auto-generated method stub
		
        runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Left", Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public void onGestureRight(CameraGestureSensor caller, long gestureLength) {
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Intent i= new Intent(MainActivity.this,ThoughtActivity.class);
				startActivity(i);
				
			}
		});
		
	}
	
	
	

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
		startListening();
		
		
	}
	
	/** Called when the activity is paused. The gesture detector is stopped
	 *  so that the camera is no longer working to recognize gestures. */
	@Override
	public void onPause() {
		super.onPause(); 
		if(!mOpenCVInitiated)
			return; 
		mGestureSensor.stop();
		stopListening();
	
	}

	

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		tts.shutdown();
	}


	@Override
	public void onSensorClick(ClickSensor caller) {
		
	
	} 
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == 1234 && resultCode == RESULT_OK) {
	ArrayList<String> matches = data.getStringArrayListExtra(
	RecognizerIntent.EXTRA_RESULTS);
	
	
	if((matches.get(0).toString().equalsIgnoreCase("Open thought of the Day")))
	{
		Intent i= new Intent(MainActivity.this,ThoughtActivity.class);
		startActivity(i);
		finish();
		
	}
	
	}
	}


	@Override
	public void processError(int error) {
		// TODO Auto-generated method stub
		if(error==SpeechRecognizer.ERROR_NETWORK)
			Toast.makeText(context, "Check internet connection",Toast.LENGTH_SHORT).show();
		
	}


	@Override
	public void processVoiceCommands(String... voiceCommands) {
		// TODO Auto-generated method stub
		StringBuffer buffer=new StringBuffer();
		  
		  for (String command : voiceCommands) 
		  buffer.append(command);
		  
		  final AIRequest aiRequest = new AIRequest();
		  String str=buffer.toString();
		  Log.d("MainActivity: message sent : ", str);
		  aiRequest.setQuery(str);
		  new AsyncTask<AIRequest, Void, AIResponse>() {
			    @Override
			    protected AIResponse doInBackground(AIRequest... requests) {
			        final AIRequest request = requests[0];
			        try {
			            final AIResponse response = aiDataService.request(request);
			            return response;
			        } catch (AIServiceException e) {
			        }
			        return null;
			    }
			    @Override
			    protected void onPostExecute(AIResponse aiResponse) {
			        if (aiResponse != null) {
			          
			        	String str=aiResponse.toString();
			        	String resolved_query=aiResponse.getResult().getResolvedQuery();
			        	///////////////////////////////////////////////////////////////////////
			        	stopListening();
			        	tts.speak(resolved_query);
			        
			        	Log.d("Response : ",str );
			        }
			    }
			}.execute(aiRequest);
		  restartListeningService();
	}


	@Override
	public void onUtterenceComplete(String str) {
		// TODO Auto-generated method stub
		startListening();
	}
	
	   
    
}