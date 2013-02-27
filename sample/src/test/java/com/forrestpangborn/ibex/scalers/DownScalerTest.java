package com.forrestpangborn.ibex.scalers;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DownScalerTest extends TestCase {
	
	private DownScaler scaler;
	
	@Override @Before
	public void setUp() throws Exception {
		super.setUp();
		scaler = new DownScaler();
	}
	
	@Override @After
	public void tearDown() throws Exception {
		super.tearDown();
		scaler = null;
	}
	
	@Test
	public void testScaleDownSquareToSquare() {
		Bitmap orig = Bitmap.createBitmap(20, 20, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 10, 10);
		
		assertEquals(10, bmp.getWidth());
		assertEquals(10, bmp.getHeight());
	}
}