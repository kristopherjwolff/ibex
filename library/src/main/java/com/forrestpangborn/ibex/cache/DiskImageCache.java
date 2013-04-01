package com.forrestpangborn.ibex.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.common.io.ByteStreams;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

public class DiskImageCache implements ImageCache {
	
	private DiskLruCache cache;
	
	public DiskImageCache(Context context, File path, long size) {
		int versionCode = -1;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			cache = DiskLruCache.open(path, versionCode, 1, size);
		} catch (NameNotFoundException ex) {
			// TODO : this will not happen????
		} catch (IOException ex) {
			Log.e("Ibex", "IOException attempting to open DiskLruCache!", ex);
		}
	}
	
	public DiskImageCache(Context context, long size) {
		this(context, context.getCacheDir(), size);
	}
	
	@Override
	public void put(String key, byte[] data) {
		if (data != null && key != null) {
			try {
				Editor editor = cache.edit(key);
				if (editor != null) {
					OutputStream output = editor.newOutputStream(0);
					output.write(data);
					editor.commit();
					output.close();
				}
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
				InputStream input = snapshot.getInputStream(0);
				ret = ByteStreams.toByteArray(input);
				input.close();
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