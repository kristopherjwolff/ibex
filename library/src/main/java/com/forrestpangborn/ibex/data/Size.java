package com.forrestpangborn.ibex.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

public class Size implements Parcelable {
	
	public static final Parcelable.Creator<Size> CREATOR = new Parcelable.Creator<Size>(){
		public Size createFromParcel(Parcel in) {
			return new Size(in);
		}

		public Size[] newArray(int size) {
			return new Size[size];
		}
	};
	
	public final int width;
	public final int height;
	
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Size(Bitmap bitmap) {
		this(bitmap.getWidth(), bitmap.getHeight());
	}
	
	private Size(Parcel in) {
		this.width = in.readInt();
		this.height = in.readInt();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Size) {
			Size size = (Size)object;
			return Objects.equal(width, size.width) && Objects.equal(height, size.height);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(width, height);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(width);
		dest.writeInt(height);
	}
	
	public boolean isNonZero() {
		return width > 0 && height > 0;
	}
}