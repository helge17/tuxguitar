package org.herac.tuxguitar.gui.tools.browser.ftp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Base64Decoder {
	
	private static final int BUFFER_SIZE = 1024;
	
	private static int get1(byte buf[], int off) {
		return ((buf[off] & 0x3f) << 2) | ((buf[off + 1] & 0x30) >>> 4);
	}
	
	private static int get2(byte buf[], int off) {
		return ((buf[off + 1] & 0x0f) << 4) | ((buf[off + 2] & 0x3c) >>> 2);
	}
	
	private static int get3(byte buf[], int off) {
		return ((buf[off + 2] & 0x03) << 6) | (buf[off + 3] & 0x3f);
	}
	
	private static int check(int ch) {
		if ((ch >= 'A') && (ch <= 'Z')) {
			return ch - 'A';
		} else if ((ch >= 'a') && (ch <= 'z')) {
			return ch - 'a' + 26;
		} else if ((ch >= '0') && (ch <= '9')) {
			return ch - '0' + 52;
		} else {
			switch (ch) {
				case '=':
					return 65;
				case '+':
					return 62;
				case '/':
					return 63;
				default:
					return -1;
			}
		}
	}
	
	public static byte[] decode(byte[] bytes){
		try{
			ByteArrayInputStream  in  = new ByteArrayInputStream( bytes );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			byte buffer[] = new byte[BUFFER_SIZE];
			byte chunk[] = new byte[4];
			int got = -1;
			int ready = 0;
			
			fill: while ((got = in.read(buffer)) > 0) {
				int skiped = 0;
				
				while (skiped < got) {
					
					while (ready < 4) {
						if (skiped >= got){ 
							continue fill;
						}
						int ch = check(buffer[skiped++]);
						if (ch >= 0) {
							chunk[ready++] = (byte) ch;
						}
					}
					
					if (chunk[2] == 65) {
						out.write(get1(chunk, 0));
						return out.toByteArray();
					} 
					else if (chunk[3] == 65) {
						out.write(get1(chunk, 0));
						out.write(get2(chunk, 0));
						return out.toByteArray();
					} 
					else {
						out.write(get1(chunk, 0));
						out.write(get2(chunk, 0));
						out.write(get3(chunk, 0));
					}
					ready = 0;
				}
			}
			if (ready == 0){
				out.flush();
				return out.toByteArray();
			}
			
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		
		return bytes;
	}
}