package com.forrestpangborn.ibex.data;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.widget.ImageView.ScaleType;

import com.forrestpangborn.ibex.view.AIbexImageView;
import com.google.common.base.Objects;
import com.google.common.hash.Hashing;

public final class Request {
	
	public static final class Builder {
		private WeakReference<AIbexImageView> imageViewRef;
		private Size size;
		private Size minSize;
		private String url;
		private Bundle headers;
		private String cacheKey;
		private ScaleType scaleType;
		private boolean shouldScale;
		
		public Builder imageView(AIbexImageView value) { imageViewRef = new WeakReference<AIbexImageView>(value); return this; }
		public Builder size(Size value) { this.size = value; return this; }
		public Builder minSize(Size value) { this.minSize = value; return this; }
		public Builder url(String value) { this.url = value; return this; }
		public Builder headers(Bundle value) { this.headers = value; return this; }
		public Builder cacheKey(String value) { this.cacheKey = value; return this; }
		public Builder scaleType(ScaleType value) { this.scaleType = value; return this; }
		public Builder shouldScale(boolean value) { this.shouldScale = value; return this; }
		
		public Request build() {
			if (imageViewRef == null || size == null || url == null) {
				throw new IllegalStateException();
			}
			return new Request(imageViewRef, size, minSize, url, headers, cacheKey, scaleType, shouldScale);
		}
	}
	
	public final WeakReference<AIbexImageView> imageViewRef;
	public final Size size;
	public final Size minSize;
	public final String url;
	public final Bundle headers;
	public final String key;
	public final ScaleType scaleType;
	public final boolean shouldScale;
	
	private final String cacheKey;
	
	private Request(WeakReference<AIbexImageView> imageViewRef, Size size, Size minSize, String url, Bundle headers, String cacheKey, ScaleType scaleType, boolean shouldScale) {
		this.imageViewRef = imageViewRef;
		this.size = size;
		this.minSize = minSize != null ? minSize : size;
		this.url = url;
		this.headers = headers;
		this.scaleType = scaleType;
		this.shouldScale = shouldScale;
		this.key = Hashing.md5().hashString(cacheKey != null ? cacheKey : url).toString();
		
		this.cacheKey = cacheKey;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Request) {
			Request request = (Request)object;
			return Objects.equal(size, request.size) &&
					Objects.equal(url, request.url) &&
					Objects.equal(key, request.key) &&
					Objects.equal(scaleType, request.scaleType) &&
					Objects.equal(shouldScale, request.shouldScale);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(size, url, cacheKey, scaleType);
	}
}