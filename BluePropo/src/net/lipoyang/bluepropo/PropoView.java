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

//import java.util.HashMap;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.content.res.Resources;
import android.view.MotionEvent;

public class PropoView extends View {
	// screen size of the original design
	private final float W_SCREEN = 1184;
	private final float H_SCREEN = 720;
	// Bluetooth button size
	private float W_BT_BUTTON = 240;
	private float H_BT_BUTTON = 106;
	// Bluetooth button base point (left-top) 
	private float X_BT_BUTTON = W_SCREEN/2 - W_BT_BUTTON/2;
	private float Y_BT_BUTTON = 54;
	// F<->B bar radius and length of movement (half)
	private float R_FB_BAR = 42;
	private float L_FB_BAR = 173; //(range/2 - radius/2)
	// F<->B bar neutral point
	private float X_FB_BAR = 299;//296
	private float Y_FB_BAR = 377;
	// L<->R bar radius and length of movement (half)
	private float R_LR_BAR = 42;
	private float L_LR_BAR = 173; //(range/2 - radius/2)
	// L<->R bar neutral point
	private float X_LR_BAR = 897;//888
	private float Y_LR_BAR = 377;
	// margin of bar touch range
	private float MARGIN_BAR = 50;
	
	// touch points
	// -- SparseArray is faster than HashMap.
//  private HashMap<Integer, Point> points =new HashMap<Integer, Point>();
    private SparseArray<Point> points =new SparseArray<Point>();
    
    // ID of touch point
    public int fbID = -1;	// touch on F<->B bar
    public int lrID = -1;	// touch on L<->R bar
    public int btID = -1;	// touch on Bluetooth button
    
    // position of F<->B bar, L<->R bar
    private float fb_y;
    private float lr_x;

    // Bluetooth state
    private int btState = MainActivity.STATE_DISCONNECTED;
    
    // bitmap objects
	private Bitmap imgBar, imgDisconnected, imgConnecting, imgConnected;
	private Paint paint;
	
	// main activity
	private MainActivity mainActivity;
	
	// constructors
	public PropoView(Context context, AttributeSet attrs ) {
		super(context,attrs);
		init();
	}
	public PropoView(Context context) {
		super(context);
		init();
	}
	void init(){
		Resources res = this.getContext().getResources();
		imgDisconnected = BitmapFactory.decodeResource(res, R.drawable.disconnected);
		imgConnecting   = BitmapFactory.decodeResource(res, R.drawable.connecting);
		imgConnected    = BitmapFactory.decodeResource(res, R.drawable.connected);
		imgBar          = BitmapFactory.decodeResource(res, R.drawable.bar);
		paint = new Paint();
	}
	
	// set main activity
	public void setMainActivity(MainActivity ma) {
		mainActivity = ma;
		
		// get screen information
		WindowManager wm = (WindowManager)ma.getBaseContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		
		// scale factor
		float xScale = displayMetrics.widthPixels / W_SCREEN;
		float yScale = displayMetrics.heightPixels / H_SCREEN;

		// Bluetooth button size
		W_BT_BUTTON = imgDisconnected.getWidth();
		H_BT_BUTTON = imgDisconnected.getHeight();
		// Bluetooth button base point (left-top) 
		X_BT_BUTTON = displayMetrics.widthPixels/2 - W_BT_BUTTON/2;
		Y_BT_BUTTON = 54 * yScale;
		// F<->B bar radius and length of movement (half)
		R_FB_BAR = 42 * xScale;
		L_FB_BAR = 173 * yScale; //(range/2 - radius/2)
		// F<->B bar neutral point
		X_FB_BAR = 296 * xScale;
		Y_FB_BAR = 377 * yScale;
		// L<->R bar radius and length of movement (half)
		R_LR_BAR = 42 * yScale;
		L_LR_BAR = 173 * xScale; //(range/2 - radius/2)
		// L<->R bar neutral point
		X_LR_BAR = 888 * xScale;
		Y_LR_BAR = 377 * yScale;
		// margin of bar touch range
		MARGIN_BAR = 50 * xScale;
		
		// initial position of sticks
	    fb_y = Y_FB_BAR;
	    lr_x = X_LR_BAR;
	}
	
