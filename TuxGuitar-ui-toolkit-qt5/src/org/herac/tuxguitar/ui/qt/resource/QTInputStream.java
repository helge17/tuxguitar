package org.herac.tuxguitar.ui.qt.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.qtjambi.qt.core.QBuffer;
import org.qtjambi.qt.core.QByteArray;
import org.qtjambi.qt.core.QIODevice;

public class QTInputStream extends QBuffer {
	
	public QTInputStream(QByteArray data) {
		super(data);
		
		this.open(QIODevice.OpenModeFlag.ReadOnly);
	}
	
	public QTInputStream(byte[] data) {
		this(new QByteArray(data));
	}
	
	public QTInputStream(InputStream stream) {
		this(toByteArray(stream));
	}
	
	private static byte[] toByteArray(InputStream in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			int read = 0;
			while((read = in.read()) != -1){
				out.write(read);
			}
			byte[] bytes = out.toByteArray();
			in.close();
			out.close();
			out.flush();
			
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
