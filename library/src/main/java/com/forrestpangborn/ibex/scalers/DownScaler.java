package com.forrestpangborn.ibex.scalers;

import android.graphics.Bitmap;

public class DownScaler extends AImageScaler {
	
	@Override
	public Bitmap scale(Bitmap bmp, int width, int height) {
		Bitmap ret = bmp;
		int bWidth = bmp.getWidth();
		int bHeight = bmp.getHeight();
		
		if (bWidth == width && bHeight == height) {
			ret = bmp;
		} else if (bWidth > width || bHeight > height) {
			float pct = Math.min(width / (float)bWidth, height / (float)bHeight);
			ret = scale(bmp, pct);
		}
		return ret;
	}
}