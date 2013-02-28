package com.forrestpangborn.ibex.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.forrestpangborn.ibex.service.AImageLoadingService;
import com.forrestpangborn.ibex.util.ImageMetadata;

public abstract class AIbexImageView extends ImageView {
	
	private String imageUrl;
	private BroadcastReceiver imageLoadedReceiver;
	
	public AIbexImageView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	protected abstract ComponentName getServiceComponentName(Context context);
	protected abstract int getMinWidth();
	protected abstract int getMinHeight();
	protected abstract String getUrl();
	protected abstract String getKey();

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
	
	protected ImageMetadata getImageMetadata() {
		return new ImageMetadata(getWidth(), getHeight(), getMinWidth(), getMinHeight(), getUrl(), getKey(), getScaleType());
	}
	
	protected void loadImage() {
		String url = getUrl();
		if (url != null && !url.equals(imageUrl)) {
			Context context = getContext();
			if (imageUrl != null) {
				Intent intent = new Intent();
				intent.setComponent(getServiceComponentName(context));
				intent.setAction(AImageLoadingService.ACTION_CANCEL_REQUEST);
				intent.putExtra(AImageLoadingService.EXTRA_METADATA, getImageMetadata());
				
				context.startService(intent);
				imageUrl = null;
			}
			
			if (imageLoadedReceiver == null) {
				imageLoadedReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						Bitmap bmp = (Bitmap)intent.getParcelableExtra(AImageLoadingService.EXTRA_BITMAP);
						String loadedUrl = intent.getStringExtra(AImageLoadingService.EXTRA_URL);
						if (loadedUrl != null &&  getUrl() != null && loadedUrl.equals(getUrl())) {
							LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this);
							imageLoadedReceiver = null;
							setImageBitmap(bmp);
						}
					}
				};
				IntentFilter filter = new IntentFilter(AImageLoadingService.ACTION_IMAGE_LOADED);
				LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(imageLoadedReceiver, filter);
			}
			
			Intent intent = new Intent();
			intent.setComponent(getServiceComponentName(context));
			intent.setAction(AImageLoadingService.ACTION_REQUEST_IMAGE);
			intent.putExtra(AImageLoadingService.EXTRA_METADATA, getImageMetadata());
			
			context.startService(intent);
			imageUrl = url;
		}
	}
}