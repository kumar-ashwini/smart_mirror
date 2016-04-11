package com.example.smartmirror;

import java.util.ArrayList;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ToDoActivity extends Activity {

	ListView lv;
  // ArrayList<String> arr;
  // ArrayList<String> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_to_do);
		if( Defaults.APPLICATION_ID.equals( "" ) || Defaults.SECRET_KEY.equals( "" ) || Defaults.VERSION.equals( "" ) )
	    {
	      showAlert( this, "Missing application ID and secret key arguments. Login to Backendless Console, select your app and get the ID and key from the Manage > App Settings screen. Copy/paste the values into the Backendless.initApp call" );
	      return;
	    }

	    Backendless.initApp( this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION );
	    
		
		
		
		lv= (ListView) findViewById(R.id.listView1);
		//arr=getTask();
		


		//getTask();
		
		
	}// 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//      TaskList taskList= new TaskList();
//      taskList.execute("Get");
		getTask();
	}
	
	public static void showAlert( final Activity context, String message )
	  {
	    new AlertDialog.Builder( context ).setTitle( "An error occurred" ).setMessage( message ).setPositiveButton( "OK", new DialogInterface.OnClickListener()
	    {
	      @Override
	      public void onClick( DialogInterface dialogInterface, int i )
	      {
	        context.finish();
	      }
	    } ).show();
	  }
	
	
	
	
/*private class TaskList extends AsyncTask<String, Integer, ArrayList<String>>
{
 ProgressDialog downloadingbox;
	@Override
	protected void onPreExecute() {
		
	    downloadingbox= new ProgressDialog(ToDoActivity.this);
		downloadingbox.setCancelable(true);
		downloadingbox.setTitle("Fetching List");
		downloadingbox.setMessage("please wait");
		downloadingbox.show();
		
	}
	
@Override
	protected ArrayList<String> doInBackground(String... params) {
		
		//list= new ArrayList<String>();
		TasksManager.findEntities( new AsyncCallback<List<Task>>()
			    {
			      @Override
			      public void handleResponse( List<Task> response )
			      {
			    	  int n=0;
			    	  Task t;
			    	  while(n<response.size())
			    	  {
			    		 t= response.get(n) ;
			    		// list.add(t.getTitle());
			    		 n++;
			    	  }
			           
			      }

			      @Override
			      public void handleFault( BackendlessFault fault )
			      {
			      }
			    } );
		
		//return list;
						
	}
	
	@Override
	protected void onPostExecute(ArrayList<String> result) {
	
		arr=result;
		
		downloadingbox.dismiss();
		
		CustomAdapter adapter= new CustomAdapter();
		lv.setAdapter(adapter);
		
		
	}

}//taskList*/

public class CustomAdapter extends BaseAdapter
{  private ArrayList<String> list;
 
    public CustomAdapter(ArrayList<String> list)
    {
    this.list=list;
    }
	@Override
	public int getCount() {
		
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.list_item, null);
		TextView textView = (TextView) view.findViewById(R.id.textView1);
		textView.setText(list.get(position));
		return view;
	}
	
}

public void getTask()
{
	 final ArrayList<String> list= new ArrayList<String>();
	TasksManager.findEntities( new AsyncCallback<List<Task>>()
		    {
		      @Override
		      public void handleResponse( List<Task> response )
		      {
		    	  int n=0;
		    	  Task t;
		    	  while(n<response.size())
		    	  {
		    		 t= response.get(n) ;
		    		 list.add(t.getTitle());
		    		 n++;
		    	  }
		           
		      }

		      @Override
		      public void handleFault( BackendlessFault fault )
		      {
		    	  Log.d("ToDoActivity", fault.toString());
		      }
		    } );
	
	
	Log.d("To-Do a", list.toString());
	//CustomAdapter adapter= new CustomAdapter(list);
	ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
	lv.setAdapter(adapter);
	
}


}
