package org.herac.tuxguitar.io.base;

import java.io.OutputStream;

public class TGSongWriterHandle extends TGSongPersistenceHandle {
	
	private OutputStream outputStream;
	
	public TGSongWriterHandle(){
		super();
	}

	public OutputStream getOutputStream() {
		return this.outputStream;
	}

	public void setOutputStream(OutputStream os) {
		this.outputStream = os;
	}
}
