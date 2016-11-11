package org.herac.tuxguitar.android.storage.saf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TGSafStreamUtil {

	public static InputStream getInputStream(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		TGSafStreamUtil.write(in, out);

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static void write(InputStream in, OutputStream out) throws IOException {
		int read = 0;
		while((read = in.read()) != -1){
			out.write(read);
		}
		in.close();
		out.close();
		out.flush();
	}
}
