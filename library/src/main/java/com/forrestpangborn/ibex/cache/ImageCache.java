package com.forrestpangborn.ibex.cache;

public interface ImageCache {

	public void put(String key, byte[] data);
	public byte[] get(String key);
	public void close();
}