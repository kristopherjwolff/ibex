package com.forrestpangborn.ibex.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.task.ImageLoadingTask;

public abstract class ImageLoadingService extends Service {
	
	public static final String ACTION_REQUEST_IMAGE = "com.forrestpangborn.ibex.REQUEST_IMAGE";
	public static final String ACTION_CANCEL_REQUEST = "com.forrestpangborn.ibex.CANCEL_REQUEST";
	public static final String ACTION_IMAGE_LOADED = "com.forrestpangborn.ibex.IMAGE_LOADED";
	
	public static final String EXTRA_REQUEST = "com.forrestpangborn.ibex.REQUEST";
	public static final String EXTRA_RESPONSE = "com.forrestpangborn.ibex.RESPONSE";
	public static final String EXTRA_URL = "com.forrestpangborn.ibex.URL";
	
	private static final int WHAT_STOP_IF_IDLE = 1821;
	private static final int SHUTDOWN_TIMEOUT = 10000;
	
	protected ThreadPoolExecutor executor;
	protected ImageCache cache;
	
	private Map<String, Integer> requestCounts = new HashMap<String, Integer>();
	
	private final Handler uiThreadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case WHAT_STOP_IF_IDLE:
					if (executor.getActiveCount() == 0 && executor.getQueue().isEmpty()) {
						stopSelf();
					}
					break;
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		executor = getExecutor();
		cache = getCache();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		String action = intent.getAction();
		final Request request = intent.getParcelableExtra(EXTRA_REQUEST);
		final String cacheKey = request.getUniqueKey();
		
		if (ACTION_REQUEST_IMAGE.equals(action)) {
			incrementRequestCount(cacheKey);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					processIntent(request, cacheKey);
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
		if (cache != null) {
			cache.close();
		}
	}
	
	protected abstract ThreadPoolExecutor getExecutor();
	protected abstract ImageCache getCache();
	
	protected void processIntent(Request request, final String cacheKey) {
		uiThreadHandler.removeMessages(WHAT_STOP_IF_IDLE);
		
		Integer requestCount = requestCounts.get(cacheKey);
		if (requestCount != null && requestCount > 0) {
			ImageLoadingTask task = new ImageLoadingTask(request, cache);
			if (task.load(this)) {
				uiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						decrementRequestCount(cacheKey);
					}
				});
			}
		}
		uiThreadHandler.removeMessages(WHAT_STOP_IF_IDLE);
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
		if (count != null && count > 2) {
			requestCounts.put(key, --count);
		} else {
			requestCounts.remove(key);
			uiThreadHandler.sendEmptyMessageDelayed(WHAT_STOP_IF_IDLE, SHUTDOWN_TIMEOUT);
		}
	}
}