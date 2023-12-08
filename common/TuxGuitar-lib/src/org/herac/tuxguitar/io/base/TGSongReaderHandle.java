package org.herac.tuxguitar.io.base;

import java.io.InputStream;

public class TGSongReaderHandle extends TGSongPersistenceHandle {

	private InputStream inputStream;

	public TGSongReaderHandle() {
		super();
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void setInputStream(InputStream is) {
		this.inputStream = is;
	}
}
