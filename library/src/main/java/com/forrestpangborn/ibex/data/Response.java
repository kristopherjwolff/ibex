package com.forrestpangborn.ibex.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView.ScaleType;

import com.google.common.base.Objects;

public class Response implements Parcelable {
	
	public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
		 public Response createFromParcel(Parcel in) {
			 return new Response(in);
		 }
		 
		 public Response[] newArray(int size) {
			 return new Response[size];
		 }
	 };

	public final Size size;
	public final String uniqueKey;
	public final boolean isScaled;
	public final ScaleType scaleType;
	public final Bitmap bitmap;
	
	public Response(Size size, String uniqueKey, boolean isScaled, ScaleType scaleType, Bitmap bmp) {
		this.size = size;
		this.uniqueKey = uniqueKey;
		this.isScaled = isScaled;
		this.scaleType = scaleType;
		this.bitmap = bmp;
	}
	
	public Response(Request request, Bitmap bmp) {
		this(request.getSize(), request.getUniqueKey(), request.isScalable(), request.getScaleType(), bmp);
	}
	
	private Response(Parcel in) {
		size = in.readParcelable(Size.class.getClassLoader());
		uniqueKey = in.readString();
		boolean[] tmp = new boolean[1];
		in.readBooleanArray(tmp);
		isScaled = tmp[0];
		scaleType = Enum.valueOf(ScaleType.class, in.readString());
		bitmap = in.readParcelable(Bitmap.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(size, 0);
		dest.writeString(uniqueKey);
		dest.writeBooleanArray(new boolean[] {isScaled});
		dest.writeString(scaleType.name());
		dest.writeParcelable(bitmap, 0);
	}
	
	public Size getSize() {
		return size;
	}
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	
	public ScaleType getScaleType() {
		return scaleType;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public boolean satisifies(Request request) {
		return Objects.equal(size, request.getSize()) && 
				Objects.equal(uniqueKey, request.getUniqueKey()) &&
				Objects.equal(isScaled, request.isScalable()) &&
				Objects.equal(scaleType, request.getScaleType());
	}
}