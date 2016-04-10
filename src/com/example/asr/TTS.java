package com.example.asr;

import java.util.HashMap;
import java.util.Locale;

import org.w3c.dom.ls.LSInput;

import android.content.Context;
import android.speech.tts.*;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;


public class TTS extends UtteranceProgressListener implements OnInitListener{
	

	private TextToSpeech myTTS;		
	private static TTS singleton;	
	
	private static final String LOGTAG = "TTS";
	private UtteranceFinishedListener listener;
	
	
	private TTS(Context ctx, UtteranceFinishedListener listener) {
			myTTS = new TextToSpeech(ctx,(OnInitListener) this);
		this.listener=listener;
	}

	
	public static TTS getInstance(Context ctx,UtteranceFinishedListener listener){
		if(singleton==null){
			singleton = new TTS(ctx,listener);
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
		HashMap<String,String> map=new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "121");
		myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, map);  		
	}
	
	
	public void speak(String text, String languageCode) throws Exception{
		setLocale(languageCode);
		HashMap<String,String> map=new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "121");
		myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, map); 
		//myTTS.speak(text, TextToSpeech.QUEUE_ADD, null); 		
	}
	
	
	public void speak(String text){
		setLocale();
		HashMap<String,String> map=new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "121");
		myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, map); 		
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
	
	
	@Override
	public void onInit(int status) {
		if(status != TextToSpeech.ERROR){
			setLocale();
			 myTTS.setOnUtteranceProgressListener(this);
	    }
		else
		{
			Log.e(LOGTAG, "Error creating the TTS");
		}
				
	}


	@Override
	public void onDone(String utteranceId) {
		// TODO Auto-generated method stub
		Log.d("TTS", "onUtteranceFinished: utteranceId is "+utteranceId);
		this.listener.onUtteranceFinished(utteranceId);
	}


	@Override
	@Deprecated
	public void onError(String utteranceId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStart(String utteranceId) {
		// TODO Auto-generated method stub
		
	}
 
	
	public interface UtteranceFinishedListener
	{
		public void onUtteranceFinished(String utteranceId);
	}

	
	
	

}