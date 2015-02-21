package com.del.android.rgb;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import com.del.android.scope.R;
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
	    	if(mConnectedDeviceName!=null) mBTSerialService.write(buff);
	    	else  Toast.makeText(getApplicationContext(), R.string.title_not_connected,Toast.LENGTH_SHORT).show();
	    	
			if(D) 
			{
				for( int i = 0; i < FRAME_SIZE; i++ )
					Log.d(TAG, i+": "+buff[i] );
			}
		}
	};
	
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Name of the connected device
    private String mConnectedDeviceName = null;
    
    private final Handler mHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg)
        {
        	switch (msg.what)
        	{
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1)
                {
                case BluetoothSerialService.STATE_CONNECTED:
                    mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    break;
                case BluetoothSerialService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothSerialService.STATE_NONE:
                    mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(),getText(R.string.title_connected_to)
                		+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    private void setup()
    {
        Log.d(TAG, "setup()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTSerialService = new BluetoothSerialService(this, mHandler);     
        
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) 
        {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mBTSerialService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK)
            { 
                setup();
            } 
            else 
            {// User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    
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
        if (mBTSerialService != null) mBTSerialService.stop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
       
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }     
}
