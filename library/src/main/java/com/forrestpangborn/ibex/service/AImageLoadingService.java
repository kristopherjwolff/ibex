package com.forrestpangborn.ibex.service;

import static com.forrestpangborn.ibex.util.DataUtils.buildMD5Hash;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.task.ImageLoadingTask;
import com.forrestpangborn.ibex.util.ImageMetadata;

public abstract class AImageLoadingService extends Service {
	
	public static final String ACTION_REQUEST_IMAGE = "com.forrestpangborn.ibex.REQUEST_IMAGE";
	public static final String ACTION_CANCEL_REQUEST = "com.forrestpangborn.ibex.CANCEL_REQUEST";
	public static final String ACTION_IMAGE_LOADED = "com.forrestpangborn.ibex.IMAGE_LOADED";
	
	public static final String EXTRA_METADATA = "com.forrestpangborn.ibex.METADATA";
	public static final String EXTRA_BITMAP = "com.forrestpangborn.ibex.BITMAP";
	public static final String EXTRA_URL = "com.forrestpangborn.ibex.URL";
	
	private static final int WHAT_STOP_IF_IDLE = 1821;
	private static final int SHUTDOWN_TIMEOUT = 10000;
	
	private Map<String, Integer> requestCounts = new HashMap<String, Integer>();
	
	private final Handler uiThreadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case WHAT_STOP_IF_IDLE:
					ThreadPoolExecutor executor = getExecutor();
					if (executor.getActiveCount() == 0 && executor.getQueue().isEmpty()) {
						stopSelf();
					}
					break;
			}
		}
	};
	
	private static String obtainCacheKey(ImageMetadata metadata) {
		String key = metadata.getKey();
		return (key != null ? buildMD5Hash(key) : buildMD5Hash(metadata.getUrl()));	
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		String action = intent.getAction();
		final ImageMetadata metadata = intent.getParcelableExtra(EXTRA_METADATA);
		final String cacheKey = obtainCacheKey(metadata);
		
		if (ACTION_REQUEST_IMAGE.equals(action)) {
			incrementRequestCount(cacheKey);
			getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					processIntent(metadata, cacheKey);
				}
			});
		} else if (ACTION_CANCEL_REQUEST.equals(action)) {
			decrementRequestCount(cacheKey);
		}
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getCache().close();
	}
	
	protected abstract ThreadPoolExecutor getExecutor();
	protected abstract ImageCache getCache();
	
	protected void processIntent(ImageMetadata metadata, final String cacheKey) {
		uiThreadHandler.removeMessages(WHAT_STOP_IF_IDLE);
		
		Integer requestCount = requestCounts.get(cacheKey);
		if (requestCount != null && requestCount > 0) {
			ImageLoadingTask task = new ImageLoadingTask(metadata, getCache());
			task.load(this);
			
			uiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					decrementRequestCount(cacheKey);
				}
			});
		}
		uiThreadHandler.sendEmptyMessageDelayed(WHAT_STOP_IF_IDLE, SHUTDOWN_TIMEOUT);
	}
	
	private void incrementRequestCount(String key) {
		Integer count = requestCounts.get(key);
		if (count == null) {
			requestCounts.put(key, 1);
		} else {
			requestCounts.put(key, ++count);
		}
	}
	
	private void decrementRequestCount(String key) {
		Integer count = requestCounts.get(key);
		if (count > 2) {
			requestCounts.put(key, --count);
		} else {
			requestCounts.remove(key);
			uiThreadHandler.sendEmptyMessageDelayed(WHAT_STOP_IF_IDLE, SHUTDOWN_TIMEOUT);
		}
	}
}