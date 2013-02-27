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
	
	private BroadcastReceiver imageLoadedReceiver;
	private String url;
	private String key;
	
	public AIbexImageView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public void setUrl(String url) {
		if (url == null) {
			if (imageLoadedReceiver != null) {
				LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(imageLoadedReceiver);
				imageLoadedReceiver = null;
			}
		} else if ((getWidth() > 0 && getHeight() > 0) && this.url != url) {
			loadImage();
		}
		this.url = url;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setKeyAndUrl(String url, String key) {
		setKey(key);
		setUrl(url);
	}
	
	protected abstract ComponentName getServiceComponentName(Context context);

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > 0 && h > 0 && url != null) {
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
	
	private void loadImage() {
		if (url != null) {
			if (imageLoadedReceiver == null) {
				imageLoadedReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						Bitmap bmp = (Bitmap)intent.getParcelableExtra(AImageLoadingService.EXTRA_BITMAP);
						String loadedUrl = intent.getStringExtra(AImageLoadingService.EXTRA_URL);
						if (bmp.getWidth() == getWidth() && bmp.getHeight() == getHeight() && url != null && url.equals(loadedUrl)) {
							LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this);
							imageLoadedReceiver = null;
							setImageBitmap(bmp);
						}
					}
				};
				IntentFilter filter = new IntentFilter(AImageLoadingService.ACTION_IMAGE_LOADED);
				LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(imageLoadedReceiver, filter);
			}
			
			Context context = getContext();
			ImageMetadata metadata = new ImageMetadata(getWidth(), getHeight(), -1, -1, url, key, getScaleType());
			Intent intent = new Intent();
			intent.setComponent(getServiceComponentName(context));
			intent.putExtra(AImageLoadingService.EXTRA_METADATA, metadata);
			
			context.startService(intent);
		}
	}
}