package com.forrestpangborn.ibex.cache;

import static com.forrestpangborn.ibex.util.DataUtils.buildByteArray;

import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

public class DiskImageCache implements ImageCache {

	private static final long CACHE_SIZE = 3000000L;
	
	private DiskLruCache cache;
	
	public DiskImageCache(Context context) {
		int versionCode = -1;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			cache = DiskLruCache.open(context.getCacheDir(), versionCode, 1, CACHE_SIZE);
		} catch (NameNotFoundException ex) {
			// this will not happen.
		} catch (IOException ex) {
			Log.e("Ibex", "IOException attempting to open DiskLruCache!", ex);
		}
	}
	
	@Override
	public void put(String key, byte[] data) {
		if (data != null && key != null) {
			try {
				Editor editor = cache.edit(key);
				OutputStream os = editor.newOutputStream(0);
				os.write(data);
				editor.commit();
			} catch (IOException ex) {
				// nada!
			}
		}
	}

	@Override
	public byte[] get(String key) {
		byte[] ret = null;
		
		try {
			Snapshot snapshot = cache.get(key);
			if (snapshot != null) {
				ret = buildByteArray(snapshot.getInputStream(0));
			}
		} catch (IOException ex) {
			ret = null;
		}
		
		return ret;
	}

	@Override
	public void close() {
		try {
			cache.close();
		} catch (IOException ex) {
			Log.e("Ibex", "Exception trying to close DiskLruCache!", ex);
		}
		
	}
}