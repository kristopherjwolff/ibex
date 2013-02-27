package com.forrestpangborn.ibex.view;

import android.content.ComponentName;
import android.content.Context;
import android.util.AttributeSet;

import com.forrestpangborn.ibex.service.ImageLoadingService;

public class IbexImageView extends AIbexImageView {

	public IbexImageView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public IbexImageView(Context context, AttributeSet set) {
		this(context, set, 0);
	}
	
	public IbexImageView(Context context) {
		this(context, null);
	}
	
	@Override
	protected ComponentName getServiceComponentName(Context context) {
		return new ComponentName(context, ImageLoadingService.class);
	}
}