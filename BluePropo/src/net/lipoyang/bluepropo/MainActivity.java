/*
 * Copyright (C) 2014 Bizan Nishimura (@lipoyang)
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

package net.lipoyang.bluepropo;

import net.lipoyang.blueserial.BlueSerial;
import net.lipoyang.blueserial.BlueSerialListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity  implements BlueSerialListener{

    // Debugging
    private static final String TAG = "BluePropo";
    private static final boolean DEBUGGING = true;
   
    // Propo View
    private PropoView propoView;
    // Bluetooth SPP module
    private BlueSerial blueSerial;
    
    // Bluetooth state
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    private int btState = STATE_DISCONNECTED;
    
    //***** onCreate, onStart, onResume, onPause, onStop, onDestroy
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DEBUGGING) Log.e(TAG, "++ ON CREATE ++");

        setContentView(R.layout.activity_main);
        propoView = (PropoView)findViewById(R.id.propoView1);
        propoView.setMainActivity(this);
       
        // create a BlueSerial.
        blueSerial = new BlueSerial(this);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if(DEBUGGING) Log.e(TAG, "++ ON START ++");
        
        // start the BlueSerial, or finish this application.
        if( !blueSerial.start()) {
        	finish();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(DEBUGGING) Log.e(TAG, "+ ON RESUME +");
        
        // resume the BlueSerial.
        blueSerial.resume();
    }
    
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(DEBUGGING) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(DEBUGGING) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        // stop the BlueSerial.
    	blueSerial.stop();
        
        super.onDestroy();
        if(DEBUGGING) Log.e(TAG, "--- ON DESTROY ---");
    }

    // onConneting, onConneted, onDisconneted  of BlueSerialLinster
    public void onConneting()
    {
    	btState = STATE_CONNECTING;
    	propoView.setBtState(btState);
    }
    public void onConneted(String devideName)
    {
    	btState = STATE_CONNECTED;
    	propoView.setBtState(btState);
    }
    public void onDisconneted()
    {
    	btState = STATE_DISCONNECTED;
    	propoView.setBtState(btState);
    }
    
    // On touch PropoView's Bluetooth Button
    public void onTouchBtButton()
    {
    	if(btState == STATE_DISCONNECTED){
	    	// open a Bluetooth device list.
	    	blueSerial.openDeviceList();
    	}
    }
    
    // get the result from a Bluetooth device list activity launched by onTouchBtButton()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(DEBUGGING) Log.d(TAG, "onActivityResult " + resultCode);
    	
    	// put the result to the BlueSerial.
    	// if failure, finish this application.
      	if( !blueSerial.onActivityResult(requestCode, resultCode, data))
      	{
      		finish();
      	}
    }
    
    // On touch PropoView's FB Stick
    // fb = -1.0 ... +1.0
    public void onTouchFbStick(float fb)
    {
    	// send "#Dxx$" where xx = 00 ... FF
    	// [7:2] voltage setting (cf. DRV8830 datasheet)
    	// [1:0] direction 00b:Standby, 01b:Reverse, 10b:Forward, 11b:Break
    	int dir, volt;
    	if(fb == 0.0){
    		dir = 0x00;
    		volt = 0x00;
    	}else if(fb > 0.0){
    		dir = 0x01;
    		volt = (int)(fb * 60.0F);
    	}else{
    		dir = 0x02;
    		volt = (int)(-fb * 60.0F);
    	}
    	int data = (volt << 2) | dir;
    	String command = "#D" +String.format("%02X", data) + "$";
    	
    	// send the BlueSerial a message.
    	blueSerial.sendMessage(command);
    }
    
    // On touch PropoView's LR Stick
    // lr = -1.0 ... +1.0
    public void onTouchLrStick(float lr)
    {
    	// send "#Bxxx$" where xxx = 000 ... 180 (decimal[degree])
    	int degree = (int)(-lr * 90.0F) + 90;
		if (degree > 180) degree = 180;
		if (degree < 0) degree = 0;
    	String command = "#B" +String.format("%03d", degree) + "$";

    	// send the BlueSerial a message.
    	blueSerial.sendMessage(command);
    }
    
}
