package com.forrestpangborn.ibex.sample;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.cache.TwoLevelImageCache;
import com.forrestpangborn.ibex.service.ImageLoadingService;

public class SampleImageLoadingService extends ImageLoadingService {
	
	private static final int SIZE_MEMORY_CACHE = 1500000;
	private static final long SIZE_DISK_CACHE = 7000000;
	
	private ThreadPoolExecutor executor;
	private ImageCache cache;
	
	@Override
	protected ThreadPoolExecutor getExecutor() {
		if (executor == null) {
			executor = new ThreadPoolExecutor(4, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}
		return executor;
	}
	
	@Override
	protected ImageCache getCache() {
		if (cache == null) {
			cache = new TwoLevelImageCache(this, SIZE_MEMORY_CACHE, SIZE_DISK_CACHE);
		}
		return cache;
	}
}