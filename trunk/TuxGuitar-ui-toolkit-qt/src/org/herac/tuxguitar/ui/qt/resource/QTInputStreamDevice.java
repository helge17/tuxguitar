package org.herac.tuxguitar.ui.qt.resource;

import java.io.IOException;
import java.io.InputStream;

import com.trolltech.qt.core.QIODevice;

public class QTInputStreamDevice extends QIODevice { 
	
	private InputStream stream;

	public QTInputStreamDevice(InputStream stream) {
		this.stream = stream;
		
		this.open(QIODevice.OpenModeFlag.ReadOnly);
	}

	@Override
	protected int readData(byte[] buffer) {
		try {
			return this.stream.read(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected int writeData(byte[] arg0) {
		return 0;
	};
}
