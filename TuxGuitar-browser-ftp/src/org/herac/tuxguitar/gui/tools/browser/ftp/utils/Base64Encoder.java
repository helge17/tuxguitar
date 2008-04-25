package org.herac.tuxguitar.gui.tools.browser.ftp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Encoder {
	
	private static final int BUFFER_SIZE = 1024;
	
	private static byte ENCODING[] = { 
		(byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H',
		(byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P',
		(byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',
		(byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
		(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
		(byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v',
		(byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
		(byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/',
		(byte) '='
	};
	
	private static int get1(byte buf[], int off) {
		return (buf[off] & 0xfc) >> 2;
	}
	
	private static int get2(byte buf[], int off) {
		return ((buf[off] & 0x3) << 4) | ((buf[off + 1] & 0xf0) >>> 4);
	}
	
	private static int get3(byte buf[], int off) {
		return ((buf[off + 1] & 0x0f) << 2) | ((buf[off + 2] & 0xc0) >>> 6);
	}
	
	private static int get4(byte buf[], int off) {
		return buf[off + 2] & 0x3f;
	}
	
	public static byte[] encode(byte[] bytes) {
		try{
			
			ByteArrayInputStream  in  = new ByteArrayInputStream( bytes );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte buffer[] = new byte[BUFFER_SIZE];
			int got = -1;
			int off = 0;
			int count = 0;
			while ((got = in.read(buffer, off, BUFFER_SIZE - off)) > 0) {
				if ((got + off) >= 3) {
					got += off;
					off = 0;
					while (off + 3 <= got) {
						int c1 = get1(buffer, off);
						int c2 = get2(buffer, off);
						int c3 = get3(buffer, off);
						int c4 = get4(buffer, off);
						switch (count) {
							case 73:
								out.write(ENCODING[c1]);
								out.write(ENCODING[c2]);
								out.write(ENCODING[c3]);
								out.write('\n');
								out.write(ENCODING[c4]);
								count = 1;
								break;
							case 74:
								out.write(ENCODING[c1]);
								out.write(ENCODING[c2]);
								out.write('\n');
								out.write(ENCODING[c3]);
								out.write(ENCODING[c4]);
								count = 2;
								break;
							case 75:
								out.write(ENCODING[c1]);
								out.write('\n');
								out.write(ENCODING[c2]);
								out.write(ENCODING[c3]);
								out.write(ENCODING[c4]);
								count = 3;
								break;
							case 76:
								out.write('\n');
								out.write(ENCODING[c1]);
								out.write(ENCODING[c2]);
								out.write(ENCODING[c3]);
								out.write(ENCODING[c4]);
								count = 4;
								break;
							default:
								out.write(ENCODING[c1]);
								out.write(ENCODING[c2]);
								out.write(ENCODING[c3]);
								out.write(ENCODING[c4]);
								count += 4;
								break;
						}
						off += 3;
					}
					
					for (int i = 0; i < 3; i++){
						buffer[i] = (i < got - off) ? buffer[off + i] : ((byte) 0);
					}
					off = got - off;
				} else {
					off += got;
				}
			}
			
			switch (off) {
				case 1:
					out.write(ENCODING[get1(buffer, 0)]);
					out.write(ENCODING[get2(buffer, 0)]);
					out.write('=');
					out.write('=');
					break;
				case 2:
					out.write(ENCODING[get1(buffer, 0)]);
					out.write(ENCODING[get2(buffer, 0)]);
					out.write(ENCODING[get3(buffer, 0)]);
					out.write('=');
			}
			
			return out.toByteArray();
			
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		
		return bytes;
	}
}