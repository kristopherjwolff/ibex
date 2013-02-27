package com.forrestpangborn.ibex.scalers;

import android.graphics.Bitmap;

public abstract class AImageScaler {

	public abstract Bitmap scale(Bitmap bmp, int width, int height);
	
	protected final Bitmap scale(Bitmap bmp, float pct) {
		Bitmap ret = Bitmap.createScaledBitmap(bmp, Math.round(bmp.getWidth() * pct), Math.round(bmp.getHeight() * pct), false);
		bmp.recycle();
		return ret;
	}
}