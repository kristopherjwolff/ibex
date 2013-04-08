package com.forrestpangborn.ibex.data;

import junit.framework.TestCase;

import org.junit.Test;

import android.widget.ImageView.ScaleType;

import com.forrestpangborn.ibex.data.Request.Builder;

public class RequestTest extends TestCase {
	
	private static Request buildExpectsException(Builder builder, Class<? extends Exception> eClass) {
		Exception exception = null;
		Request r = null;
		try {
			r = builder.build();
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull(exception);
		assertEquals(eClass, exception.getClass());
		return r;
	}
	
	private static final Size SIZE = new Size(100, 100);
	private static final Size MIN_SIZE = new Size(75, 75);
	private static final String URL = "url";
	
	private Builder builder;
	private Request request;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		builder = new Builder();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		builder = null;
		request = null;
	}
	
	@Test
	public void testMissingSize() {
		builder.minSize(MIN_SIZE).url(URL);
		assertNull(buildExpectsException(builder, IllegalStateException.class));
	}
	
	@Test
	public void testMissingUrl() {
		builder.size(SIZE).minSize(MIN_SIZE);
		assertNull(buildExpectsException(builder, IllegalStateException.class));
	}
	
	@Test
	public void testRequestContainsOnlyRequiredArgs() {
		builder.size(SIZE).minSize(MIN_SIZE).url(URL);
		request = builder.build();
		
		assertNotNull(request);
		assertEquals(SIZE, request.getSize());
		assertEquals(MIN_SIZE, request.getMinSize());
		assertEquals(URL, request.getUrl());
		
		assertNull(request.getKey());
		assertNull(request.getScaleType());
	}
	
	@Test
	public void testRequestContainsKey() {
		builder.size(SIZE).minSize(MIN_SIZE).url(URL).key("key");
		request = builder.build();
		
		assertEquals("key", request.getKey());
	}
	
	@Test
	public void testRequestAlternateScaleType() {
		builder.size(SIZE).minSize(MIN_SIZE).url(URL).scaleType(ScaleType.FIT_CENTER);
		request = builder.build();
		
		assertEquals(ScaleType.FIT_CENTER, request.getScaleType());
	}
}