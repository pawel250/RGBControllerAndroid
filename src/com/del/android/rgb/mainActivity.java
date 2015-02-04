package com.del.android.rgb;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.del.android.rgb.Draw;
//import com.del.android.scope.R;


public class mainActivity extends Activity
{
	//Debugging
	private static final String TAG = "RGBController";
	private static final boolean D = false;
	
    // Layout Views
    private TextView mTitle;
	private Draw mDraw; 
    private Handler HandlerDraw; //handler do komunikacji z klasa draw
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
       
        // Set up the window layout
      requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
      setContentView(R.layout.main);
         
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
      
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
              WindowManager.LayoutParams.FLAG_FULLSCREEN); 
    
        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
        
        mDraw = (Draw) findViewById(R.id.draw);
        HandlerDraw=mDraw.mHandler;
        mDraw.mainActivityHandler=mHandler;
           
        
        if(D) Log.e(TAG, "-- ON CREATE --");
    }
    
    private final Handler mHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg)
        {
        	switch (msg.what)
        	{
                
            case 0:
                // save the connected device's name
        
                break;
            case 1:
                
                break;
            case 2:
            	 if(D) Log.i(TAG, "MESSAGE_PARAM_CHANGED: " + msg.arg1);
                 
            	break;
            }
        }
    };
    
    public void onStart()
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        
    }

    
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
       
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }     
}
