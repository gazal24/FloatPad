package com.codeplay.floatpad;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatpadService extends Service{

	WindowManager windowManager;
	WindowManager.LayoutParams layoutParams;
	FloatPadViewHolder viewHolder;
	Double MOVE_THRESHOLD = 10.0d;

	View inputLayout;

	boolean inputLayoutMoved=false;

	public class FloatPadViewHolder {
		View inputBar;
		EditText textField;
		ImageView inputImage, copyText, close;

		public FloatPadViewHolder(View v){
			this.inputBar = v.findViewById(R.id.input_bar);
			this.textField = (EditText) v.findViewById(R.id.textField);
			this.inputImage = (ImageView) v.findViewById(R.id.input_image);
			this.copyText = (ImageView) v.findViewById(R.id.copyText);
			this.close = (ImageView) v.findViewById(R.id.close);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inputLayout = layoutInflater.inflate(R.layout.input_layout, null);
		viewHolder = new FloatPadViewHolder(inputLayout);

		viewHolder.textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					Log.d("", "Has focus");
					//((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
				} else {
					Log.d("", "Lost focus");
				}
			}
		});

		viewHolder.inputBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("", "Touched ");
				v.performClick();
				return false;
			}
		});

		viewHolder.inputBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					Log.i("input bar ", "Has Focus");
				} else {
					Log.i("input bar ", "Lost Focus");
				}
			}
		});

		viewHolder.inputImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(viewHolder.inputBar.getVisibility() == View.GONE) {
					viewHolder.inputBar.setVisibility(View.VISIBLE);
					layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
					windowManager.updateViewLayout(inputLayout, layoutParams);
				}
				else {
					viewHolder.inputBar.setVisibility(View.GONE);
					layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
							| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
					windowManager.updateViewLayout(inputLayout, layoutParams);
				}
			}
		});

		viewHolder.inputImage.setOnTouchListener(new OnTouchListener() {
			private int initialX, initialY; private float initialTouchX, initialTouchY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN :
					initialX = layoutParams.x;
					initialY = layoutParams.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return true;
				case MotionEvent.ACTION_UP :
					if(inputLayoutMoved)
						inputLayoutMoved = false;
					else
						v.performClick();
					return true;
				case MotionEvent.ACTION_MOVE :
					if(Math.abs(event.getRawX() - initialTouchX) > MOVE_THRESHOLD ||
							Math.abs(event.getRawY() - initialTouchY) > MOVE_THRESHOLD)
						inputLayoutMoved = true;
					layoutParams.x = initialX + (int)(event.getRawX() - initialTouchX);
					layoutParams.y = initialY + (int)(event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(inputLayout, layoutParams);
					return true;
				}
				return false;
			}
		});

		viewHolder.copyText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("", "TextCopied");
				ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
				clipboardManager.setPrimaryClip(ClipData.newPlainText("text", viewHolder.textField.getText().toString()));
				Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		viewHolder.close.setOnTouchListener(new OnTouchListener() {
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
		viewHolder.inputBar.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		if(inputLayout != null) windowManager.removeView(inputLayout);
		super.onDestroy();
	}
}
