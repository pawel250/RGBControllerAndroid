package com.del.android.rgb;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

import com.larswerkman.holocolorpicker.*;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.ColorPicker.channels;


public class mainActivity extends Activity
{
	//Debugging
	private static final String TAG = "RGBController";
	private static final boolean D = true;
	
    // Layout Views
    private TextView mTitle;
    
    private ColorPicker picker;
    
    EditText redEditText;
    EditText greenEditText;
    EditText blueEditText;
    
    Button sendButton;

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
        
        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);

        picker.addSVBar(svBar);

        picker.setOnColorChangedCh1Listener( OnColorChangedCh1 );
        picker.setOnColorChangedCh2Listener( OnColorChangedCh2 );

        //to turn of showing the old color
        picker.setShowOldCenterColor(true);

        ToggleButton ch1Button = (ToggleButton) findViewById(R.id.ch1Button);
        ch1Button.setOnClickListener(ch1ButtonListener);
		if( picker.getActiveChannel() == channels.CH1 )
		{
			ch1Button.setText( R.string.ch1String );
		}
		else
		{
			ch1Button.setText( R.string.ch2String );
		}
		
        ToggleButton powerButton = (ToggleButton) findViewById(R.id.powerButton);
        powerButton.setOnClickListener(powerButtonListener);
        
        redEditText = (EditText) findViewById(R.id.redValueTextEdit);
        redEditText.setOnEditorActionListener(redEditTextListener);
        
        greenEditText = (EditText) findViewById(R.id.greenValueTextEdit);
        greenEditText.setOnEditorActionListener(greenEditTextListener);
        
        blueEditText = (EditText) findViewById(R.id.blueValueTextEdit);
        blueEditText.setOnEditorActionListener(blueEditTextListener);
   
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);
        
        if(D) Log.e(TAG, "-- ON CREATE --");
    }
    
    public class OnColorChangedCh1Class implements OnColorChangedListener
    {
    	public void onColorChanged(int color)
		{
    		//int pickedColor = picker.getColor();
    		
    		Log.e( TAG, "1:"+ Color.red( color ) + "x"+ Color.green( color ) + "x"+ Color.blue( color ) /*+ ""+ Color.alpha( color ) */);
    		
		}
    }
    OnColorChangedCh1Class OnColorChangedCh1 = new OnColorChangedCh1Class();
    
    public class OnColorChangedCh2Class implements OnColorChangedListener
    {
    	public void onColorChanged(int color)
		{
    		//int pickedColor = picker.getColor();
    		
			Log.e( TAG, "2:"+ Color.red( color ) + "x"+ Color.green( color ) + "x"+ Color.blue( color ) /*+ ""+ Color.alpha( color ) */);
			//Log.d( TAG, "pickedColor2: " + pickedColor );
    		
		}
    }
    OnColorChangedCh2Class OnColorChangedCh2 = new OnColorChangedCh2Class();
    
    private OnClickListener powerButtonListener = new OnClickListener()
	{
		public void onClick(View arg0) 
		{
			ToggleButton powerButton = (ToggleButton) arg0;
			if(D) Log.d(TAG,"powerButtonListener"+ powerButton.isChecked());
			
			if( powerButton.isChecked() )
			{
				//picker.activeChannel = 2;
			}
			else
			{
				//picker.activeChannel = 1;
			}
		}
	};
    
    private OnClickListener ch1ButtonListener = new OnClickListener()
	{
		public void onClick(View arg0) 
		{
			ToggleButton ch1Button = (ToggleButton) arg0;
			if(D) Log.d(TAG,"ch1ButtonListener"+ ch1Button.isChecked());
			
			if( picker.getActiveChannel() == channels.CH1 )
			{
				picker.setActiveChannel(channels.CH2);
				picker.invalidate();
				picker.setColor( picker.getCenterColor() );
				//picker.setColor(-65000);
				ch1Button.setText( R.string.ch2String );
			}
			else
			{
				picker.setActiveChannel(channels.CH1);
				picker.invalidate();
				picker.setColor( picker.getCenterColor() );
				//picker.setColor(-25000);
				ch1Button.setText( R.string.ch1String );
			}
		}
	};
	
	private OnEditorActionListener redEditTextListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(D) Log.d(TAG,"redEditTextListener"+ redEditText.getText() );
			//picker.setCh1CenterColor( picker.getCh2Color() );
			return true;
		}
	};
	private OnEditorActionListener greenEditTextListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(D) Log.d(TAG,"greenEditTextListener"+ greenEditText.getText() );
			return true;
		}
	};
	private OnEditorActionListener blueEditTextListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(D) Log.d(TAG,"blueEditTextListener"+ blueEditText.getText() );
			return true;
		}
	};
	
	private OnClickListener sendButtonListener = new OnClickListener()
	{
		public void onClick(View arg0) 
		{
			if(D) Log.d(TAG,"sendButtonListener" );
		}
	};
    
    private final static Handler mHandler = new Handler() 
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