	// set Bluetooth State
	public void setBtState(int state)
	{
		btState = state;
		invalidate();	// redraw
	}
	
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		
		// draw bitmap objects
		
		// Bluetooth button
		switch(btState){
		case MainActivity.STATE_CONNECTING:
			c.drawBitmap(imgConnecting,X_BT_BUTTON,Y_BT_BUTTON,paint);
			break;
		case MainActivity.STATE_CONNECTED:
			c.drawBitmap(imgConnected,X_BT_BUTTON,Y_BT_BUTTON,paint);
			break;
		case MainActivity.STATE_DISCONNECTED:
		default:
			c.drawBitmap(imgDisconnected,X_BT_BUTTON,Y_BT_BUTTON,paint);
			break;
		}
		// F<->B bar
        c.drawBitmap(imgBar,X_FB_BAR - R_FB_BAR, fb_y     - R_FB_BAR, paint);
		// L<->R bar
        c.drawBitmap(imgBar,lr_x     - R_FB_BAR, Y_LR_BAR - R_FB_BAR, paint);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	// get touch informations
        int action = event.getAction();
        // int index = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int eventID = event.getPointerId(index);
        int touchCount = event.getPointerCount();
 
        switch ( action & MotionEvent.ACTION_MASK ) {
 
        // (1) on touch-down
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
        	
        	// get the touch point
            int tx =(int)event.getX(index);
            int ty =(int)event.getY(index);
            Point posTouch = new Point(tx, ty);
            
            if(posTouch != null) {
            	// (1.1) touch F<->B bar?
            	if(fbID == -1) {
                	if( (tx > (X_FB_BAR - R_FB_BAR - MARGIN_BAR)) &&
                		(tx < (X_FB_BAR + R_FB_BAR + MARGIN_BAR)) &&	
                    	(ty > (Y_FB_BAR - L_FB_BAR - R_FB_BAR - MARGIN_BAR*2)) &&
                        (ty < (Y_FB_BAR + L_FB_BAR + R_FB_BAR + MARGIN_BAR*2)))
                	{
                        fbID = eventID;
                        points.put(eventID, posTouch);
                        
                        // message to the main activity
                        //   F<->B value (-1.0 ... +1.0)
                        fb_y = ty;
                        if(fb_y < Y_FB_BAR - L_FB_BAR) fb_y = Y_FB_BAR - L_FB_BAR;
                        if(fb_y > Y_FB_BAR + L_FB_BAR) fb_y = Y_FB_BAR + L_FB_BAR;
                        float fb = -(fb_y - Y_FB_BAR) / L_FB_BAR;
                        mainActivity.onTouchFbStick(fb);
                	}
                }
            	// (1.2) touch L<->R bar?
                if(lrID == -1) {
                	if( (tx > (X_LR_BAR - L_LR_BAR - R_LR_BAR - MARGIN_BAR*2)) &&
                    	(tx < (X_LR_BAR + L_LR_BAR + R_LR_BAR + MARGIN_BAR*2)) &&	
                        (ty > (Y_LR_BAR - R_LR_BAR - MARGIN_BAR)) &&
                        (ty < (Y_LR_BAR + R_LR_BAR + MARGIN_BAR)))
                    {
                		lrID = eventID;
                        points.put(eventID, posTouch);

                        // message to the main activity
                        //   L<->R value (-1.0 ... +1.0)
                        lr_x = tx;
                        if(lr_x < X_LR_BAR - L_LR_BAR) lr_x = X_LR_BAR - L_LR_BAR;
                        if(lr_x > X_LR_BAR + L_LR_BAR) lr_x = X_LR_BAR + L_LR_BAR;
                        float lr = (lr_x - X_LR_BAR) / L_LR_BAR;
                        mainActivity.onTouchLrStick(lr);
                    }
                }
            	// (1.3) touch Bluetooth button?
            	if(btID == -1){
                	if( (tx >= (X_BT_BUTTON )) &&
                    	(tx <= (X_BT_BUTTON + W_BT_BUTTON)) &&	
                        (ty >= (Y_BT_BUTTON )) &&
                        (ty <= (Y_BT_BUTTON + H_BT_BUTTON)))
                	{
                		btID = eventID;
                        points.put(eventID, posTouch);
                	}
            	}
            }
            break;
        
        // (2) on touch-move
        case MotionEvent.ACTION_MOVE:
 
            for(index = 0; index < touchCount; index++) {
 
            	// get the touch point
                eventID = event.getPointerId(index);
                tx =(int)event.getX(index);
                ty =(int)event.getY(index);
                posTouch = new Point(tx, ty);
 
//              if(points.containsKey(eventID2)) {
                if(points.get(eventID) != null) {
                    if(posTouch != null) {
                    	
                    	// (2.1) move F<->B bar?
                    	if(eventID == fbID)
                    	{
                        	if( (tx > (X_FB_BAR - R_FB_BAR - MARGIN_BAR)) &&
                            	(tx < (X_FB_BAR + R_FB_BAR + MARGIN_BAR)) &&	
                               	(ty > (Y_FB_BAR - L_FB_BAR - R_FB_BAR - MARGIN_BAR*2)) &&
                                (ty < (Y_FB_BAR + L_FB_BAR + R_FB_BAR + MARGIN_BAR*2)))
                        	{
                                fb_y = ty;
                                if(fb_y < Y_FB_BAR - L_FB_BAR) fb_y = Y_FB_BAR - L_FB_BAR;
                                if(fb_y > Y_FB_BAR + L_FB_BAR) fb_y = Y_FB_BAR + L_FB_BAR;
                        		points.put(eventID, posTouch);
                            }else{
                            	eventID = -1;
                            	fb_y = Y_FB_BAR;
                            	points.remove(eventID);
                            }
                        	
                            // message to the main activity
                            //   F<->B value (-1.0 ... +1.0)
                            float fb = -(fb_y - Y_FB_BAR) / L_FB_BAR;
                            mainActivity.onTouchFbStick(fb);
                    	}
                    	// (2.2) move L<->R bar
                    	else if(eventID == lrID)
                    	{
                        	if( (tx > (X_LR_BAR - L_LR_BAR - R_LR_BAR - MARGIN_BAR*2)) &&
                               	(tx < (X_LR_BAR + L_LR_BAR + R_LR_BAR + MARGIN_BAR*2)) &&	
                                (ty > (Y_LR_BAR - R_LR_BAR - MARGIN_BAR)) &&
                                (ty < (Y_LR_BAR + R_LR_BAR + MARGIN_BAR)))
                           	{
                                lr_x = tx;
                                if(lr_x < X_LR_BAR - L_LR_BAR) lr_x = X_LR_BAR - L_LR_BAR;
                                if(lr_x > X_LR_BAR + L_LR_BAR) lr_x = X_LR_BAR + L_LR_BAR;
                           		points.put(eventID, posTouch);
                            }else{
                            	eventID = -1;
                            	lr_x = X_LR_BAR;
                               	points.remove(eventID);
                            }
                        	
                            // message to the main activity
                            //   L<->R value (-1.0 ... +1.0)
                            float lr = (lr_x - X_LR_BAR) / L_LR_BAR;
                            mainActivity.onTouchLrStick(lr);
                    	}
                    }
                }
            }
            break;
 
        // (3) on touch-up
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
 
//          if(points.containsKey(eventID)) {
            if(points.get(eventID) != null) {
            	
            	// (3.1) leave F<->B bar?
            	if(eventID == fbID) {
                	fb_y = Y_FB_BAR;
                    fbID = -1;
                    
                    // message to the main activity
                    //   L<->R value (-1.0 ... +1.0)
                    float fb = -(fb_y - Y_FB_BAR) / L_FB_BAR;
                    mainActivity.onTouchFbStick(fb);
                }
            	// (3.2) leave L<->R bar?
                else if(eventID == lrID) {
                	lr_x = X_LR_BAR;
                    lrID = -1;
                    
                    // message to the main activity
                    //   L<->R value (-1.0 ... +1.0)
                    float lr = (lr_x - X_LR_BAR) / L_LR_BAR;
                    mainActivity.onTouchLrStick(lr);
                }
            	// (3.3) leave Bluetooth button?
                else if(eventID == btID){
            		btID = -1;
            		
        			// message to the main activity
        			mainActivity.onTouchBtButton();
            	}
                points.remove(eventID);
            }
            break;
        }
        invalidate(); // redraw
        return true;
    }
}
