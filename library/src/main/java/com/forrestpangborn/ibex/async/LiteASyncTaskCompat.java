package com.forrestpangborn.ibex.async;

import java.util.concurrent.Executor;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Response;
import com.forrestpangborn.ibex.view.AIbexImageView;

public abstract class LiteASyncTaskCompat {
	
	protected abstract Response doInBackground(Request request);

	public void execute(final Request request, Executor executor) {
		AsyncTask<Request, Void, Response> task = new AsyncTask<Request, Void, Response>() {
			@Override
			protected Response doInBackground(Request... params) {
				return LiteASyncTaskCompat.this.doInBackground(params[0]);
			}
			
			@Override
			public void onPostExecute(Response response) {
				LiteASyncTaskCompat.this.onPostExecute(response);
			}
		};
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(executor, request);
		} else {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					final Response response = doInBackground(request);
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							onPostExecute(response);
						}
					});
				}
			});
		}
	}
	
	private void onPostExecute(Response response) {
		if (response != null && response.imageView != null) {
			AIbexImageView imageView = response.imageView.get();
			if (imageView != null) {
				imageView.onResponse(response);
			}
		}
	}
}