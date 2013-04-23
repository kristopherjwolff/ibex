package com.forrestpangborn.ibex.sample;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.cache.TwoLevelImageCache;
import com.forrestpangborn.ibex.manager.RequestManager;

public class SampleRequestManager extends RequestManager {
	
	private static final int SIZE_MEMORY_CACHE = 1500000;
	private static final long SIZE_DISK_CACHE = 7000000;
	
	public SampleRequestManager(Context context) {
		super(context);
	}
	
	@Override
	protected ThreadPoolExecutor getExecutor() {
		return new ThreadPoolExecutor(4, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	@Override
	protected ImageCache getCache() {
		return new TwoLevelImageCache(appContext, SIZE_MEMORY_CACHE, SIZE_DISK_CACHE);
	}
}