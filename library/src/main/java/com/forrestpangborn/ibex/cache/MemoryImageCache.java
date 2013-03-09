package com.forrestpangborn.ibex.cache;

import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap;
import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap.OnByteArrayMapEntryRemovedListener;

public class MemoryImageCache implements ImageCache {

	private ByteArrayLinkedHashMap map;
	
	public MemoryImageCache(int maxSize, OnByteArrayMapEntryRemovedListener listener) {
		map = new ByteArrayLinkedHashMap(maxSize, listener);
	}
	
	public MemoryImageCache(int maxSize) {
		this(maxSize, null);
	}
	
	@Override
	public void put(String key, byte[] value) {
		map.put(key, value);
	}
	
	public byte[] get(String key) {
		return map.get(key);
	}

	@Override
	public void close() {}
}