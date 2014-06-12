/*
 * Copyright (C) 2014 Bizan Nishimura (@lipoyang)
 * 
 * This is forked from a Android SDK sample project 'BluetoothChat'
 */

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lipoyang.blueserial;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BlueSerial {

    // Debugging
    private static final String TAG = "BlueSerial";
    private static final boolean DEBUGGING = true;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    
    // parent activity
    Activity parent;
    
    // event listener
    private BlueSerialListener listener;
    
    // constructor
    public BlueSerial(Activity p)
    {
    	parent = p;
    	listener = (BlueSerialListener)p;
    }
    
    // start (called from onStart() of the Activity)
    public boolean start()
    {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(parent, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return false;
        }
  
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            parent.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) initCommunication();
        }
        
        return true;
    }
    
    // resume (called from onResume() of the Activity)
    public void resume()
    {
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }
    
    // stop (called from onDestroy() of the Activity)
    public void stop()
    {
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }
    
    // initialize the serial communication 
    private void initCommunication() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(parent, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    // The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothChatService.MESSAGE_STATE_CHANGE:
                if(DEBUGGING) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                	listener.onConneted(mConnectedDeviceName);
                	//mTitle.setText(R.string.title_connected_to);
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                	listener.onConneting();
                	//mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                	listener.onDisconneted();
                	//mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case BluetoothChatService.MESSAGE_WRITE:
//                byte[] writeBuf = (byte[]) msg.obj;
//                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
                break;
            case BluetoothChatService.MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
//                // construct a string from the valid bytes in the buffer
//                String readMessage = new String(readBuf, 0, msg.arg1);
                break;
            case BluetoothChatService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(BluetoothChatService.DEVICE_NAME);
                Toast.makeText(parent, "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case BluetoothChatService.MESSAGE_TOAST:
                Toast.makeText(parent, msg.getData().getString(BluetoothChatService.TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    // send a message
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
        	// Toast.makeText(parent, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }
    
    // open a Bluetooth device list (DeviceListActivity)
    public void openDeviceList()
    {
        Intent serverIntent = new Intent(parent, DeviceListActivity.class);
        // startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        parent.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }
    
    // get the result from the Bluetooth device list (DeviceListActivity)
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	boolean ret = true;
    	
    	switch (requestCode) {
    	case REQUEST_CONNECT_DEVICE_SECURE:
    		if (resultCode == Activity.RESULT_OK) {
    			connectDevice(data, true);
    		}
    		break;
    	case REQUEST_CONNECT_DEVICE_INSECURE:
    		if (resultCode == Activity.RESULT_OK) {
    			connectDevice(data, false);
    		}
    		break;
    	case REQUEST_ENABLE_BT:
    		if (resultCode == Activity.RESULT_OK) {
    			initCommunication();
    		} else {
    			// User did not enable Bluetooth or an error occured
    			Log.d(TAG, "BT not enabled");
    			Toast.makeText(parent, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
    			
    			ret = false;
    		}
    	}
    	return ret;
    }
    // connect to a Bluetooth device
    // this is called by onActivityResult()
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    } 
}
