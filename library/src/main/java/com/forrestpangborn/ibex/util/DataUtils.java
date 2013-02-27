package com.forrestpangborn.ibex.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataUtils {

	public static String buildMD5Hash(String s) {
		String ret = null;
		if (s != null) {
			MessageDigest md = null;
	        try {
	        	md = MessageDigest.getInstance("MD5");
	        } catch (NoSuchAlgorithmException nsae) {
	        	// this will not happen.
	        }
	        byte[] array = md.digest(s.getBytes());
	        BigInteger bigInt = new BigInteger(1, array);
	        ret = bigInt.toString(16);
		}
		return ret;
	}
	
	public static byte[] buildByteArray(InputStream stream) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int b;
		while ((b = stream.read()) != -1) {
			bos.write(b);
		}
		return bos.toByteArray();
	}
}