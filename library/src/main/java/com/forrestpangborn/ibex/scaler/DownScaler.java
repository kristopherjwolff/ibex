package com.forrestpangborn.ibex.scaler;

import android.graphics.Bitmap;

public class DownScaler {
	
	public static enum DownScaleType {
		FIT,
		CROP;
	}
	
	public Bitmap scale(Bitmap bmp, int width, int height, DownScaleType type) {
		Bitmap ret = bmp;
		
		if (bmp != null) {
			int bWidth = bmp.getWidth();
			int bHeight = bmp.getHeight();
			
			if (bWidth == width || bHeight == height) {
				ret = bmp;
			} else if (bWidth > width || bHeight > height) {
				float pct = 1f;
				
				switch (type) {
					case FIT:
						pct = Math.min(width / (float)bWidth, height / (float)bHeight);
						break;
						
					case CROP:
						pct = Math.max(width / (float)bWidth, height / (float)bHeight);
						break;
				}
				
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