package com.example.asr;

import java.util.Locale;
import android.content.Context;
import android.speech.tts.*;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;


public class TTS implements OnInitListener,OnUtteranceCompletedListener{
	

	private TextToSpeech myTTS;		
	private static TTS singleton;	
	
	private static final String LOGTAG = "TTS";
	private TTS.OnUtteranceCompletedListener listener;
	
	
	private TTS(Context ctx) {
			myTTS = new TextToSpeech(ctx,(OnInitListener) this);
		this.listener=(OnUtteranceCompletedListener) ctx;
	}

	
	public static TTS getInstance(Context ctx){
		if(singleton==null){
			singleton = new TTS(ctx);
		}
		
		return singleton;
	}
	
	
	public void setLocale(String languageCode, String countryCode) throws Exception{
	    if(languageCode==null)
	    {
	    	setLocale();
	    	throw new Exception("Language code was not provided, using default locale");
	    }
	    else{
	    	if(countryCode==null)
	    		setLocale(languageCode);
	    	else {
	    		Locale lang = new Locale(languageCode, countryCode);
		    	if (myTTS.isLanguageAvailable(lang) == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE )
		    		myTTS.setLanguage(lang);
		    	{
		    		setLocale();
		    		throw new Exception("Language or country code not supported, using default locale");
		    	}
	    	}
	    }
	}
	
	
	public void setLocale(String languageCode) throws Exception{
		if(languageCode==null)
		{
			setLocale();
			throw new Exception("Language code was not provided, using default locale");
		}
		else {
			Locale lang = new Locale(languageCode);
			if (myTTS.isLanguageAvailable(lang) != TextToSpeech.LANG_MISSING_DATA && myTTS.isLanguageAvailable(lang) != TextToSpeech.LANG_NOT_SUPPORTED)
				myTTS.setLanguage(lang);
			else
			{
				setLocale();
				throw new Exception("Language code not supported, using default locale");
			}
		}
	}

	
	public void setLocale(){
		myTTS.setLanguage(Locale.getDefault());
	}
	
	
	public void speak(String text, String languageCode, String countryCode) throws Exception{
		setLocale(languageCode, countryCode);
		myTTS.speak(text, TextToSpeech.QUEUE_ADD, null); 		
	}
	
	
	public void speak(String text, String languageCode) throws Exception{
		setLocale(languageCode);
		myTTS.speak(text, TextToSpeech.QUEUE_ADD, null); 		
	}
	
	
	public void speak(String text){
		setLocale();
		myTTS.speak(text, TextToSpeech.QUEUE_ADD, null); 		
	}
	
	
	public void stop(){
		if(myTTS.isSpeaking())
			myTTS.stop();
	}
	
	
	public void shutdown(){
		myTTS.stop();
		myTTS.shutdown();
		singleton=null;		
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void onInit(int status) {
		if(status != TextToSpeech.ERROR){
			setLocale();
	    }
		else
		{
			Log.e(LOGTAG, "Error creating the TTS");
		}
		if(status == TextToSpeech.SUCCESS) {
	        myTTS.setOnUtteranceCompletedListener(this);
	    }
		
	}


	@Override
	public void onUtteranceCompleted(String arg0) {
		// TODO Auto-generated method stub
		this.listener.onUtterenceComplete(arg0);
	}
	
	public interface OnUtteranceCompletedListener
	{
		public void onUtterenceComplete(String str);
	}

}