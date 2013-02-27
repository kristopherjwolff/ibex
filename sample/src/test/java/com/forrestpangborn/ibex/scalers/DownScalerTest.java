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
		assertTrue(bmp != orig);
	}
	
	@Test
	public void testNoScaleUp() {
		Bitmap orig = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 20, 20);
		assertEquals(10, bmp.getWidth());
		assertEquals(10, bmp.getHeight());
		assertTrue(bmp == orig);
	}
	
	@Test
	public void testEqualDimensions() {
		Bitmap orig = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 10, 10);
		assertEquals(10, bmp.getWidth());
		assertEquals(10, bmp.getHeight());
		assertTrue(bmp == orig);
	}
	
	@Test
	public void testEqualWidth() {
		Bitmap orig = Bitmap.createBitmap(20, 10, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 20, 20);
		assertEquals(20, bmp.getWidth());
		assertEquals(10, bmp.getHeight());
		assertTrue(bmp == orig);
	}
	
	@Test
	public void testEqualHeight() {
		Bitmap orig = Bitmap.createBitmap(10, 20, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 20, 20);
		assertEquals(10, bmp.getWidth());
		assertEquals(20, bmp.getHeight());
		assertTrue(bmp == orig);
	}
	
	@Test
	public void testScaleDownWidthBased() {
		Bitmap orig = Bitmap.createBitmap(40, 10, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 20, 20);
		assertEquals(20, bmp.getWidth());
		assertEquals(5, bmp.getHeight());
		assertTrue(bmp != orig);
	}
	
	@Test
	public void testScaleDownHeightBased() {
		Bitmap orig = Bitmap.createBitmap(10, 40, Config.ARGB_8888);
		Bitmap bmp = scaler.scale(orig, 20, 20);
		assertEquals(5, bmp.getWidth());
		assertEquals(20, bmp.getHeight());
		assertTrue(bmp != orig);
	}
}