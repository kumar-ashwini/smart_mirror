
package com.example.asr;


public interface IVoiceControl {
    public void processVoiceCommands(String... voiceCommands); 
    
    public void restartListeningService(); 
    public void processError(int error);
}

