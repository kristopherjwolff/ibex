package com.forrestpangborn.ibex.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Response;
import com.forrestpangborn.ibex.service.ImageLoadingService;

public abstract class AIbexImageView extends ImageView {
	
	private Request previousRequest;
	private BroadcastReceiver imageLoadedReceiver;
	
	public AIbexImageView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public void clear() {
		previousRequest = null;
		setImageDrawable(null);
	}
	
	protected abstract ComponentName getServiceComponentName(Context context);
	protected abstract Request.Builder createRequestBuilder();

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > 0 && h > 0) {
			loadImage();
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		if (imageLoadedReceiver != null) {
			LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(imageLoadedReceiver);
		}
	}
	
	protected void loadImage() {
		Request request;
		
		try {
			request = createRequestBuilder().build();
		} catch (IllegalStateException ex) {
			Log.e("Ibex", "Error building request in AIbexImageView.");
			request = null;
		}
		
		if (request != null && !request.equals(previousRequest)) {
			if (previousRequest != null && 
					request != null && 
					request.getUniqueKey() != null &&
					!request.getUniqueKey().equals(previousRequest.getUniqueKey())) {
				setImageBitmap(null);	
			}
			
			Context context = getContext();
			if (previousRequest != null) {
				Intent intent = new Intent();
				intent.setComponent(getServiceComponentName(context));
				intent.setAction(ImageLoadingService.ACTION_CANCEL_REQUEST);
				intent.putExtra(ImageLoadingService.EXTRA_REQUEST, previousRequest);
				
				context.startService(intent);
				previousRequest = null;
			}
			
			if (imageLoadedReceiver == null) {
				imageLoadedReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						Response response = intent.getParcelableExtra(ImageLoadingService.EXTRA_RESPONSE);
						if (previousRequest != null && response.satisifies(previousRequest)) {
							LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this);
							imageLoadedReceiver = null;
							setImageBitmap(response.getBitmap());
						}
					}
				};
				IntentFilter filter = new IntentFilter(ImageLoadingService.ACTION_IMAGE_LOADED);
				LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(imageLoadedReceiver, filter);
			}
			
			previousRequest = request;
			
			Intent intent = new Intent();
			intent.setComponent(getServiceComponentName(context));
			intent.setAction(ImageLoadingService.ACTION_REQUEST_IMAGE);
			intent.putExtra(ImageLoadingService.EXTRA_REQUEST, request);
			
			context.startService(intent);
		}
	}
}