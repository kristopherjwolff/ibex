package com.forrestpangborn.ibex.data;

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
		private String key;
		private boolean shouldScale;
		private ScaleType scaleType = ScaleType.CENTER;
		
		public Builder size(Size value) { this.size = value; return this; }
		public Builder minSize(Size value) { this.minSize = value; return this; }
		public Builder url(String value) { this.url = value; return this; }
		public Builder key(String value) { this.key = value; return this; }
		public Builder scale(boolean value) { this.shouldScale = value; return this; }
		public Builder scaleType(ScaleType value) { this.scaleType = value; return this; }
		
		public Request build() {
			if (size == null || minSize == null || url == null) {
				throw new IllegalStateException();
			}
			return new Request(size, minSize, url, key, shouldScale, scaleType);
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
	private final String key;
	private final boolean shouldScale;
	private final ScaleType scaleType;
	
	private Request(Size size, Size minSize, String url, String key, boolean shouldScale, ScaleType scaleType) {
		this.size = size;
		this.minSize = minSize;
		this.url = url;
		this.key = key;
		this.shouldScale = shouldScale;
		this.scaleType = scaleType;
	}
	
	private Request(Parcel in) {
		size = in.readParcelable(Size.class.getClassLoader());
		minSize = in.readParcelable(Size.class.getClassLoader());
		url = in.readString();
		key = in.readString();
		boolean[] tmp = new boolean[1];
		in.readBooleanArray(tmp);
		shouldScale = tmp[0];
		scaleType = Enum.valueOf(ScaleType.class, in.readString());
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Request) {
			Request request = (Request)object;
			return Objects.equal(size, request.size) &&
					Objects.equal(url, request.url) &&
					Objects.equal(key, request.key) &&
					Objects.equal(scaleType, request.scaleType);
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
		dest.writeString(key);
		dest.writeBooleanArray(new boolean[] {shouldScale});
		dest.writeString(scaleType.toString());
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
	
	public String getKey() {
		return key;
	}
	
	public boolean shouldScale() {
		return shouldScale;
	}
	
	public ScaleType getScaleType() {
		return scaleType;
	}
	
	public String getUniqueKey() {
		return Hashing.md5().hashString(key != null ? key : url).toString();
	}
}