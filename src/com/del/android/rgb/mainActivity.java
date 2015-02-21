package com.del.android.rgb;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
import android.widget.Toast;
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
    ToggleButton powerButton;
    
    private BluetoothAdapter mBluetoothAdapter = null;
 // Member object for the BT services
    private BluetoothSerialService mBTSerialService = null;
    
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
		
        powerButton = (ToggleButton) findViewById(R.id.powerButton);
        
        redEditText = (EditText) findViewById(R.id.redValueTextEdit);
        redEditText.setOnEditorActionListener(redEditTextListener);
        
        greenEditText = (EditText) findViewById(R.id.greenValueTextEdit);
        greenEditText.setOnEditorActionListener(greenEditTextListener);
        
        blueEditText = (EditText) findViewById(R.id.blueValueTextEdit);
        blueEditText.setOnEditorActionListener(blueEditTextListener);
   
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(sendButtonListener);
        
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) 
        {
            Toast.makeText(this, R.string.bt_not_available, Toast.LENGTH_LONG).show();
            if(D) Log.e(TAG, "Bluetooth is not available");
            finish();
            return;
        }
        
        if(D) Log.e(TAG, "-- ON CREATE --");
    }
    
    public class OnColorChangedCh1Class implements OnColorChangedListener
    {
    	public void onColorChanged(int color)
		{    		
    		Log.e( TAG, "1:"+ Color.red( color ) + "x"+ Color.green( color ) + "x"+ Color.blue( color ) /*+ ""+ Color.alpha( color ) */);
    		updateColorEditText(color);
		}
    }
    OnColorChangedCh1Class OnColorChangedCh1 = new OnColorChangedCh1Class();
    
    public class OnColorChangedCh2Class implements OnColorChangedListener
    {
    	public void onColorChanged(int color)
		{    		
			Log.e( TAG, "2:"+ Color.red( color ) + "x"+ Color.green( color ) + "x"+ Color.blue( color ) /*+ ""+ Color.alpha( color ) */);
			updateColorEditText(color);
    		
		}
    }
    OnColorChangedCh2Class OnColorChangedCh2 = new OnColorChangedCh2Class();
    
    private void updateColorEditText( int color)
    {
    	int pickedColor = picker.getCenterColor();
    	redEditText.setText( String.valueOf( Color.red(pickedColor) ) );
    	greenEditText.setText( String.valueOf( Color.green(pickedColor) ) );
    	blueEditText.setText( String.valueOf( Color.blue(pickedColor) ) );
    }
    
    private OnClickListener ch1ButtonListener = new OnClickListener()
	{
		public void onClick(View arg0) 
		{
			ToggleButton ch1Button = (ToggleButton) arg0;
			if(D) Log.d(TAG,"ch1ButtonListener"+ ch1Button.isChecked());
			
			if( picker.getActiveChannel() == channels.CH1 )
			{
				picker.setActiveChannel(channels.CH2);
				int color = picker.getCenterColor();
				picker.setColor( color );
	    		updateColorEditText( color );
				ch1Button.setText( R.string.ch2String );
			}
			else
			{
				picker.setActiveChannel(channels.CH1);
				int color = picker.getCenterColor();
				picker.setColor( color );
	    		updateColorEditText( color );
				ch1Button.setText( R.string.ch1String );
			}
		}
	};
	
	private OnEditorActionListener redEditTextListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(D) Log.d(TAG,"redEditTextListener"+ redEditText.getText() );
			 int centerCol = picker.getCenterColor();
			 int modifiedCol = Integer.valueOf( redEditText.getText().toString() );
			 if( modifiedCol > 255 )
			 {
				 modifiedCol = 255;
				 redEditText.setText("255");
			 }
			 else if( modifiedCol < 0 )
			 {
				 modifiedCol = 0;
				 redEditText.setText("0");
			 }
			 picker.setColor( Color.rgb( modifiedCol, Color.green(centerCol), Color.blue(centerCol)));
			return true;
		}
	};
	private OnEditorActionListener greenEditTextListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(D) Log.d(TAG,"greenEditTextListener"+ greenEditText.getText() );
			 int centerCol = picker.getCenterColor();
			 int modifiedCol = Integer.valueOf( greenEditText.getText().toString() );
			 if( modifiedCol > 255 )
			 {
				 modifiedCol = 255;
				 greenEditText.setText("255");
			 }
			 else if( modifiedCol < 0 )
			 {
				 modifiedCol = 0;
				 greenEditText.setText("0");
			 }
			 picker.setColor( Color.rgb( Color.red(centerCol), modifiedCol, Color.blue(centerCol)));
			return true;
		}
	};
	private OnEditorActionListener blueEditTextListener = new OnEditorActionListener()
	{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(D) Log.d(TAG,"blueEditTextListener"+ blueEditText.getText() );
			 int centerCol = picker.getCenterColor();
			 int modifiedCol = Integer.valueOf( blueEditText.getText().toString() );
			 if( modifiedCol > 255 )
			 {
				 modifiedCol = 255;
				 blueEditText.setText("255");
			 }
			 else if( modifiedCol < 0 )
			 {
				 modifiedCol = 0;
				 blueEditText.setText("0");
			 }
			 picker.setColor( Color.rgb( Color.red(centerCol), Color.green(centerCol), modifiedCol));
			return true;
		}
	};
	
	private final int FRAME_SIZE = 8;
	private byte buff[] = new byte[FRAME_SIZE];
	private final byte FRAME_START_MARKER = 1;
	private final byte FRAME_END_MARKER = FRAME_START_MARKER + 2;
	
	private void buildTxFrame()
	{
		int ch1Color = picker.getCh1CenterColor();
		int ch2Color = picker.getCh2CenterColor();
		
		if( ! powerButton.isChecked() )
		{
			ch1Color = 0;
			ch2Color = 0;
		}
		
		buff[0] = FRAME_START_MARKER;
		buff[1] = (byte) Color.red( ch1Color );
		buff[2] = (byte) Color.green( ch1Color );
		buff[3] = (byte) Color.blue( ch1Color );
		buff[4] = (byte) Color.red( ch2Color );
		buff[5] = (byte) Color.green( ch2Color );
		buff[6] = (byte) Color.blue( ch2Color );
		buff[7] = FRAME_END_MARKER;
	}
	
	private OnClickListener sendButtonListener = new OnClickListener()
	{
		public void onClick(View arg0)
		{
			if(D) Log.d(TAG, "sendButtonListener" );
			buildTxFrame();
			if(D) 
			{
				for( int i = 0; i < FRAME_SIZE; i++ )
					Log.d(TAG, i+": "+buff[i] );
			}
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
        
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the session
        } 
        else
        {
           if (mBTSerialService == null) setup();
        }
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
