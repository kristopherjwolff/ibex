package com.forrestpangborn.ibex.sample;

import android.content.ComponentName;
import android.content.Context;
import android.util.AttributeSet;

import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Request.Builder;
import com.forrestpangborn.ibex.data.Size;
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
	protected Request.Builder createRequestBuilder() {
		Builder b = new Builder();
		b.size(new Size(getWidth(), getHeight()));
		b.minSize(new Size(Math.min(getWidth(), 500), Math.min(getHeight(), 500)));
		return b.url(url).scaleType(ScaleType.CENTER_INSIDE);
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