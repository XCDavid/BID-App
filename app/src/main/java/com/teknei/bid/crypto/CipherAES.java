
package com.teknei.bid.crypto;

import java.security.SecureRandom;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 *
 * @author XXX
 */
public class CipherAES {


	private final static String algorithm = "AES"; 
	private final static String cI = "AES/CBC/NoPadding";

/**
 * 
 * @param idTran
 * @return
 */
	public byte [] generateKey (String idTran) {

		try {
			Calendar calendar = Calendar.getInstance();
			java.util.Date now = calendar.getTime();
			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

			SecureRandom rand = new SecureRandom();
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			rand.setSeed(idTran.getBytes());
			rand.setSeed(currentTimestamp.getTime());
			rand.setSeed(generator.generateKey().getEncoded());

			byte key[] = new byte[16];
			rand.nextBytes(key);
			return key;

		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param key
	 * @param encrypted
	 * @return
	 */
	public byte [] decrypt(byte [] key, byte [] encrypted)
	{
		byte[] decrypted = null; 
		try
		{
			byte [] iv= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			Cipher cipher = Cipher.getInstance(cI);
			SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
			decrypted = cipher.doFinal(encrypted);
		}
		catch(Exception e)
		{
			System.out.println("Error. No se pudo descifrar información con algoritmo AES");
		}
		return decrypted;
	}


	/**
	 * 
	 * @param key
	 * @param cleartext
	 * @return
	 */
	public byte[] encrypt(byte [] key, byte [] cleartext)
	{
		byte[] encrypted = null;
		byte [] iv= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		try
		{

			Cipher cipher = Cipher.getInstance(cI);
			SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
			encrypted = cipher.doFinal(cleartext);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error. No se pudo cifrar información con algoritmo AES");
		}

		return encrypted;
	}


}
