package com.appazal.scribble;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class ScribbleService extends Service{

	WindowManager windowManager;
	WindowManager.LayoutParams layoutParams;

	View inputLayout;
	ImageView inputImage;
	View inputBar;
	EditText textField;
	ImageView copyText, close;
//	Service self;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("", "OnCREATE SERvice");
//		self = this;
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inputLayout = layoutInflater.inflate(R.layout.input_layout, null);
		inputImage = (ImageView) inputLayout.findViewById(R.id.input_image);
		inputBar = inputLayout.findViewById(R.id.input_bar);
		textField = (EditText) inputLayout.findViewById(R.id.textField);
		copyText = (ImageView) inputLayout.findViewById(R.id.copyText);
		close = (ImageView) inputLayout.findViewById(R.id.close);
		
		textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Log.d("", "Has focus");
//                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    Log.d("", "Lost focus");
                }
            }
        });
		
		inputBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("", "Touched ");
//				v.performClick();
				return false;
			}

		});
		
		inputImage.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("", "Image Touched ");
				if(inputBar.getVisibility() == View.GONE) {
					inputBar.setVisibility(View.VISIBLE);
					layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
					windowManager.updateViewLayout(inputLayout, layoutParams);
				}
				else {
					inputBar.setVisibility(View.GONE);
					layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
							| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
					windowManager.updateViewLayout(inputLayout, layoutParams);
				}
				return false;
			}
		});
		
		copyText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("", "TextCopied");
				return false;
			}
		});
		
		close.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("", "Closed");
				stopSelf();
				return false;
			}
		});
		
		layoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		
		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		layoutParams.x = 0 ; layoutParams.y= 100;
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		
		windowManager.addView(inputLayout, layoutParams);
        inputBar.setVisibility(View.GONE);
	}
	
	@Override
	public void onDestroy() {
		if(inputLayout != null) windowManager.removeView(inputLayout);
		super.onDestroy();
	}
}
