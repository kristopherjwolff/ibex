package com.forrestpangborn.ibex.sample;

import android.content.ComponentName;
import android.content.Context;
import android.util.AttributeSet;

import com.forrestpangborn.ibex.view.AIbexImageView;

public class SampleImageView extends AIbexImageView {
	
	private static ComponentName SERVICE;
	private String url;
	private boolean preventLoad;

	public SampleImageView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
		if (SERVICE == null) {
			SERVICE = new ComponentName(context, SampleImageLoadingService.class);
		}
	}
	
	public SampleImageView(Context context, AttributeSet set) {
		this(context, set, -1);
	}

	@Override
	protected ComponentName getServiceComponentName(Context context) {
		return SERVICE;
	}

	@Override
	protected int getMinWidth() {
		return Math.min(getWidth(), 500);
	}

	@Override
	protected int getMinHeight() {
		return Math.min(getHeight(), 500);
	}

	@Override
	protected String getUrl() {
		return url;
	}

	@Override
	protected String getKey() {
		return null;
	}
	
	@Override
	protected boolean isScalable() {
		return true;
	}
	
	@Override
	protected void loadImage() {
		if (!preventLoad) {
			super.loadImage();
		}
	}
	
	public void setPreventLoad(boolean flag) {
		this.preventLoad = flag;
	}
	
	public void setUrl(String value) {
		this.url = value;
		
		if (getWidth() > 0 && getHeight() > 0) {
			loadImage();
		}
	}
}