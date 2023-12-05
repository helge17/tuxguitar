package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TGDriveBrowserUtil {
	
	public static InputStream getInputStream(InputStream in) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		TGDriveBrowserUtil.write(in, out);
		
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public static void write(InputStream in, OutputStream out) throws Throwable {
		int read = 0;
		while((read = in.read()) != -1){
			out.write(read);
		}
		in.close();
		out.close();
		out.flush();
	}
}
