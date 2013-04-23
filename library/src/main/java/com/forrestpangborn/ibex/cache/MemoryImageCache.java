package com.forrestpangborn.ibex.cache;

import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap;
import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap.OnByteArrayMapEntryRemovedListener;
import com.forrestpangborn.ibex.data.Request;

public class MemoryImageCache implements ImageCache {

	private ByteArrayLinkedHashMap map;
	
	public MemoryImageCache(int initialCapacity, int maxSize, OnByteArrayMapEntryRemovedListener listener) {
		map = new ByteArrayLinkedHashMap(initialCapacity, maxSize, listener);
	}
	
	public MemoryImageCache(int initialCapacity, int maxSize) {
		this(initialCapacity, maxSize, null);
	}
	
	@Override
	public void put(Request request, byte[] value) {
		map.put(request.key, value);
	}
	
	@Override
	public byte[] get(Request request) {
		return map.get(request.key);
	}

	@Override
	public void close() {}
}