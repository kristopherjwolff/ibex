package com.forrestpangborn.ibex.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Request.Builder;
import com.forrestpangborn.ibex.data.Response;
import com.forrestpangborn.ibex.manager.RequestManager;

public abstract class AIbexImageView extends ImageView {
	
	private int defaultDrawableId;
	private RequestManager requestManager;
	private Request previousRequest;
	
	protected abstract Builder createRequestBuilder();
	protected abstract RequestManager getRequestManager();
	
	public AIbexImageView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
	}
	
	public AIbexImageView(Context context) {
		this(context, null, -1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w > 0 && h > 0) {
			loadImage();
		}
	}
	
	public void setDefaultDrawableId(int value) {
		this.defaultDrawableId = value;
	}
	
	public void clear() {
		cancelRequest();
		removeImage();
	}
	
	public boolean onResponse(Response response) {
		boolean ret = false;
		if (previousRequest != null && response.satisifies(previousRequest)) {
			ret = true;
			setImageBitmap(response.bitmap);
		}
		return ret;
	}
	
	protected void loadImage() {
		Request request;
		
		try {
			request = createRequestBuilder().build();
		} catch (IllegalStateException ex) {
			Log.i("Ibex", "Error building request in AIbexImageView.");
			request = null;
		}
		
		if (request != null && !request.equals(previousRequest)) {
			clear();
			
			if (requestManager == null) {
				requestManager = getRequestManager();
			}
			
			requestManager.queue(request);
			previousRequest = request;
		}
	}
	
	public void cancelRequest() {
		if (previousRequest != null) {
			if (requestManager == null) {
				requestManager = getRequestManager();
			}
			
			requestManager.cancel(previousRequest);
			previousRequest = null;
		}
	}
	
	private void removeImage() {
		if (defaultDrawableId == 0) {
			setImageDrawable(null);
		} else {
			setImageResource(defaultDrawableId);
		}
	}
}