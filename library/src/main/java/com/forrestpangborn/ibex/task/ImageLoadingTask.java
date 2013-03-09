package com.forrestpangborn.ibex.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView.ScaleType;

import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Response;
import com.forrestpangborn.ibex.data.Size;
import com.forrestpangborn.ibex.scaler.DownScaler;
import com.forrestpangborn.ibex.service.ImageLoadingService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.common.io.ByteStreams;

public class ImageLoadingTask {

	private final Request request;
	private final ImageCache cache;
	
	public ImageLoadingTask(Request request, ImageCache cache) {
		this.request = request;
		this.cache = cache;
	}
	
	public boolean load(Context context) {
		Size size = request.getSize();
		Size minSize = request.getMinSize();
		String url = request.getUrl();
		ScaleType scaleType = request.isScalable() ? request.getScaleType() : null;
		String cacheKey = request.getUniqueKey();
		
		if (url != null && size.isNonZero()) {
			Bitmap bmp = null;
			if (cache != null) {
				byte[] cacheData = cache.get(cacheKey);
				if (cacheData != null) {
					bmp = buildScaledBitmap(cacheData, size, scaleType);
				}
			}
			
			if (bmp == null && url != null) {
				byte[] remoteData = loadImage(url, cacheKey);
				if (remoteData != null) {
					if (cache != null) {
						cache.put(cacheKey, remoteData);
					}
					bmp = buildScaledBitmap(remoteData, size, scaleType);
				}
			}
			
			if (bmp != null && bmp.getWidth() >= minSize.width && bmp.getHeight() >= minSize.height) {
				Intent loadedIntent = new Intent(ImageLoadingService.ACTION_IMAGE_LOADED);
				loadedIntent.putExtra(ImageLoadingService.EXTRA_RESPONSE, new Response(request, bmp));
				LocalBroadcastManager.getInstance(context).sendBroadcast(loadedIntent);
				return true;
			}
		}
		return false;
	}
	
	private byte[] loadImage(String url, String key) {
		byte[] data = null;
		HttpTransport transport = AndroidHttp.newCompatibleTransport();
		
		try {
			HttpRequest request = transport.createRequestFactory().buildGetRequest(new GenericUrl(url));
			HttpResponse response = request.execute();
			
			if (response.getStatusCode() == HttpURLConnection.HTTP_OK) {
				InputStream input = response.getContent();
				data = ByteStreams.toByteArray(input);
				input.close();
				cache.put(key, data);
			} else {
				data = null;
			}
		} catch (IOException ex) {
			data = null;
		}
		
		
		return data;
	}
	
	private Bitmap buildScaledBitmap(byte[] data, Size size, ScaleType scaleType) {
		Bitmap bmp = null;
		
		if (data != null && size.isNonZero()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, options);
			
			int inSampleSize = 0;
			for (int i = 1; i < 10; i++) {
				int x = (int)Math.pow(2, i);
				if (((options.outWidth / x) >= size.width) && ((options.outHeight / x) >= size.height)) {
					inSampleSize = x;
				} else {
					break;
				}
			}
			options.inJustDecodeBounds = false;
			options.inSampleSize = inSampleSize;
			
			Bitmap orig = BitmapFactory.decodeByteArray(data, 0, data.length, options);
			if (orig != null) {
				orig.setDensity(Bitmap.DENSITY_NONE);
				Size origSize = new Size(orig);
				
				if (scaleType == null || origSize.equals(size)) {
					bmp = orig;
				} else {
					// TODO : centerCrop scaler
					switch (scaleType) {
						case CENTER_INSIDE:
						case FIT_CENTER:
						case FIT_START:
						case FIT_END:
						case FIT_XY:
							bmp = new DownScaler().scale(orig, size.width, size.height);
							break;
							
						case CENTER:
						case MATRIX:
						default:
							bmp = orig;
							break;
					}
				}
			}
		}
		return bmp;
	}
}