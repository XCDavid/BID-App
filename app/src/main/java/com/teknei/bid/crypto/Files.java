package com.teknei.bid.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import com.teknei.bid.tools.Base64;

public class Files {

	/**
	 * 
	 * @param key
	 * @param path
	 * @param nameFile
	 * @return
	 */
	public boolean createFileKey(byte key [], String path, String nameFile) {
		
	        FileWriter fichero = null;
	        PrintWriter pw = null;
	        try
	        {
	            fichero = new FileWriter(path + nameFile);
	            pw = new PrintWriter(fichero);
	            String asB64 = Base64.encode(key);
	            pw.println(asB64);
	            
	            return true;
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        } finally {
	           try {
	           if (null != fichero)
	              fichero.close();
	           } catch (Exception e2) {
	              e2.printStackTrace();
	           }
	        }
	}
	
	/**
	 * 
	 * @param pathKeyPub
	 * @return
	 */
	public byte [] readFileKey(String pathKeyPub) {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			
			archivo = new File (pathKeyPub);
			fr = new FileReader (archivo);
			br = new BufferedReader(fr);
			
			StringBuffer buffer = new StringBuffer();
			String linea;
			while((linea=br.readLine())!=null) {
				buffer.append(linea);
			}
			
            byte[] asBytes = Base64.decode(buffer.toString());
			return asBytes;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			try{                    
				if( null != fr ){   
					fr.close();     
				}                  
			}catch (Exception e2){ 
				e2.printStackTrace();
			}
		}

	}
	
	

}
