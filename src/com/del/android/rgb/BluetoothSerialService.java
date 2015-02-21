package com.del.android.rgb;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;




import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothSerialService 
{
	private static final String TAG = "ARMscope";
    private static final boolean D = false;
    // Unique UUID for this application
    private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private int mState;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    public boolean block;
    
    
	public BluetoothSerialService(Context context, Handler handler) 
	{
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }
	
	 public synchronized int getState() 
	 {
	        return mState;
	 }

	    /**
	     * Start the chat service. Specifically start AcceptThread to begin a
	     * session in listening (server) mode. Called by the Activity onResume() */
	 public synchronized void start() 
	 {
	        if (D) Log.d(TAG, "start");

	        // Cancel any thread attempting to make a connection
	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	        setState(STATE_NONE);
	 }
	
	public synchronized void connect(BluetoothDevice device) 
	{
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
	
	 private class ConnectedThread extends Thread 
	 {
		 	private final BluetoothSocket mmSocket;
	        private final InputStream mmInStream;
	        private final OutputStream mmOutStream;

	        public ConnectedThread(BluetoothSocket socket)
	        {
	            Log.d(TAG, "create ConnectedThread");
	            mmSocket = socket;
	            InputStream tmpIn = null;
	            OutputStream tmpOut = null;

	            // Get the BluetoothSocket input and output streams
	            try {
	                tmpIn = socket.getInputStream();
	                tmpOut = socket.getOutputStream();
	            } catch (IOException e) {
	                Log.e(TAG, "temp sockets not created", e);
	            }

	            mmInStream = tmpIn;
	            mmOutStream = tmpOut;
	        }

	        public void run() 
	        {
	            if (D)Log.i(TAG, "BEGIN mConnectedThread");

	            byte[] buffer = new byte[256];
	            int bytes;
	           
	            // Keep listening to the InputStream while connected
	            while (true) 
	            {
	                try 
	                {
	                    // Read from the InputStream
	                	while(block){};
	                    bytes = mmInStream.read(buffer);
	                    block=true;
	                   
	                    //if(D) Log.i(TAG, "bytes" + bytes+ " odebrane:" + odebranych);
	                    //odebranych=odebranych+bytes;	
	                    // Send the obtained bytes to the UI Activity
	                    mHandler.obtainMessage(mainActivity.MESSAGE_READ, bytes,-1, buffer).sendToTarget();
	                } catch (IOException e) {
	                	if (D)Log.e(TAG, "disconnected", e);
	                    connectionLost();
	                    break;
	                }
	            }
	        }

	        public void write(byte[] buffer) {
	            try {
	                mmOutStream.write(buffer);
	                // Share the sent message back to the UI Activity
	               // mHandler.obtainMessage(mainActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
	            } catch (IOException e) {
	            	if (D)Log.e(TAG, "Exception during write", e);
	            }
	        }

	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) {
	            	if (D)Log.e(TAG, "close() of connect socket failed", e);
	            }
	        }
	 }
	 
	 private class ConnectThread extends Thread 
	 {
	        private final BluetoothSocket mmSocket;
	        private final BluetoothDevice mmDevice;

	        public ConnectThread(BluetoothDevice device) 
	        {
	            mmDevice = device;
	            BluetoothSocket tmp = null;
	            // Get a BluetoothSocket for a connection with the
	            // given BluetoothDevice
	            try {
	                tmp = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
	            } catch (IOException e) 
	            {
	                Log.e(TAG, "create() failed", e);
	            }
	            mmSocket = tmp;
	        }

	        public void run() {
	            Log.i(TAG, "BEGIN mConnectThread");
	            setName("ConnectThread");

	            // Always cancel discovery because it will slow down a connection
	            mAdapter.cancelDiscovery();

	            // Make a connection to the BluetoothSocket
	            try 
	            {
	                // This is a blocking call and will only return on a
	                // successful connection or an exception
	            	if(D) Log.i(TAG, "mmSocket.connect");
	                mmSocket.connect();
	            } catch (IOException e)
	            {
	                connectionFailed();
	                // Close the socket
	                try {
	                	 if (D) Log.d(TAG, "mmSocket.close() in ConnectThread");
	                    mmSocket.close();
	                } catch (IOException e2) {
	                    Log.e(TAG, "unable to close() socket during connection failure", e2);
	                }
	                // Start the service over to restart listening mode
	                BluetoothSerialService.this.start();
	                return;
	            }

	            // Reset the ConnectThread because we're done
	            synchronized (BluetoothSerialService.this)
	            {
	                mConnectThread = null;
	                if(D) Log.i(TAG, "mConnectThread=null");
	            }

	            // Start the connected thread
	            connected(mmSocket, mmDevice);
	        }

	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) {
	                Log.e(TAG, "close() of connect socket failed", e);
	            }
	        }
	 }
	
	 public synchronized void stop()
	 {
	        if (D) Log.d(TAG, "stop");
	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
	        setState(STATE_NONE);
	 }
	 
	 private synchronized void setState(int state) 
	 {
	        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
	        mState = state;
	        // Give the new state to the Handler so the UI Activity can update
	     	        
	        mHandler.obtainMessage(mainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	 }
	 
	 private void connectionFailed() 
	 {
	        setState(STATE_NONE);
	        if (D) Log.d(TAG, "connectionFailed()");

	        // Send a failure message back to the Activity
	        Message msg = mHandler.obtainMessage(mainActivity.MESSAGE_TOAST);
	        Bundle bundle = new Bundle();
	        bundle.putString(mainActivity.TOAST,"Unable to connect device");
	        msg.setData(bundle);
	        mHandler.sendMessage(msg);
	    }

	    /**
	     * Indicate that the connection was lost and notify the UI Activity.
	     */
	    private void connectionLost()
	    {
	        setState(STATE_NONE);
	        if(D) Log.i(TAG, "connectionLost()");
            
	        // Send a failure message back to the Activity
	       Message msg = mHandler.obtainMessage(mainActivity.MESSAGE_TOAST);
	        Bundle bundle = new Bundle();
	        bundle.putString(mainActivity.TOAST, "Connection Lost");
	        msg.setData(bundle);
	        mHandler.sendMessage(msg);
	    }
	    
	    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
	    {
	        if (D) Log.d(TAG, "connected()");

	        // Cancel the thread that completed the connection
	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	        // Start the thread to manage the connection and perform transmissions
	        mConnectedThread = new ConnectedThread(socket);
	        mConnectedThread.start();

	        // Send the name of the connected device back to the UI Activity
	        Message msg = mHandler.obtainMessage(mainActivity.MESSAGE_DEVICE_NAME);
	        Bundle bundle = new Bundle();
	        bundle.putString(mainActivity.DEVICE_NAME, device.getName());
	        msg.setData(bundle);
	        mHandler.sendMessage(msg);
	        if(D) Log.i(TAG, "connected().288 with " + device.getName());
            
	        setState(STATE_CONNECTED);
	    }
	    
	    public void write(byte[] out) {
	        // Create temporary object
	        ConnectedThread r;
	        // Synchronize a copy of the ConnectedThread
	        synchronized (this)
	        {
	            if (mState != STATE_CONNECTED) return;
	            r = mConnectedThread;
	        }
	        // Perform the write unsynchronized
	        r.write(out);
	    }

}
