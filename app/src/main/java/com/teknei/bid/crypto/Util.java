package com.teknei.bid.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Util {
	
	final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	private ByteBuffer buffer;
	private int lenBuffer;
	
	ByteBuffer data;

	public Util() {
			
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public String asciiToHex(String text) {
		byte r[] = text.getBytes(); 
		StringBuilder sb = new StringBuilder();
		for (byte b : r) 
		{
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}


	/**
	 * 
	 * @param title
	 * @param bb
	 */
	public void printByteBuffer(String title, ByteBuffer bb) {

		System.out.println(title+" -> ");
		for(int i=0; i < bb.capacity(); i++) {
			System.out.print(String.format("%02X",bb.get(i)) + " ");
		}
		System.out.println();
	}

	/**
	 * 
	 * @param res
	 */
	public void printResponseAPDU(int res) {
		System.out.println("SW -> "+String.format("%02X",res));
	}

	
	/**
	 * 
	 * @param title
	 * @param ba
	 */
	public void printByteArray(String title, byte [] ba) {

		System.out.print("\n"+title+" -> ");
		for(int i=0; i < ba.length; i++) {
			System.out.print(String.format("%02X",ba[i]));
		}
		System.out.println();
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public String addPadding(String text) {
		char c= 0x00;
		int l = text.length();
		if (l<16) {
			for(int i = 0;i<(16-l);i++) {
				text = text.concat(String.valueOf(c));
			}
		}
		return text;
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public byte[] calculatePadding8byte(int size){

		int mod = size % 8;

		if(mod>0){
			mod = 8-mod;
		}
		//-System.out.println("MOD "+mod);   
		byte [] padding = new byte[mod];
		for(int i=0; i<mod; i++) {
			padding[i]=(byte)0x00;
		}

		return padding;
	}

	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public byte[] calculatePadding16byte(int size){

		int mod = size % 16;
		if(mod>0){
			mod = 16-mod;
			byte [] padding = new byte[mod];
			for(int i=0; i<mod; i++) {
				padding[i]=(byte)0x00;
			}
			return padding;
		}
		//-System.out.println("MOD "+mod);   
		return null;
	}

	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] toByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len/2];

		for(int i = 0; i < len; i+=2){
			data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		}

		return data;
	}
	
	/**
	 * 
	 * @param decipher
	 * @param fields
	 * @return
	 */
	public int [] cutBuffer(byte [] decipher, int fields) {
		
		int [] cut =  new int[fields];
		int j = 0;
		for(int i = 0; i<decipher.length; i++){
			
			if(decipher[i] == 0x20 && decipher[i+1] == 0x20 && decipher[i+2] == 0x20){	
				//-System.out.println("**** "+i);
				cut[j] = i;
				j++;
			}
		}
		return cut;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStringDate(){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String date  = dateFormat.format(new Date());
		return date;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public boolean setLenBuffer(int size) {
		buffer = ByteBuffer.allocate(size);
		return true;
	}

	/**
	 * 
	 * @param array
	 * @return
	 */
	public boolean addBuffer(byte [] array) {
		byte [] pass = {0x20,0x20,0x20};
		buffer.put(array);
		buffer.put(pass);
		lenBuffer = lenBuffer + array.length;
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean emptyBuffer() {
		
		buffer = null;
		return true;
	}
	
	
}
