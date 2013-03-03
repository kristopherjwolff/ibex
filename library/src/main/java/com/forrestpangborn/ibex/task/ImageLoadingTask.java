package com.forrestpangborn.ibex.task;

import static com.forrestpangborn.ibex.util.DataUtils.buildByteArray;

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
import com.forrestpangborn.ibex.scalers.AImageScaler;
import com.forrestpangborn.ibex.scalers.BidirectionalScaler;
import com.forrestpangborn.ibex.scalers.DownScaler;
import com.forrestpangborn.ibex.service.AImageLoadingService;
import com.forrestpangborn.ibex.util.ImageMetadata;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

public class ImageLoadingTask {

	private ImageMetadata metadata;
	private ImageCache cache;
	
	public ImageLoadingTask(ImageMetadata metadata, ImageCache cache) {
		this.metadata = metadata;
		this.cache = cache;
	}
	
	public void load(Context context) {
		int width = metadata.getWidth();
		int height = metadata.getHeight();
		int minWidth = metadata.getMinWidth();
		int minHeight = metadata.getHeight();
		String url = metadata.getUrl();
		String key = metadata.getKey();
		ScaleType scaleType = metadata.getScaleType();
		
		if (url != null && width != -1 && height != -1) {
			Bitmap bmp = buildScaledBitmap(cache.get(key), width, height, minWidth, minHeight, scaleType /*0*/);
			
			if (bmp == null && url != null) {
				bmp = buildScaledBitmap(loadImage(url, key), width, height, minWidth, minHeight, scaleType /*-1*/);
			}
			
			if (bmp != null) {
				Intent loadedIntent = new Intent(AImageLoadingService.ACTION_IMAGE_LOADED);
				loadedIntent.putExtra(AImageLoadingService.EXTRA_URL, url);
				loadedIntent.putExtra(AImageLoadingService.EXTRA_BITMAP, bmp);
				LocalBroadcastManager.getInstance(context).sendBroadcast(loadedIntent);
			}
		}
	}
	
	private byte[] loadImage(String url, String key) {
		byte[] data;
		HttpTransport transport = AndroidHttp.newCompatibleTransport();
		
		try {
			HttpRequest request = transport.createRequestFactory().buildGetRequest(new GenericUrl(url));
			HttpResponse response = request.execute();
			
			InputStream stream = response.getContent();
			if (response.getStatusCode() == HttpURLConnection.HTTP_OK) {
				data = buildByteArray(stream);
				cache.put(key, data);
			} else {
				data = null;
			}
		} catch (IOException ex) {
			data = null;
		}
		
		return data;
	}
	
	private Bitmap buildScaledBitmap(byte[] data, int width, int height, int minWidth, int minHeight, ScaleType scaleType) {
		Bitmap bmp = null;
		if (data != null && width > 0 && height > 0) {
			Bitmap orig = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			if (orig.getWidth() == width && orig.getHeight() == height) {
				bmp = orig;
			} else {
				AImageScaler scaler;
				
				// TODO : centerCrop scaler
				switch (scaleType) {
					case CENTER_INSIDE:
						scaler = new DownScaler();
						break;
						
					case FIT_CENTER:
					case FIT_START:
					case FIT_END:
					case FIT_XY:
						scaler = new BidirectionalScaler();
						break;
						
					case CENTER:
					case MATRIX:
					default:
						scaler = null;
						bmp = orig;
						break;
				}
				
				if (scaler != null) {
					bmp = scaler.scale(bmp, minWidth, minHeight);
				}
			}
		}
		return bmp;
	}
}