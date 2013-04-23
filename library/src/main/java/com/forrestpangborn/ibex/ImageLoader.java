package com.forrestpangborn.ibex;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView.ScaleType;

import com.forrestpangborn.ibex.cache.ImageCache;
import com.forrestpangborn.ibex.data.Request;
import com.forrestpangborn.ibex.data.Response;
import com.forrestpangborn.ibex.data.Size;
import com.forrestpangborn.ibex.scaler.DownScaler;
import com.forrestpangborn.ibex.scaler.DownScaler.DownScaleType;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.common.io.ByteStreams;

public class ImageLoader {

	private final Request request;
	private final ImageCache cache;
	
	public ImageLoader(Request request, ImageCache cache) {
		this.request = request;
		this.cache = cache;
	}
	
	public Response load(Context context) {
		Response ret = null;
		
		if (request.url != null && request.size.isNonZero()) {
			Bitmap bmp = null;
			if (cache != null) {
				byte[] cacheData = cache.get(request);
				if (cacheData != null) {
					bmp = buildScaledBitmap(cacheData, request.size, request.scaleType, request.shouldScale);
				}
			}
			
			if (bmp == null && request.url != null) {
				byte[] remoteData = loadImage(request.url, request.headers, request.key);
				if (remoteData != null) {
					if (cache != null) {
						cache.put(request, remoteData);
					}
					bmp = buildScaledBitmap(remoteData, request.size, request.scaleType, request.shouldScale);
				}
			}
			
			if (bmp != null && bmp.getWidth() >= request.minSize.width && bmp.getHeight() >= request.minSize.height) {
				ret = new Response(request, bmp);
			}
		}
		return ret;
	}
	
	private byte[] loadImage(String url, Bundle headers, String key) {
		byte[] data = null;
		HttpTransport transport = AndroidHttp.newCompatibleTransport();
		
		try {
			HttpRequest request = transport.createRequestFactory().buildGetRequest(new GenericUrl(url));
			HttpHeaders httpHeaders = new HttpHeaders();
			
			if (headers != null) {
				for (String headerKey : headers.keySet()) {
					String headerValue = headers.getString(headerKey);
					if (headerValue != null) {
						httpHeaders.put(headerKey, headerValue);
					}
				}
				
				request.setHeaders(httpHeaders);
			}
			
			HttpResponse response = request.execute();
			
			if (response.getStatusCode() == HttpURLConnection.HTTP_OK) {
				InputStream input = response.getContent();
				data = ByteStreams.toByteArray(input);
				input.close();
			} else {
				data = null;
			}
		} catch (IOException ex) {
			data = null;
		}
		
		
		return data;
	}
	
	private Bitmap buildScaledBitmap(byte[] data, Size size, ScaleType scaleType, boolean shouldScale) {
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
				
				if (!shouldScale || scaleType == null || origSize.equals(size)) {
					bmp = orig;
				} else {
					switch (scaleType) {
						case CENTER_INSIDE:
						case FIT_CENTER:
						case FIT_START:
						case FIT_END:
						case FIT_XY:
							bmp = new DownScaler().scale(orig, size.width, size.height, DownScaleType.FIT);
							break;
						
						case CENTER_CROP:
							bmp = new DownScaler().scale(orig, size.width, size.height, DownScaleType.CROP);
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