package com.example.smartmirror;

import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;

public class TempPreferences {
SharedPreferences prefs;
HashMap<String,String> hm;

     public TempPreferences(Activity activity)
     {
	prefs= activity.getSharedPreferences("MyPrefs", Activity.MODE_PRIVATE);
	
     }
     
     public HashMap<String,String> getTemp()
     {
    	 hm= new HashMap<String, String>();
    	hm.put("Temp",  prefs.getString("temp", "No data"));
    	hm.put("Details",  prefs.getString("details","No data"));
    	hm.put("Icon",  prefs.getString("icon", "No icon"));
    	 
    	 return hm;
     }
     
     public void setTemp(HashMap<String, String> hashmap)
     
     {
    	 SharedPreferences.Editor editor = prefs.edit();
    	 editor.putString("temp", hashmap.get("Temp"));
    	 editor.putString("details", hashmap.get("Details"));
    	 editor.putString("icon", hashmap.get("Icon"));
    	 editor.commit();
    	 
     }



}
