package com.forrestpangborn.ibex.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.forrestpangborn.ibex.cache.DiskImageCache;
import com.forrestpangborn.ibex.cache.ImageCache;

public class ImageLoadingService extends AImageLoadingService {
	
	private ThreadPoolExecutor executor;
	private ImageCache cache;
	
	@Override
	protected synchronized ThreadPoolExecutor getExecutor() {
		if (executor == null) {
			executor = new ThreadPoolExecutor(0, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		}
		return executor;
	}
	
	@Override
	protected synchronized ImageCache getCache() {
		if (cache == null) {
			cache = new DiskImageCache(this);
		}
		return cache;
	}
}