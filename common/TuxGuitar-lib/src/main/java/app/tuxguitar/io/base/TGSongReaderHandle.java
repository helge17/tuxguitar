package app.tuxguitar.io.base;

import java.io.InputStream;

public class TGSongReaderHandle extends TGSongPersistenceHandle {

	private InputStream inputStream;

	// when set, this attribute means a newer file format version was detected when decoding inputStream
	// (to suggest user to upgrade)
	private boolean newerFileFormatDetected = false;

	public TGSongReaderHandle() {
		super();
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void setInputStream(InputStream is) {
		this.inputStream = is;
	}

	public boolean isNewerFileFormatDetected() {
		return this.newerFileFormatDetected;
	}

	public void setNewerFileFormatDetected(boolean value) {
		this.newerFileFormatDetected = value;
	}
}
