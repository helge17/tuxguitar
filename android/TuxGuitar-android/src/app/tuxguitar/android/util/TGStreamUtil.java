package app.tuxguitar.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TGStreamUtil {

	public static InputStream getInputStream(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		TGStreamUtil.write(in, out);

		return new ByteArrayInputStream(out.toByteArray());
	}

	public static void write(InputStream in, OutputStream out) throws IOException {
		int read = 0;
		byte[] buffer = new byte[8192];
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
		in.close();
		out.close();
		out.flush();
	}
}
