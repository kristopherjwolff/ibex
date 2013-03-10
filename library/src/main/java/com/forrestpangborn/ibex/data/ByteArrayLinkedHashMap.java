package com.forrestpangborn.ibex.data;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ByteArrayLinkedHashMap extends LinkedHashMap<String, byte[]> {
	
	public static interface OnByteArrayMapEntryRemovedListener {
		public void onByteArrayMapEntryRemoved(Entry<String, byte[]> entry);
	}
	
	private static final long serialVersionUID = -1475773059875033959L;
	
	private final int maxSize;
	
	private OnByteArrayMapEntryRemovedListener listener;
	private int currentSize;
	
	public ByteArrayLinkedHashMap(int initialCapacity, int maxSize, OnByteArrayMapEntryRemovedListener listener) {
		super(initialCapacity, 0.75f, true);
		this.maxSize = maxSize;
		this.listener = listener;
	}
	
	@Override
	public byte[] put(String key, byte[] value) {
		if (value.length <= maxSize) {
			currentSize += value.length;
			return super.put(key, value);
		}
		return null;
	}
	
	@Override
	protected boolean removeEldestEntry(Entry<String, byte[]> entry) {
		if (currentSize > maxSize) {
			currentSize -= entry.getValue().length;
			if (listener != null) {
				listener.onByteArrayMapEntryRemoved(entry);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void clear() {
		super.clear();
		currentSize = 0;
	}
	
	@Override
	public byte[] remove(Object key) {
		byte[] ret = super.remove(key);
		if (ret != null) {
			currentSize -= ret.length;
		}
		return ret;
	}
	
	public int getCurrentSize() {
		return currentSize;
	}
}