package com.forrestpangborn.ibex.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView.ScaleType;

public final class ImageMetadata implements Parcelable {
	
	private final int width;
	private final int height;
	private final int minWidth;
	private final int minHeight;
	private final String url;
	private final String key;
	private final ScaleType scaleType;
	
	public static final Parcelable.Creator<ImageMetadata> CREATOR = new Parcelable.Creator<ImageMetadata>() {
		 public ImageMetadata createFromParcel(Parcel in) {
			 return new ImageMetadata(in);
		 }
		 
		 public ImageMetadata[] newArray(int size) {
			 return new ImageMetadata[size];
		 }
	 };
	
	public ImageMetadata(int width, int height, int minWidth, int minHeight, String url, String key, ScaleType scaleType) {
		this.width = width;
		this.height = height;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.url = url;
		this.key = key;
		this.scaleType = scaleType;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeInt(minWidth);
		dest.writeInt(minHeight);
		dest.writeString(url);
		dest.writeString(key);
		dest.writeString(scaleType.toString());
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMinWidth() {
		return minWidth;
	}
	
	public int getMinHeight() {
		return minHeight;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getKey() {
		return key;
	}
	
	public ScaleType getScaleType() {
		return scaleType;
	}
	
	private ImageMetadata(Parcel in) {
		width = in.readInt();
		height = in.readInt();
		minWidth = in.readInt();
		minHeight = in.readInt();
		url = in.readString();
		key = in.readString();
		scaleType = Enum.valueOf(ScaleType.class, in.readString());
	}
}