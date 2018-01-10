package com.teknei.bid.crypto;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {


	public static final String ALGORITHM = "RSA/ECB/PKCS1Padding";
	
	/**
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public KeyPair buildKeyPair() throws NoSuchAlgorithmException {
		final int keySize = 2048;
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keySize);
		return keyPairGenerator.genKeyPair();
	}
	
	/**
	 * 
	 * @param text
	 * @param key
	 * @return
	 */
	public byte[] encrypt(String text, byte [] key) {
	    byte[] cipherText = null;
	    try {
	      final Cipher cipher = Cipher.getInstance(ALGORITHM);
	      KeyFactory kf = KeyFactory.getInstance("RSA");
	      PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(key));
	      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	      cipherText = cipher.doFinal(text.getBytes());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return cipherText;
	  }

	/**
	 * 
	 * @param text
	 * @param key
	 * @return
	 */
	  public String decrypt(byte[] text, byte [] key) {
	    byte[] dectyptedText = null;
	    try {
	    	
	      final Cipher cipher = Cipher.getInstance(ALGORITHM);
	      KeyFactory kf = KeyFactory.getInstance("RSA");
	      PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(key));

	      cipher.init(Cipher.DECRYPT_MODE, privateKey);
	      dectyptedText = cipher.doFinal(text);

	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }

	    return new String(dectyptedText);
	  }
	  
	  /**
	   * 
	   * @param e
	   * @param m
	   * @return
	   */
	public PublicKey bigIntegerToPublicKey(BigInteger e, BigInteger m)  {
	    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
	    try {
	    	KeyFactory fact = KeyFactory.getInstance("RSA");
	    	PublicKey pubKey = (PublicKey)fact.generatePublic(keySpec);
	    	return pubKey;
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    	return null;
	    }
	}

	/**
	 * 
	 * @param d
	 * @param m
	 * @return
	 */
	public PrivateKey bigIntegerToPrivateKey(BigInteger d, BigInteger m) {
	    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, d);
	    try {
	    	KeyFactory fact = KeyFactory.getInstance("RSA");
	    	PrivateKey privKey = fact.generatePrivate(keySpec);
	    	return privKey;
	    }catch(Exception ex) {
	    	ex.printStackTrace();
	    	return null;
	    }
	}
}
