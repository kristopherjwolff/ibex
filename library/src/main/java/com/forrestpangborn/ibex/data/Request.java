package com.forrestpangborn.ibex.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView.ScaleType;

import com.google.common.base.Objects;
import com.google.common.hash.Hashing;

public final class Request implements Parcelable {
	
	public static final class Builder {
		private Size size;
		private Size minSize;
		private String url;
		private Bundle headers;
		private String key;
		private ScaleType scaleType;
		private boolean shouldScale;
		
		public Builder size(Size value) { this.size = value; return this; }
		public Builder minSize(Size value) { this.minSize = value; return this; }
		public Builder url(String value) { this.url = value; return this; }
		public Builder headers(Bundle value) { this.headers = value; return this; }
		public Builder key(String value) { this.key = value; return this; }
		public Builder scaleType(ScaleType value) { this.scaleType = value; return this; }
		public Builder shouldScale(boolean value) { this.shouldScale = value; return this; }
		
		public Request build() {
			if (size == null || minSize == null || url == null) {
				throw new IllegalStateException();
			}
			return new Request(size, minSize, url, headers, key, scaleType, shouldScale);
		}
	}
	
	public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
		 public Request createFromParcel(Parcel in) {
			 return new Request(in);
		 }
		 
		 public Request[] newArray(int size) {
			 return new Request[size];
		 }
	 };
	
	private final Size size;
	private final Size minSize;
	private final String url;
	private final Bundle headers;
	private final String key;
	private final ScaleType scaleType;
	private final boolean shouldScale;
	
	private Request(Size size, Size minSize, String url, Bundle headers, String key, ScaleType scaleType, boolean shouldScale) {
		this.size = size;
		this.minSize = minSize;
		this.url = url;
		this.headers = headers;
		this.key = key;
		this.scaleType = scaleType;
		this.shouldScale = shouldScale;
	}
	
	private Request(Parcel in) {
		size = in.readParcelable(Size.class.getClassLoader());
		minSize = in.readParcelable(Size.class.getClassLoader());
		url = in.readString();
		headers = in.readBundle();
		key = in.readString();
		scaleType = Enum.valueOf(ScaleType.class, in.readString());
		boolean[] tmp = new boolean[1];
		in.readBooleanArray(tmp);
		shouldScale = tmp[0];
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
		return Objects.hashCode(size, url, key, scaleType);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(size, 0);
		dest.writeParcelable(minSize, 0);
		dest.writeString(url);
		dest.writeBundle(headers);
		dest.writeString(key);
		dest.writeString(scaleType.toString());
		dest.writeBooleanArray(new boolean[] { shouldScale });
	}
	
	public Size getSize() {
		return size;
	}
	
	public Size getMinSize() {
		return minSize;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Bundle getHeaders() {
		return headers;
	}
	
	public String getKey() {
		return key;
	}
	
	public ScaleType getScaleType() {
		return scaleType;
	}
	
	public boolean getShouldScale() {
		return shouldScale;
	}
	
	public String getUniqueKey() {
		return Hashing.md5().hashString(key != null ? key : url).toString();
	}
}