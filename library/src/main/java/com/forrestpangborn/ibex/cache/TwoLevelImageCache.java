package com.forrestpangborn.ibex.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap;
import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap.OnByteArrayMapEntryRemovedListener;
import com.google.common.io.ByteStreams;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

public class TwoLevelImageCache implements ImageCache, OnByteArrayMapEntryRemovedListener {

	private ByteArrayLinkedHashMap memoryCache;
	private DiskLruCache diskCache;
	
	public TwoLevelImageCache(Context context, int memoryCacheSize, long diskCacheSize) {
		memoryCache = new ByteArrayLinkedHashMap(memoryCacheSize, this);
		int versionCode = -1;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			diskCache = DiskLruCache.open(context.getCacheDir(), versionCode, 1, diskCacheSize);
		} catch (NameNotFoundException ex) {
			// TODO : this will not happen????
		} catch (IOException ex) {
			Log.e("Ibex", "IOException attempting to open DiskLruCache!", ex);
		}
	}

	@Override
	public void put(String key, byte[] data) {
		if (memoryCache.put(key, data) == null) {
			try {
				Editor editor = diskCache.edit(key);
				if (editor != null) {
					OutputStream output = editor.newOutputStream(0);
					output.write(data);
					output.close();
					editor.commit();
				}
			} catch (IOException ex) {
				// nada!
			}
		}
	}

	@Override
	public byte[] get(String key) {
		byte[] memoryData = null;
		byte[] diskData = null;
		
		memoryData = memoryCache.get(key);
		
		if (memoryData == null) {
			try {
				Snapshot snapshot = diskCache.get(key);
				if (snapshot != null) {
					InputStream input = snapshot.getInputStream(0);
					diskData = ByteStreams.toByteArray(input);
					input.close();
				}
			} catch (IOException ex) {
				diskData = null;
			}
		}
		
		if (diskData != null) {
			try {
				if (memoryCache.put(key, diskData) != null) {
					diskCache.remove(key);
				}
			} catch (IOException ex) {
				// fail silently?
			}
		}
		
		return memoryData != null ? memoryData : diskData;
	}

	@Override
	public void close() {
		try {
			diskCache.close();
		} catch (IOException ex) {
			Log.e("Ibex", "Exception trying to close DiskLruCache!", ex);
		}
	}

	@Override
	public void onByteArrayMapEntryRemoved(Entry<String, byte[]> entry) {
		String key = entry.getKey();
		byte[] data = entry.getValue();
		if (data != null && key != null) {
			try {
				Editor editor = diskCache.edit(key);
				if (editor != null) {
					OutputStream output = editor.newOutputStream(0);
					output.write(data);
					output.close();
					editor.commit();
				}
			} catch (IOException ex) {
				// nada!
			}
		}
	}
}