package com.forrestpangborn.ibex.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView.ScaleType;

import com.google.common.base.Objects;
import com.google.common.hash.Hashing;

public final class Request implements Parcelable {
	
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
	private final boolean isScalable;
	private final ScaleType scaleType;
	
	public Request(Size size, Size minSize, String url, String key, boolean isScalable, ScaleType scaleType) {
		this.size = size;
		this.minSize = minSize;
		this.url = url;
		this.key = key;
		this.isScalable = isScalable;
		this.scaleType = scaleType;
	}
	
	private Request(Parcel in) {
		size = in.readParcelable(Size.class.getClassLoader());
		minSize = in.readParcelable(Size.class.getClassLoader());
		url = in.readString();
		key = in.readString();
		boolean[] tmp = new boolean[1];
		in.readBooleanArray(tmp);
		isScalable = tmp[0];
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
		dest.writeBooleanArray(new boolean[] {isScalable});
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
	
	public boolean isScalable() {
		return isScalable;
	}
	
	public ScaleType getScaleType() {
		return scaleType;
	}
	
	public String getUniqueKey() {
		return Hashing.md5().hashString(key != null ? key : url).toString();
	}
}