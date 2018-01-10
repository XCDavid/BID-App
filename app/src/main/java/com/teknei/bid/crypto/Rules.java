package com.teknei.bid.crypto;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.teknei.bid.tools.Base64;
import com.teknei.bid.crypto.Files;

public class Rules {

	protected Util util = new Util();
	private RSA rsa = new RSA();
	private CipherAES cipher = new CipherAES();
	private AES_CMAC mac = new AES_CMAC();
	private Files file = new Files();

	/**
	 * 
	 * @param info
	 * @param idTran
	 * @param step
	 * @param numObject
	 * @param pathKeyPut
	 * @return
	 */
	public byte[] generate (byte [] info, String idTran, String step, String numObject, String pathKeyPut ) {

		try {

			ByteBuffer data;

			//--Generar llave de sesión por el momento con solo dos parámetros o SALT
			byte [] keySession = cipher.generateKey(idTran);
			//-util.printByteArray("KEY SESSION ",keySession);

			byte [] padding = util.calculatePadding16byte(info.length);
			int lDataComplete = info.length;
			if(padding != null) { 
				lDataComplete = lDataComplete + padding.length;
				data = ByteBuffer.allocate(lDataComplete);
				data.put(info);
				data.put(padding);
			}{
				data = ByteBuffer.allocate(lDataComplete);
				data.put(info);
			}

			//-- Información a cifrar con padding multiplos 16 bytes
			//-util.printByteArray("DATA ",data.array());

			//--- Calcular cmac de toda la información(Biometría) usando llave de sesión
			byte [] cmac = mac.calculateCMAC(keySession, data.array());
			//-util.printByteArray("CMAC", cmac);

			//-- Información en plano(Biometría) con el cmac adjunto
			ByteBuffer dataWithCMAC = ByteBuffer.allocate( data.array().length + cmac.length);
			dataWithCMAC.put(data.array());
			dataWithCMAC.put(cmac);

			//-- Encriptar(AES 128) información(Biometría) con cmac usando llave de sesión
			byte [] cipherSymmetric = cipher.encrypt(keySession, dataWithCMAC.array());
			//-util.printByteArray("Cipher AES", cipherSymmetric);

			//-- Leer archivo de llave pública (RSA 2048 bits)
			byte [] kpairPublic = file.readFileKey( pathKeyPut );
			//-util.printByteArray("Public RSA", kpairPublic);

			//-byte [] kpairPrivate = file.readFileKey( path , nameKPri);
			//-util.printByteArray("Private RSA", kpairPrivate);

			//-- Pasar a Base64 la llave de sesión
			String kSession64 = Base64.encode(keySession);
			keySession = null; //--Limpiar

			//-- Usando la llave pública, solo cifrar datos del cliente,transacción y session key 
			/* Máximo 245 caracteres por uso de la librería */
			String plain = kSession64 + "|" + idTran + "|"+ step + "|" + numObject +"|";

			//-System.out.println("\nDATA -->"+ plain);

			kSession64 = null; //--Limpiar 
			//-util.printByteArray("Data complete", plain.getBytes());		


			//-- Cifrar con llave pública la llave de sesión en BAse64 y los datos del cliente | equipo
			byte c [] = rsa.encrypt(plain, kpairPublic);
			//-util.printByteArray("Cifrado RSA", c);

			//-- La información encriptada debe viajar junta (cifrado AES y cifrado RSA)
			ByteBuffer cipherComplete = ByteBuffer.allocate(c.length + cipherSymmetric.length);
			cipherComplete.put(c);
			cipherComplete.put(cipherSymmetric);

			//-- Datos cifrados
			//-util.printByteArray("Cifrado Completo", cipherComplete.array());

			return cipherComplete.array();

		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	/**
	 * 
	 * @param chain
	 * @param keyPri
	 * @return
	 */
	public List<byte []> decryptChain(byte [] chain, byte [] keyPri){

		if(chain == null || chain.length < 257){
			System.out.println("La trama no cumple las condiciones");
			return null;
		}


		try {

			byte [] cipherRSA = Arrays.copyOfRange(chain, 0, 256);
			byte [] cipherAES = Arrays.copyOfRange(chain, 256, chain.length);

			String x = rsa.decrypt(cipherRSA,  keyPri);
			//-System.out.println("Descifrado RSA " + x);

			String[] parts = x.split("\\|");
			String keySessionDecipher = parts[0]; 	//-- key session
			String objects = parts[3];				//-- Número de objetos

			int numObjects = Integer.parseInt(objects);

			byte[] keySessionD = Base64.decode(keySessionDecipher.getBytes());
			util.printByteArray("session", keySessionD);

			byte [] decipherSymmetric = cipher.decrypt(keySessionD , cipherAES);

			byte [] cmacDecipher = Arrays.copyOfRange(decipherSymmetric, decipherSymmetric.length -16,decipherSymmetric.length);
			byte [] dataD = Arrays.copyOfRange(decipherSymmetric, 0 ,decipherSymmetric.length -16);
			byte [] cmacD = mac.calculateCMAC(keySessionD, dataD);

			if(Arrays.equals(cmacDecipher, cmacD)) {
				System.out.println("Excelente !!!");
				return segmentar(decipherSymmetric, numObjects);

			}else {
				System.out.println("Error !!");
				return null;
			}

		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}



	/**
	 * 
	 * @param des
	 * @param numObjetos
	 * @return
	 */
	private List<byte []> segmentar(byte des [], int numObjetos){

		int [] val = util.cutBuffer(des, numObjetos);
		List<byte []> arrays = new ArrayList<byte []>();

		for (int i = 0; i < numObjetos; i++){
			if(i == 0){
				arrays.add( Arrays.copyOfRange(des, 0, val[i]) );
			}
			else{
				arrays.add( Arrays.copyOfRange(des, val[i-1]+3, val[i]) );
			}	
		}

		return arrays;

	}
}
