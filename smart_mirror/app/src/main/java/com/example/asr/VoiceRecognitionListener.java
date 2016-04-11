package com.example.asr;


import java.util.ArrayList;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

public class VoiceRecognitionListener implements RecognitionListener {
 
 private static VoiceRecognitionListener instance = null;
 
 IVoiceControl listener; // This is running activity (initialize it later)
 
 public static VoiceRecognitionListener getInstance() {
  if (instance == null) {
   instance = new VoiceRecognitionListener();
  }
  return instance;
 }
 
 private VoiceRecognitionListener() { }
 
 public void setListener(IVoiceControl listener) {
        this.listener = listener;
    }
 
    public void processVoiceCommands(String... voiceCommands) {
        listener.processVoiceCommands(voiceCommands);
    }
 
    // This method will be executed when voice commands were found
 public void onResults(Bundle data) {
  ArrayList<String> matches = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
  String word =  matches.get(0);
  processVoiceCommands(word);
 }
 
 // User starts speaking
 public void onBeginningOfSpeech() {
  System.out.println("Starting to listen");
 }
 
 public void onBufferReceived(byte[] buffer) { }
 
 // User finished speaking
 public void onEndOfSpeech() {
  System.out.println("Waiting for result...");
 }
 
 // If the user said nothing the service will be restarted
 public void onError(int error) {
	 
  if (listener != null) {
   listener.restartListeningService();
  }
  listener.processError(error);
 }
 public void onEvent(int eventType, Bundle params) { }
 
 public void onPartialResults(Bundle partialResults) { }
 
 public void onReadyForSpeech(Bundle params) { }
 
 public void onRmsChanged(float rmsdB) { }
}
