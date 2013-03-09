package com.forrestpangborn.ibex.scaler;

import android.graphics.Bitmap;

public class DownScaler {
	
	public Bitmap scale(Bitmap bmp, int width, int height) {
		Bitmap ret = bmp;
		
		if (bmp != null) {
			int bWidth = bmp.getWidth();
			int bHeight = bmp.getHeight();
			
			if (bWidth == width || bHeight == height) {
				ret = bmp;
			} else if (bWidth > width || bHeight > height) {
				float pct = Math.min(width / (float)bWidth, height / (float)bHeight);
				ret = scale(bmp, pct);
			}
		}
		
		return ret;
	}
	
	protected final Bitmap scale(Bitmap bmp, float pct) {
		Bitmap ret = Bitmap.createScaledBitmap(bmp, Math.round(bmp.getWidth() * pct), Math.round(bmp.getHeight() * pct), false);
		bmp.recycle();
		return ret;
	}
}