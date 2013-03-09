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
	
	public ByteArrayLinkedHashMap(int maxSize, OnByteArrayMapEntryRemovedListener listener) {
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
			if (listener != null) {
				listener.onByteArrayMapEntryRemoved(entry);
			}
			return true;
		}
		return false;
	}
}