
package com.teknei.bid.crypto;

public class AES_CMAC {
    
private void leftShiftOneBit(byte []  input, byte [] output)
{
  byte overflow = 0;

  for (int i=15; i>=0; i-- ) 
  {
    output[i] =( byte )(input[i] << 1);
    output[i] =(byte)(output[i] | overflow);
    int a =(input[i] & 0x80);
    overflow = ( a != 0)?(byte)1: (byte)0; 
  } 
}



private void xor128(byte []a, byte []b,  byte []out)
{
  for (int i=0;i<16; i++)
    out[i] = (byte)(a[i] ^ b[i]);
}



private void padding ( byte [] lastb, byte [] pad, int length )
{
  for ( int j=0; j<16; j++ ) 
  {
    if ( j < length ) 
      pad[j] = lastb[j];
    else 
      if ( j == length ) 
        pad[j] = (byte)(0x80);
      else 
        pad[j] = 0x00;
  }
}


    
private void generateSubkey(byte [] L, byte [] K1, byte [] K2)
{
  byte []  tmp = new byte[16];  
  byte [] const_Rb = 
  {
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0x87
  };    
    
  if ( (L[0] & 0x80) == 0 ) 
    leftShiftOneBit(L,K1);
  else 
  {    /* Else K1 = ( L << 1 ) (+) Rb */
    leftShiftOneBit(L,tmp);
    xor128(tmp,const_Rb,K1);
  }

  if ( (K1[0] & 0x80) == 0 ) 
    leftShiftOneBit(K1,K2);
  else 
  {
    leftShiftOneBit(K1,tmp);
    xor128(tmp,const_Rb,K2);
  }
}            
    
public  byte [] calculateCMAC(byte [] key, byte [] input)
{
  if(key.length != 16)
   return null;
  

  byte [] iv= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

  CipherAES aes= new CipherAES();  
  byte [] crypto_key = aes.encrypt(key, iv);
  if(crypto_key == null)
    return null;
  
  byte [] K1 = new byte[16];
  byte [] K2 = new byte[16];
  generateSubkey(crypto_key, K1, K2);
  
  int length = 16;
  int n, flag;    
  n = (length+15) / 16;     

  if ( n == 0 ) 
  {
    n = 1;
    flag = 0;
  } 
  else 
  {
    if ( (length%16) == 0 ) 
      flag = 1;
    else 
      flag = 0;  
  }

  byte [] M_last = new byte[16];
  byte [] padded = new byte[16];
    
  if ( flag == 1) 
  { 
    xor128(input,K1,M_last);
  } 
  else 
  {
    padding(input,padded,length%16);
    xor128(padded,K2,M_last);
  }

  byte []X= new byte[16];
  byte []Y = new byte[16];
    
  for (int  i=0; i<16; i++ ) 
    X[i] = 0;

  xor128(X,M_last,Y);
    
  byte [] mac = aes.encrypt(key , Y);
  if( mac == null)
    return null;
  
  return mac;
 
}
    
}
