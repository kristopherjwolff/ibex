package com.forrestpangborn.ibex.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.os.Handler;

import com.forrestpangborn.ibex.ImageLoader;
import com.forrestpangborn.ibex.async.LiteASyncTaskCompat;
import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Response;

public abstract class RequestManager {
	
	private Map<String, Integer> requestCounts = new HashMap<String, Integer>();
	
	protected final Context appContext;
	protected final ThreadPoolExecutor executor;
	protected final ImageCache cache;
	
	private final Handler uiThreadHandler = new Handler();
	
	protected abstract ImageCache getCache();
	protected abstract ThreadPoolExecutor getExecutor();
	
	public RequestManager(Context context) {
		appContext = context.getApplicationContext();
		executor = getExecutor();
		cache = getCache();
	}

	public void queue(final Request request) {
		incrementRequestCount(request.key);
		
		new LiteASyncTaskCompat() {
			@Override
			protected Response doInBackground(Request request) {
				Response response = null;
				final String key = request.key;
				Integer requestCount = requestCounts.get(key);
				
				if (requestCount != null && requestCount > 0) {
					response = new ImageLoader(request, cache).load(appContext);
					uiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							decrementRequestCount(key);
						}
					});
				}
				return response;
			}
		}.execute(request, executor);
	}
	
	public void cancel(Request request) {
		decrementRequestCount(request.key);
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
		}
	}
}