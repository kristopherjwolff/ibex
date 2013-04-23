package com.forrestpangborn.ibex.cache;

import com.forrestpangborn.ibex.data.Request;

public interface ImageCache {

	public void put(Request request, byte[] data);
	public byte[] get(Request request);
	public void close();
}