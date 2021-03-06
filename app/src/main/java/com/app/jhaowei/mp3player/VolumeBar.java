package com.app.jhaowei.mp3player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PaintFlagsDrawFilter;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.app.jhaowei.mp3player.EventHandler;

public class VolumeBar extends View implements View.OnTouchListener{
	private Paint paint;
	private AudioManager audioManager = null;
	private int fontSize = 50;
	private int horizontal;
	private int view_width, view_height;
	private int maxVolume = 15, currentVolume = 3;
	private boolean fromUser = false;
	
	@SuppressLint("ClickableViewAccessibility")
	public VolumeBar(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setTextSize(fontSize);
		paint.setTextAlign(Align.LEFT);
		
		horizontal = view_height = this.getHeight();
		
		setOnTouchListener(this);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		view_width  = w;
		view_height = h;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
		
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		
		paint.setColor(Color.parseColor("#000000"));
	    canvas.drawText("15", 1, fontSize, paint);   
	    canvas.drawText("0", 1, view_height - 10, paint);
	    
	    if(!fromUser){
	    	horizontal =  view_height - (currentVolume * (int)((float)view_height / 15.0));
	    }
	    else {
	    	currentVolume =  (int)((float)(view_height - horizontal) / ((float)view_height / 15.0));
	    }
	    
	    if(currentVolume == 15)
    		horizontal = 0;
	    else if(currentVolume == 0)
	    	horizontal = view_height;
	    
    	canvas.drawLine(0, horizontal, view_width, horizontal, paint);
	    
	    if(horizontal <= view_height / 2) 
	    	canvas.drawText(currentVolume + "", 1, horizontal + fontSize, paint);
	    else
	    	canvas.drawText(currentVolume + "", 1, horizontal - 10, paint);
	    
	    paint.setColor(Color.parseColor("#55AA0000"));
	    canvas.drawRect(0, horizontal, view_width, view_height, paint);

	    if(audioManager != null)
	    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			horizontal = clamp((int) event.getY());
			fromUser = true;
			invalidate();
			EventHandler.volumeHandler.removeCallbacks(EventHandler.volume_runnable);
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			horizontal = clamp((int) event.getY());
			fromUser = true;
			invalidate();
			EventHandler.volumeHandler.removeCallbacks(EventHandler.volume_runnable);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP) {
			fromUser = false;
			EventHandler.volumeHandler.removeCallbacks(EventHandler.volume_runnable);
			EventHandler.volumeHandler.postDelayed(EventHandler.volume_runnable, 2000);
		}
		
		return true;
	}
	
	private int clamp(int n) {
		if(n >= view_height)
			n = view_height;
		else if(n <= 0) 
			n = 0;
		
		return n;
	}
	
	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
	}
	
	public void setVolume(int currentVolume) {
		this.currentVolume = currentVolume;
		horizontal =  view_height - this.currentVolume * (int)((float)view_height / (float)maxVolume);
		invalidate();
	}
	
	public void setMaxVolume(int maxVolume) {
		this.maxVolume = maxVolume;
		invalidate();
	}
}
