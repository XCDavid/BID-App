package com.teknei.bid.services;

import android.os.Environment;

import com.teknei.bid.crypto.Rules;
import com.teknei.bid.crypto.Util;

public class CipherFingerServices {

	private final static String pathKey = Environment.getExternalStorageDirectory() + "/.morpho/k_pub.key";

	public final static byte[] cipherFinger(String operationId, byte[] imgFinger){
		if(imgFinger != null) {
			Util util = new Util();
			Rules rule = new Rules();

			util.emptyBuffer();
			util.setLenBuffer(imgFinger.length + 3);
			util.addBuffer(imgFinger);

			byte[] info = util.getBuffer().array();

			byte[] cipheredFinger = rule.generate(info, operationId, "1", "1", pathKey);

			return cipheredFinger;

		}else {
			System.out.println("No se pueden encriptar valores nulos");

			return null;
		}
	}
}
