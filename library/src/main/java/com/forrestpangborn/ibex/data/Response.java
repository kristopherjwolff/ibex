package com.forrestpangborn.ibex.data;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.widget.ImageView.ScaleType;

import com.forrestpangborn.ibex.view.AIbexImageView;
import com.google.common.base.Objects;

public class Response {

	public final WeakReference<AIbexImageView> imageView;
	public final Size size;
	public final String uniqueKey;
	public final ScaleType scaleType;
	public final Bitmap bitmap;
	
	public Response(WeakReference<AIbexImageView>ref, Size size, String uniqueKey, ScaleType scaleType, Bitmap bmp) {
		this.imageView = ref; 
		this.size = size;
		this.uniqueKey = uniqueKey;
		this.scaleType = scaleType;
		this.bitmap = bmp;
	}
	
	public Response(Request request, Bitmap bmp) {
		this(request.imageViewRef, request.size, request.key, request.scaleType, bmp);
	}
	
	public boolean satisifies(Request request) {
		return Objects.equal(size, request.size) && 
				Objects.equal(uniqueKey, request.key) &&
				Objects.equal(scaleType, request.scaleType);
	}
}