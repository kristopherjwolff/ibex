package com.forrestpangborn.ibex.data;

import java.util.Map.Entry;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.forrestpangborn.ibex.data.ByteArrayLinkedHashMap.OnByteArrayMapEntryRemovedListener;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ByteArrayLinkedHashMapTest extends TestCase {

	private static final int SIZE = 50;
	
	private ByteArrayLinkedHashMap map;
	private OnByteArrayMapEntryRemovedListener listener;
	private Entry<String, byte[]> removedEntry;

	@Override @Before
	public void setUp() throws Exception {
		super.setUp();
		listener = new OnByteArrayMapEntryRemovedListener() {
			@Override
			public void onByteArrayMapEntryRemoved(Entry<String, byte[]> entry) {
				removedEntry = entry;
			}
		};
		map = new ByteArrayLinkedHashMap(10, SIZE, listener);
	}
	
	@Override @After
	public void tearDown() throws Exception {
		super.tearDown();
		map = null;
		listener = null;
		removedEntry = null;
	}
	
	@Test
	public void testRemovalListener() {
		byte[] arr1 = new byte[35];
		byte[] arr2 = new byte[25];
		
		assertNull(removedEntry);
		
		map.put("1", arr1);
		assertNull(removedEntry);
		
		map.put("2", arr2);
		assertNotNull(removedEntry);
		assertEquals(arr1, removedEntry.getValue());
		assertEquals(25, map.getCurrentSize());
	}
	
	@Test
	public void testPutEntryExceedsMaxSize() {
		byte arr[] = new byte[55];
		map.put("1", arr);
		
		assertNull(map.get("1"));
		assertEquals(0, map.getCurrentSize());
	}
	
	@Test
	public void testEldestRemoved() {
		byte[] arr1 = new byte[25];
		byte[] arr2 = new byte[25];
		byte[] arr3 = new byte[25];
		
		assertNull(removedEntry);
		
		map.put("1", arr1);
		assertNull(removedEntry);
		
		map.put("2", arr2);
		assertNull(removedEntry);
		
		map.get("1");
		assertNull(removedEntry);
		
		map.put("3", arr3);
		assertEquals(arr2, removedEntry.getValue());
		assertNull(map.get("2"));
	}
	
	@Test
	public void testPutEntryValidSize() {
		assertNull(map.get("1"));
		byte[] arr = new byte[1];
		map.put("1", arr);
		assertEquals(arr, map.get("1"));
		assertEquals(1, map.getCurrentSize());
	}
	
	@Test
	public void testMultipleValidPuts() {
		byte[] arr1 = new byte[1];
		byte[] arr2 = new byte[1];
		byte[] arr3 = new byte[1];
		
		assertNull(map.get("1"));
		assertNull(map.get("2"));
		assertNull(map.get("3"));
		
		map.put("1", arr1);
		assertEquals(arr1, map.get("1"));
		assertNull(map.get("2"));
		assertNull(map.get("3"));
		assertEquals(1, map.getCurrentSize());
		
		map.put("2", arr2);
		assertEquals(arr1, map.get("1"));
		assertEquals(arr2, map.get("2"));
		assertNull(map.get("3"));
		assertEquals(2, map.getCurrentSize());
		
		map.put("3", arr3);
		assertEquals(arr1, map.get("1"));
		assertEquals(arr2, map.get("2"));
		assertEquals(arr3, map.get("3"));
		assertEquals(3, map.getCurrentSize());
	}
	
	@Test
	public void testValidPutFollowingInvalidPut() {
		byte[] arr1 = new byte[55];
		byte[] arr2 = new byte[5];
		
		assertNull(map.get("1"));
		assertNull(map.get("2"));
		
		map.put("1", arr1);
		assertNull(map.get("1"));
		assertNull(map.get("2"));
		assertEquals(0, map.getCurrentSize());
		
		map.put("2", arr2);
		assertNull(map.get("1"));
		assertEquals(arr2, map.get("2"));
		assertEquals(5, map.getCurrentSize());
	}

	
	@Test
	public void testClear() {
		byte[] arr1 = new byte[25];
		byte[] arr2 = new byte[20];
		map.put("1", arr1);
		map.put("2", arr2);
		
		assertEquals(45, map.getCurrentSize());
		assertNotNull(map.get("1"));
		assertNotNull(map.get("2"));
		
		map.clear();
		assertEquals(0, map.getCurrentSize());
	}
	
	@Test
	public void testRemove() {
		byte[] arr1 = new byte[25];
		byte[] arr2 = new byte[20];
		map.put("1", arr1);
		map.put("2", arr2);
		
		assertEquals(45, map.getCurrentSize());
		assertNotNull(map.get("1"));
		assertNotNull(map.get("2"));
		
		byte[] remove1 = map.remove("1");
		assertEquals(20, map.getCurrentSize());
		assertEquals(arr1, remove1);
		assertNull(map.get("1"));
		assertNotNull(map.get("2"));
		
		byte[] remove2 = map.remove("2");
		assertEquals(0, map.getCurrentSize());
		assertEquals(arr2, remove2);
		assertNull(map.get("1"));
		assertNull(map.get("2"));
	}
	
	@Test
	public void removeNonExistent() {
		byte[] remove1 = map.remove("1");
		assertNull(remove1);
		assertNull(map.get("1"));
	}
}