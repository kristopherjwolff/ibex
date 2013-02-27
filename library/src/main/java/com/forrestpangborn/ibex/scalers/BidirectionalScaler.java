package com.forrestpangborn.ibex.scalers;

import android.graphics.Bitmap;

public class BidirectionalScaler extends AImageScaler {

	@Override
	public Bitmap scale(Bitmap bmp, int width, int height) {
		Bitmap ret = null;
		int bWidth = bmp.getWidth();
		int bHeight = bmp.getHeight();
		
		if (bWidth == width && bHeight == height) {
			ret = bmp;
		} else if (bWidth > width || bHeight > height) {
			ret = new DownScaler().scale(bmp, width, height);
		} else {
			float pct = Math.min(width / (float)bWidth, height / (float)bHeight);
			ret = scale(bmp, pct);
		}
		return ret;
	}
}