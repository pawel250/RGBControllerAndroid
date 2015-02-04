package com.del.android.rgb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class Draw extends View 
{

	private static final String TAG = "ARMscope";
	private static final boolean D = false;
	private Paint mPaint;
	public Bitmap mBitmap;
	private Canvas mCanvas;
	public Handler mainActivityHandler;
	    
    private int curW = 100;
	private int curH = 100;
	
	private final int TOUCH_TIME_INTERVAL;
	private final int SCALE_PIXELS; // ilosc pixeli miedzy dzialkami
	private int groundLevel;
	
	private boolean antiAliasing;
	
	
	private float eventTime = -1;
	private float oldXCord = -1;
	private float oldYCord = -1;

	 
	public Draw(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		Log.e("TouchPaint", "Draw");
		//mContext = context;
		mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setARGB(255, 255, 255, 255);
        SCALE_PIXELS=30;
       // points=true;
        groundLevel=150;
       
        //kolor=0;
        TOUCH_TIME_INTERVAL = 50;
        
	}
	
	private void DrawScaleView() //rysowanie siatki i kasowanie starego wykresu
	{		
		if(D) Log.e(TAG, "DrawScaleView");
		mPaint.setAntiAlias(false);
		mPaint.setARGB(255,255,255,255);
		mCanvas.drawRect(0,0,curW,curH,mPaint);	 //kasowanie wykresu
		invalidate();
		
		final int skok = 5;
		int r = 100,g= 100,b = 100;
		for(  g = 0; g < 255; g+=skok)
		{		
			
			for(  r = 0; r < 255; r+=skok)
			{
				for( b = 0; b < 255; b+=skok)
				{
				
					mPaint.setARGB(255,r,g,b);
					mCanvas.drawRect(r,b,r+skok,b+skok,mPaint);	 //kasowanie wykresu
					//mCanvas.drawPoint(g,b,mPaint);
				}
			}
			invalidate();
			/*try {
			    Thread.sleep(1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}*/
		}
		
		/*
		 * 
		mPaint.setAntiAlias(antiAliasing);
		  for(int i=SCALE_PIXELS;i<curH;i=i+SCALE_PIXELS)
	    {
			if((int)(i/SCALE_PIXELS)==(int)(curH/(2*SCALE_PIXELS))+1) //warunek rysowania osi
				mCanvas.drawLine(0,i,curW,i,mPaint);
	        else
	        	for(int k=0;k<curW;k=k+SCALE_PIXELS/10)
	        		mCanvas.drawPoint(k,i,mPaint);
	    }
		for(int i=SCALE_PIXELS;i<curW;i=i+SCALE_PIXELS)
	    {
			if((int)(i/SCALE_PIXELS)==(int)(curW/(2*SCALE_PIXELS))) //warunek rysowania osi
				mCanvas.drawLine(i,0,i,curH,mPaint);
	        else
	        	for(int k=0;k<curH;k=k+SCALE_PIXELS/10)
	        		mCanvas.drawPoint(i,k,mPaint);
	    }*/
		

	}
	
	public final Handler mHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg)
        {
        	 if(D) Log.i(TAG, "Message to Draw: what:" + msg.what);
        	switch (msg.what)
        	{
        	case 0:
        		//ConversionProbes((byte[]) msg.obj,msg.arg1);
                break;
        	case 1:
        		//DrawProbes();
        		break;
        	case 2:
        		break;
            }
        }
    };
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		 if (mBitmap != null) {
             canvas.drawBitmap(mBitmap, 0, 0, null);
         }
	}

	 @Override public boolean onTouchEvent(MotionEvent event)
     {
		
		if(D)Log.e(TAG,"getY="+event.getY()+"getSize="+event.getHistorySize()+"\ngroundLevel="+groundLevel); 

	
		if(eventTime == -1) 
		{
			Log.e(TAG,"NULL oldEvent");
			eventTime = event.getEventTime();
			oldXCord = event.getX();
			oldYCord = event.getY();
			return true;
		}
		else
		{
			if(event.getEventTime() - eventTime < TOUCH_TIME_INTERVAL)
			{
				Log.e(TAG,"event.getEventTime(): "+event.getEventTime());
				Log.e(TAG,"oldEvent.getEventTime(): "+eventTime);
				//groundLevel=groundLevel+(int)(oldYCord-event.getY());	
				// firstProbe=firstProbe+(int)(oldXCord-event.getX());
				// if(firstProbe<0)firstProbe=0;
				 //mHandler.obtainMessage(Draw.MESSAGE_UPDATE,-1, -1, -1).sendToTarget();
			}
			
			eventTime = event.getEventTime();
			oldXCord = event.getX();
			oldYCord = event.getY();
		}
		return true;
     }
	
	 
	 @Override protected void onSizeChanged(int w, int h, int oldw,int oldh) 
     {
		 curW = mBitmap != null ? mBitmap.getWidth() : 0;
         curH = mBitmap != null ? mBitmap.getHeight() : 0;
         if (curW >= w && curH >= h) {
             return;
         }
         
         if (curW < w) curW = w;
         if (curH < h) curH = h;
         
         Bitmap newBitmap = Bitmap.createBitmap(curW,curH,Bitmap.Config.RGB_565);
         
         Canvas newCanvas = new Canvas();
         newCanvas.setBitmap(newBitmap);
         if (mBitmap != null) newCanvas.drawBitmap(mBitmap, 0, 0, null);
         
         mBitmap = newBitmap;
         mCanvas = newCanvas;   
         
         DrawScaleView(); 
     }
}