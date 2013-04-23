package com.forrestpangborn.ibex.data;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.widget.ImageView.ScaleType;

import com.forrestpangborn.ibex.data.Request.Builder;
import com.forrestpangborn.ibex.manager.RequestManager;
import com.forrestpangborn.ibex.view.AIbexImageView;
import com.google.common.hash.Hashing;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
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
	
	@Override @Before
	public void setUp() throws Exception {
		super.setUp();
		builder = new Builder();
	}
	
	@Override @After
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
		builder.imageView(new MockIbexImageView()).size(SIZE).minSize(MIN_SIZE).url(URL);
		request = builder.build();
		
		assertNotNull(request);
		assertEquals(SIZE, request.size);
		assertEquals(MIN_SIZE, request.minSize);
		assertEquals(URL, request.url);
		
		assertNotNull(request.key);
		assertNull(request.scaleType);
	}
	
	@Test
	public void testRequestContainsKey() {
		builder.imageView(new MockIbexImageView()).size(SIZE).minSize(MIN_SIZE).url(URL).cacheKey("key");
		request = builder.build();
		
		assertEquals(Hashing.md5().hashString("key").toString(), request.key);
	}
	
	@Test
	public void testRequestAlternateScaleType() {
		builder.imageView(new MockIbexImageView()).size(SIZE).minSize(MIN_SIZE).url(URL).scaleType(ScaleType.FIT_CENTER);
		request = builder.build();
		
		assertEquals(ScaleType.FIT_CENTER, request.scaleType);
	}
	
	private static class MockIbexImageView extends AIbexImageView {

		public MockIbexImageView() {
			super(new Activity());
		}

		@Override
		protected Builder createRequestBuilder() {
			return null;
		}

		@Override
		protected RequestManager getRequestManager() {
			return null;
		}
		
	}
}